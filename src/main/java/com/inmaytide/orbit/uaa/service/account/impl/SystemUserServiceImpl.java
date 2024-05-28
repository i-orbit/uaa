package com.inmaytide.orbit.uaa.service.account.impl;

import com.inmaytide.exception.web.ObjectNotFoundException;
import com.inmaytide.orbit.commons.business.SystemUserService;
import com.inmaytide.orbit.commons.constants.Bool;
import com.inmaytide.orbit.commons.constants.Constants;
import com.inmaytide.orbit.commons.domain.Perspective;
import com.inmaytide.orbit.commons.domain.Robot;
import com.inmaytide.orbit.commons.domain.SystemUser;
import com.inmaytide.orbit.commons.domain.pattern.Entity;
import com.inmaytide.orbit.uaa.configuration.ErrorCode;
import com.inmaytide.orbit.uaa.consts.UserAssociationCategory;
import com.inmaytide.orbit.uaa.domain.account.User;
import com.inmaytide.orbit.uaa.domain.account.UserAssociation;
import com.inmaytide.orbit.uaa.service.account.UserAssociationService;
import com.inmaytide.orbit.uaa.service.account.UserService;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * @author inmaytide
 * @since 2024/4/26
 */
@Primary
@Service
public class SystemUserServiceImpl implements SystemUserService {

    private final UserService userService;

    private final UserAssociationService userAssociationService;

    public SystemUserServiceImpl(UserService userService, UserAssociationService userAssociationService) {
        this.userService = userService;
        this.userAssociationService = userAssociationService;
    }

    @Override
    @Cacheable(cacheNames = Constants.CacheNames.USER_DETAILS, key = "#id")
    public SystemUser get(Serializable id) {
        if (Objects.equals(id, Robot.getInstance().getId())) {
            return Robot.getInstance().toSystemUser();
        }
        if (!NumberUtils.isCreatable(Objects.toString(id, ""))) {
            throw new ObjectNotFoundException(ErrorCode.E_0x00100002, Objects.toString(id, "null"));
        }
        User user = userService.get(NumberUtils.createLong(Objects.toString(id))).orElseThrow(() -> new ObjectNotFoundException(ErrorCode.E_0x00100002, Objects.toString(id, "null")));
        return transferUserToSystemUser(user);
    }

    private SystemUser transferUserToSystemUser(User user) {
        Objects.requireNonNull(user);
        SystemUser systemUser = new SystemUser();
        BeanUtils.copyProperties(user, systemUser);
        // 加载用户所属组织
        List<UserAssociation> associations = userAssociationService.findByUserAndCategory(user.getId(), UserAssociationCategory.ORGANIZATION);
        systemUser.setUnderOrganizations(user.getOrganizations().stream().map(Entity::getId).toList());
        systemUser.setDefaultUnderOrganization(associations.stream().filter(e -> e.getDefaulted() == Bool.Y).findFirst().map(UserAssociation::getAssociated).orElse(null));
        // 加载数据权限
        Perspective perspective = new Perspective();
        systemUser.setPerspective(perspective);
        return systemUser;
    }

}
