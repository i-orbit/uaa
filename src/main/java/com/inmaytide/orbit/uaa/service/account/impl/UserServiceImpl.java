package com.inmaytide.orbit.uaa.service.account.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.inmaytide.exception.web.ObjectNotFoundException;
import com.inmaytide.orbit.commons.business.impl.BasicServiceImpl;
import com.inmaytide.orbit.commons.constants.Is;
import com.inmaytide.orbit.commons.domain.Perspective;
import com.inmaytide.orbit.commons.domain.SystemUser;
import com.inmaytide.orbit.uaa.consts.UserAssociationCategory;
import com.inmaytide.orbit.uaa.domain.account.User;
import com.inmaytide.orbit.uaa.domain.account.UserAssociation;
import com.inmaytide.orbit.uaa.service.account.UserAssociationService;
import com.inmaytide.orbit.uaa.service.account.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.Serializable;
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

    private final UserAssociationService userAssociationService;

    public UserServiceImpl(UserAssociationService userAssociationService) {
        this.userAssociationService = userAssociationService;
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
