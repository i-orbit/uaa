package com.inmaytide.orbit.uaa.service.account.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.inmaytide.exception.web.BadRequestException;
import com.inmaytide.exception.web.ObjectNotFoundException;
import com.inmaytide.orbit.commons.business.impl.BasicServiceImpl;
import com.inmaytide.orbit.commons.domain.SystemUser;
import com.inmaytide.orbit.uaa.configuration.ErrorCode;
import com.inmaytide.orbit.uaa.domain.account.User;
import com.inmaytide.orbit.uaa.service.account.UserService;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

/**
 * @author inmaytide
 * @since 2024/1/19
 */
@Service
public class UserServiceImpl extends BasicServiceImpl<User> implements UserService {

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
        User user = baseMapper.selectById(id);
        if (user == null) {
            throw new ObjectNotFoundException(String.valueOf(id));
        }
        return transferUserToSystemUser(user);
    }

    @Override
    public SystemUser getByLoginName(String loginName) {
        return findByLoginName(loginName)
                .map(this::transferUserToSystemUser)
                .orElseThrow(() -> new BadRequestException(ErrorCode.E_0x00100002, loginName));
    }

    private SystemUser transferUserToSystemUser(User user) {
        Objects.requireNonNull(user);
        SystemUser systemUser = new SystemUser();


        return systemUser;
    }
}
