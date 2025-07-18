package com.inmaytide.orbit.uaa.service.account.impl;

import com.inmaytide.exception.web.AccessDeniedException;
import com.inmaytide.exception.web.BadRequestException;
import com.inmaytide.orbit.commons.constants.Constants;
import com.inmaytide.orbit.commons.constants.UserStatus;
import com.inmaytide.orbit.commons.domain.Message;
import com.inmaytide.orbit.commons.domain.SystemUser;
import com.inmaytide.orbit.commons.domain.dto.result.AffectedResult;
import com.inmaytide.orbit.commons.security.SecurityUtils;
import com.inmaytide.orbit.commons.service.core.SystemPropertyService;
import com.inmaytide.orbit.commons.service.message.MessageService;
import com.inmaytide.orbit.commons.utils.CodecUtils;
import com.inmaytide.orbit.commons.utils.CommonUtils;
import com.inmaytide.orbit.commons.utils.ValueCaches;
import com.inmaytide.orbit.uaa.configuration.ApplicationProperties;
import com.inmaytide.orbit.uaa.configuration.ErrorCode;
import com.inmaytide.orbit.uaa.consts.UserDefaultPasswordMode;
import com.inmaytide.orbit.uaa.domain.account.User;
import com.inmaytide.orbit.uaa.mapper.account.UserMapper;
import com.inmaytide.orbit.uaa.service.account.UserPasswordService;
import com.inmaytide.orbit.uaa.service.account.UserService;
import com.inmaytide.orbit.uaa.service.account.dto.ApplyVerificationCode;
import com.inmaytide.orbit.uaa.service.account.dto.ChangePassword;
import com.inmaytide.orbit.uaa.service.account.dto.ResetPassword;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author inmaytide
 * @since 2024/2/26
 */
@Service
public class UserPasswordServiceImpl implements UserPasswordService {

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    private final ApplicationProperties properties;

    private final SystemPropertyService propertyService;

    private final MessageService messageService;

    private final CacheManager cacheManager;

    /**
     * 不能直接注入，避免循环依赖
     */
    private UserService userService;

    public UserPasswordServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder, ApplicationProperties properties, SystemPropertyService propertyService, MessageService messageService, CacheManager cacheManager) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.properties = properties;
        this.propertyService = propertyService;
        this.messageService = messageService;
        this.cacheManager = cacheManager;
    }

    private void eraseUserCache(List<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        Cache cache = cacheManager.getCache(Constants.CacheNames.USER_DETAILS);
        if (cache != null) {
            ids.forEach(cache::evictIfPresent);
        }
    }

    private AffectedResult change(User user, ChangePassword dto) {
        // 新密码和确认密码输入不一致
        if (!Objects.equals(dto.getNewPassword(), dto.getConfirmPassword())) {
            throw new BadRequestException(ErrorCode.E_0x00100010);
        }
        // 新密码不满足安全要求
        if (!REG_PASSWORD.asPredicate().test(dto.getNewPassword())) {
            throw new BadRequestException(ErrorCode.E_0x00100011);
        }
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        if (user.getStatus() == UserStatus.INITIALIZATION) {
            user.setStatus(UserStatus.NORMAL);
            user.setStatusTime(Instant.now());
        }
        user.setPasswordExpireAt(getPasswordExpireAt(user.getTenant()));
        user.setUpdatedBy(user.getId());
        user.setUpdatedAt(Instant.now());
        userMapper.updateById(user);
        eraseUserCache(List.of(user.getId()));
        return AffectedResult.withAffected(1);
    }

    @Override
    public AffectedResult change(ChangePassword dto) {
        User user = getUserService().findByLoginName(dto.getLoginName()).orElseThrow(() -> new BadRequestException(ErrorCode.E_0x00100002, dto.getLoginName()));
        // 将修改密码的账号与当前登录账号不一致
        if (!Objects.equals(SecurityUtils.getAuthorizedUser().getId(), user.getId())) {
            throw new AccessDeniedException(ErrorCode.E_0x00100006);
        }
        // 原密码为空或输入错误
        if (StringUtils.isBlank(dto.getOriginalPassword()) || !passwordEncoder.matches(dto.getOriginalPassword(), user.getPassword())) {
            throw new BadRequestException(ErrorCode.E_0x00100009);
        }
        return change(user, dto);
    }

    @Override
    public AffectedResult changeWithValidationCode(ChangePassword dto) {
        if (StringUtils.isBlank(dto.getVerificationCode())) {
            throw new BadRequestException(ErrorCode.E_0x00100013);
        }
        User user = getUserService().findByLoginName(dto.getLoginName()).orElseThrow(() -> new BadRequestException(ErrorCode.E_0x00100002, dto.getLoginName()));
        String validationCode = ValueCaches.getAndDelete("USER_CHANGE_PASSWORD::VALIDATION_CODE", String.valueOf(user.getId())).orElse(StringUtils.EMPTY);
        if (!StringUtils.equals(validationCode, dto.getVerificationCode())) {
            throw new BadRequestException(ErrorCode.E_0x00100013);
        }
        return change(user, dto);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public AffectedResult reset(ResetPassword dto) {
        if (CollectionUtils.isEmpty(dto.getUserIds())) {
            return AffectedResult.NOT_AFFECTED;
        }
        boolean isSuperAdministrator = SecurityUtils.isSuperAdministrator();
        SystemUser authorizedUser = SecurityUtils.getAuthorizedUser();
        List<User> users = userService.findByIds(dto.getUserIds());
        for (User user : users) {
            // 非超级管理员且修改的用户与操作人不在同一租户下
            if (!isSuperAdministrator && Objects.equals(user.getTenant(), authorizedUser.getTenant())) {
                throw new AccessDeniedException(ErrorCode.E_0x00100006);
            }
            user.setPassword(generateDefaultPassword(user));
            user.setPasswordExpireAt(getPasswordExpireAt(user.getTenant()));
            user.setStatus(UserStatus.INITIALIZATION);
            user.setStatusTime(Instant.now());
        }
        users.forEach(userMapper::updateById);
        eraseUserCache(CommonUtils.map(users, Function.identity(), User::getId));
        return AffectedResult.withAffected(users.size());
    }

    @Override
    public String getEncryptedPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public String generateDefaultPassword(User entity) {
        ApplicationProperties.UserDefaultPassword config = properties.getUserDefaultPassword();
        String value = null;
        if (UserDefaultPasswordMode.RANDOM == config.getMode()) {
            value = CodecUtils.generateRandomCode(8);
        } else if (UserDefaultPasswordMode.SPECIFIED == config.getMode()) {
            if (StringUtils.isBlank(config.getSpecified())) {
                throw new BadRequestException();
            }
            value = config.getSpecified();
        } else if (UserDefaultPasswordMode.TEL_LAST6 == config.getMode()) {
            if (StringUtils.isBlank(entity.getTelephoneNumber()) || entity.getTelephoneNumber().length() < 11) {
                throw new BadRequestException();
            }
            value = StringUtils.substring(entity.getTelephoneNumber(), entity.getTelephoneNumber().length() - 6);
        }
        return getEncryptedPassword(value);
    }

    @Override
    public Instant getPasswordExpireAt(String tenant) {
        Integer value = propertyService.getIntValue(tenant, Constants.SystemPropertyKeys.USER_PASSWORD_VALID_IN_DAYS).orElse(180);
        return LocalDate.now().plusDays(value).atStartOfDay().toInstant(ZoneOffset.ofHours(8));
    }

    @Override
    public void applyVerificationCode(ApplyVerificationCode dto) {
        // 根据用户名查询对应用户信息，若用户信息不存在返回“申请重置密码的验证码失败”错误，模糊相关安全信息
        User user = getUserService().findByLoginName(dto.getLoginName()).orElseThrow(() -> new BadRequestException(ErrorCode.E_0x00100014));
        String value = CodecUtils.generateRandomCode(8);
        Message message = Message.builder()
                .title("重置密码")
                .content("<span style=\"font-size: 16px;\">尊敬的<b>%s</b>您好：<br/><p style=\"text-indent: 2em;\">账号重置密码的验证码为：<b>%s</b>（有效时间为<b>15</b>分钟）</p></span>".formatted(user.getName(), value))
                .business(UserService.BUSINESS_KEY)
                .receiver(dto.getSendingMode(), user.getId())
                .build();
        messageService.createMessage(message);
    }

    @Lazy
    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public UserService getUserService() {
        return userService;
    }

}
