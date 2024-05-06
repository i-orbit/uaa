package com.inmaytide.orbit.uaa.service.account.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.inmaytide.exception.web.AccessDeniedException;
import com.inmaytide.exception.web.ObjectNotFoundException;
import com.inmaytide.orbit.commons.business.impl.BasicServiceImpl;
import com.inmaytide.orbit.commons.constants.Bool;
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
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author inmaytide
 * @since 2024/1/19
 */
@Service
public class UserServiceImpl extends BasicServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;

    private final UserPasswordService passwordService;

    public UserServiceImpl(UserMapper userMapper, UserPasswordService passwordService) {
        this.userMapper = userMapper;
        this.passwordService = passwordService;
    }

    @Override
    public Optional<User> get(Long id) {
        if (Objects.equals(Robot.getInstance().getId(), id)) {
            User robot = new User();
            robot.setId(id);
            robot.setName(Robot.getInstance().getName());
            robot.setPassword(Robot.getInstance().getPassword());
            robot.setLoginName(Robot.getInstance().getLoginName());
            return Optional.of(robot);
        }
        return super.get(id);
    }

    @Override
    public User create(User entity) {
        // 只有超级管理员和对应租户的租户管理员允许新建租户管理员
        if (entity.getIsTenantAdministrator() == Bool.Y) {
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
        User original = baseMapper.selectById(entity.getId());
        if (original == null) {
            throw new ObjectNotFoundException(String.valueOf(entity.getId()));
        }
        BeanUtils.copyProperties(entity, original, "state", "stateTime", "password", "passwordExpireAt", "sequence");
        return super.update(original);
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
    public Map<Long, String> findEmailsByIds(List<Long> ids) {
        return findFieldValueByIds(ids, User::getEmail);
    }

    @Override
    public Map<Long, String> findTelephoneNumbersByIds(List<Long> ids) {
        return findFieldValueByIds(ids, User::getTelephoneNumber);
    }

    @Override
    public Map<Long, String> findNamesByIds(List<Long> ids) {
        return findFieldValueByIds(ids, User::getName);
    }

    private Map<Long, String> findFieldValueByIds(List<Long> ids, SFunction<User, String> fieldGetter) {
        if (CollectionUtils.isEmpty(ids)) {
            return Map.of();
        }
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(fieldGetter, User::getId);
        wrapper.in(User::getId, ids);
        return baseMapper.selectList(wrapper).stream().collect(Collectors.toMap(User::getId, fieldGetter));
    }

}
