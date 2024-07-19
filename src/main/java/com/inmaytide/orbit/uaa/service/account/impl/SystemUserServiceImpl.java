package com.inmaytide.orbit.uaa.service.account.impl;

import com.inmaytide.exception.web.ObjectNotFoundException;
import com.inmaytide.orbit.commons.business.SystemUserService;
import com.inmaytide.orbit.commons.constants.Bool;
import com.inmaytide.orbit.commons.constants.Constants;
import com.inmaytide.orbit.commons.constants.Roles;
import com.inmaytide.orbit.commons.domain.*;
import com.inmaytide.orbit.uaa.configuration.ApplicationProperties;
import com.inmaytide.orbit.uaa.configuration.ErrorCode;
import com.inmaytide.orbit.uaa.consts.UserAssociationCategory;
import com.inmaytide.orbit.uaa.domain.account.User;
import com.inmaytide.orbit.uaa.domain.account.UserAssociation;
import com.inmaytide.orbit.uaa.service.account.PositionService;
import com.inmaytide.orbit.uaa.service.account.UserAssociationService;
import com.inmaytide.orbit.uaa.service.account.UserService;
import com.inmaytide.orbit.uaa.service.permission.AreaService;
import com.inmaytide.orbit.uaa.service.permission.AuthorityService;
import com.inmaytide.orbit.uaa.service.permission.OrganizationService;
import com.inmaytide.orbit.uaa.service.permission.RoleService;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author inmaytide
 * @since 2024/4/26
 */
@Primary
@Service
public class SystemUserServiceImpl implements SystemUserService {

    private final UserService userService;

    private final UserAssociationService userAssociationService;

    private final OrganizationService organizationService;

    private final PositionService positionService;

    private final RoleService roleService;

    private final AuthorityService authorityService;

    private final ApplicationProperties properties;

    private final AreaService areaService;

    public SystemUserServiceImpl(UserService userService, UserAssociationService userAssociationService, OrganizationService organizationService, PositionService positionService, RoleService roleService, AuthorityService authorityService, ApplicationProperties properties, AreaService areaService) {
        this.userService = userService;
        this.userAssociationService = userAssociationService;
        this.organizationService = organizationService;
        this.positionService = positionService;
        this.roleService = roleService;
        this.authorityService = authorityService;
        this.properties = properties;
        this.areaService = areaService;
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
        setRoles(systemUser, user.getIsTenantAdministrator());
        setPermissions(systemUser);
        setOrganizations(systemUser);
        setPositions(systemUser);
        return systemUser;
    }

    private <T> T createObjectCopiedFields(Object source, Supplier<T> supplier) {
        T t = supplier.get();
        BeanUtils.copyProperties(source, t);
        return t;
    }

    private void setOrganizations(SystemUser user) {
        List<UserAssociation> associations = userAssociationService.findByUserAndCategory(user.getId(), UserAssociationCategory.ORGANIZATION);
        List<Organization> organizations = organizationService
                .findByIds(associations.stream().map(UserAssociation::getAssociated).toList())
                .stream()
                .map(e -> createObjectCopiedFields(e, Organization::new))
                .toList();
        user.setOrganizations(organizations);
        associations.stream()
                .filter(e -> e.getDefaulted() == Bool.Y)
                .findFirst()
                .flatMap(e -> organizations.stream().filter(o -> Objects.equals(o.getId(), e.getAssociated())).findFirst())
                .ifPresent(o -> {
                    user.setOrganizationId(o.getId());
                    user.setOrganizationName(o.getName());
                });

    }

    private void setPositions(SystemUser user) {
        List<UserAssociation> associations = userAssociationService.findByUserAndCategory(user.getId(), UserAssociationCategory.POSITION);
        List<Position> positions = positionService
                .findByIds(associations.stream().map(UserAssociation::getAssociated).toList())
                .stream()
                .map(e -> createObjectCopiedFields(e, Position::new))
                .toList();
        user.setPositions(positions);
        associations.stream()
                .filter(e -> e.getDefaulted() == Bool.Y)
                .findFirst()
                .flatMap(e -> positions.stream().filter(o -> Objects.equals(o.getId(), e.getAssociated())).findFirst())
                .ifPresent(o -> {
                    user.setPositionId(o.getId());
                    user.setPositionName(o.getName());
                });
    }

    private void setRoles(SystemUser user, Bool isTenantAdministrator) {
        List<Long> ids = userAssociationService
                .findByUserAndCategory(user.getId(), UserAssociationCategory.ROLE)
                .stream()
                .map(UserAssociation::getAssociated)
                .toList();
        List<Role> roles = new ArrayList<>(roleService.findByIds(ids).stream().map(e -> createObjectCopiedFields(e, Role::new)).toList());
        // 系统是否启用了超级管理员&&用户的登录名和配置指定的超级管理员用户名是否匹配
        if (properties.isEnableSuperAdministrator() && properties.getSuperAdministratorLoginNames().contains(user.getLoginName())) {
            roles.add(new Role(Roles.ROLE_S_ADMINISTRATOR.name(), "超级管理员"));
        }
        // 用户是否是租户管理员
        if (isTenantAdministrator == Bool.Y) {
            roles.add(new Role(Roles.ROLE_T_ADMINISTRATOR.name(), "租户管理员"));
        }
        // 用户是否为配置的系统内部机器人
        if (Objects.equals(user.getId(), Robot.getInstance().getId())) {
            roles.add(new Role(Roles.ROLE_ROBOT.name(), "系统内部机器人"));
        }
        user.setRoles(roles);
    }

    private void setPermissions(SystemUser user) {
        Permission permission = new Permission();
        permission.setRoles(user.getRoles().stream().map(Role::getCode).toList());
        permission.setAuthorities(authorityService.findCodesBySystemUser(user));
        permission.setOrganizations(organizationService.findAuthorizedIds(user));
        permission.setSpecifiedOrganizations(permission.getOrganizations());
        permission.setAreas(areaService.findAuthorizedIds(user));
        permission.setSpecifiedAreas(permission.getAreas());
        user.setPermission(permission);
    }

}
