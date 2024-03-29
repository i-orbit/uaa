package com.inmaytide.orbit.uaa.service.account.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.inmaytide.exception.web.AccessDeniedException;
import com.inmaytide.exception.web.ObjectNotFoundException;
import com.inmaytide.orbit.commons.business.impl.BasicServiceImpl;
import com.inmaytide.orbit.commons.constants.Is;
import com.inmaytide.orbit.commons.constants.Languages;
import com.inmaytide.orbit.commons.constants.UserState;
import com.inmaytide.orbit.commons.domain.Perspective;
import com.inmaytide.orbit.commons.domain.Robot;
import com.inmaytide.orbit.commons.domain.SystemUser;
import com.inmaytide.orbit.commons.security.SecurityUtils;
import com.inmaytide.orbit.uaa.configuration.ErrorCode;
import com.inmaytide.orbit.uaa.consts.UserAssociationCategory;
import com.inmaytide.orbit.uaa.domain.account.User;
import com.inmaytide.orbit.uaa.domain.account.UserAssociation;
import com.inmaytide.orbit.uaa.mapper.account.UserMapper;
import com.inmaytide.orbit.uaa.service.account.UserAssociationService;
import com.inmaytide.orbit.uaa.service.account.UserPasswordService;
import com.inmaytide.orbit.uaa.service.account.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author inmaytide
 * @since 2024/1/19
 */
@Primary
@Service
public class UserServiceImpl extends BasicServiceImpl<User> implements UserService {

    private final UserMapper userMapper;

    private final UserAssociationService userAssociationService;

    private final UserPasswordService passwordService;

    public UserServiceImpl(UserMapper userMapper, UserAssociationService userAssociationService, UserPasswordService passwordService) {
        this.userMapper = userMapper;
        this.userAssociationService = userAssociationService;
        this.passwordService = passwordService;
    }

    @Override
    public User create(User entity) {
        // 只有超级管理员和对应租户的租户管理员允许新建租户管理员
        if (entity.getIsTenantAdministrator() == Is.Y) {
            if (!SecurityUtils.isSuperAdministrator() && !SecurityUtils.isTenantAdministrator(entity.getTenant())) {
                throw new AccessDeniedException(ErrorCode.E_0x00100006);
            }
        }
        entity.setState(UserState.INITIALIZATION);
        entity.setStateTime(Instant.now());
        entity.setPassword(passwordService.generateDefaultPassword(entity));
        entity.setPasswordExpireAt(passwordService.getPasswordExpireAt(entity.getTenant()));
        entity.setLang(Languages.SIMPLIFIED_CHINESE);
        entity.setSequence(userMapper.findNewSequence());
        return super.create(entity);
    }

    @Override
    public User update(User entity) {


        return super.update(entity);
    }

    @Override
    public Optional<User> findByLoginName(String loginName) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.or().eq(User::getLoginName, loginName)
                .or().eq(User::getTelephoneNumber, loginName)
                .or().eq(User::getEmail, loginName)
                .or().eq(User::getEmployeeId, loginName);
        return Optional.ofNullable(baseMapper.selectOne(wrapper));
    }

    @Override
    public SystemUser get(Serializable id) {
        if (Objects.equals(id, Robot.getInstance().getId())) {
            return Robot.getInstance().toSystemUser();
        }
        User user = baseMapper.selectById(id);
        if (user == null) {
            throw new ObjectNotFoundException(String.valueOf(id));
        }
        return transferUserToSystemUser(user);
    }

    private SystemUser transferUserToSystemUser(User user) {
        Objects.requireNonNull(user);
        SystemUser systemUser = new SystemUser();
        BeanUtils.copyProperties(user, systemUser);
        // 加载用户所属组织
        List<UserAssociation> associations = userAssociationService.findByUserAndCategory(user.getId(), UserAssociationCategory.ORGANIZATION);
        systemUser.setUnderOrganizations(associations.stream().map(UserAssociation::getAssociated).collect(Collectors.toList()));
        systemUser.setDefaultUnderOrganization(associations.stream().filter(e -> e.getDefaulted() == Is.Y).findFirst().map(UserAssociation::getAssociated).orElse(null));
        // 加载数据权限
        Perspective perspective = new Perspective();
        systemUser.setPerspective(perspective);
        return systemUser;
    }

}
