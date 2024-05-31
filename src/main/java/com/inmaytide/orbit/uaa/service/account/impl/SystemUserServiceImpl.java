package com.inmaytide.orbit.uaa.service.account.impl;

import com.inmaytide.exception.web.ObjectNotFoundException;
import com.inmaytide.orbit.commons.business.SystemUserService;
import com.inmaytide.orbit.commons.constants.Bool;
import com.inmaytide.orbit.commons.constants.Constants;
import com.inmaytide.orbit.commons.domain.*;
import com.inmaytide.orbit.uaa.configuration.ErrorCode;
import com.inmaytide.orbit.uaa.consts.RoleAssociationCategory;
import com.inmaytide.orbit.uaa.consts.UserAssociationCategory;
import com.inmaytide.orbit.uaa.domain.account.User;
import com.inmaytide.orbit.uaa.domain.account.UserAssociation;
import com.inmaytide.orbit.uaa.domain.permission.RoleAssociation;
import com.inmaytide.orbit.uaa.service.account.OrganizationService;
import com.inmaytide.orbit.uaa.service.account.PositionService;
import com.inmaytide.orbit.uaa.service.account.UserAssociationService;
import com.inmaytide.orbit.uaa.service.account.UserService;
import com.inmaytide.orbit.uaa.service.permission.AuthorityService;
import com.inmaytide.orbit.uaa.service.permission.RoleAssociationService;
import com.inmaytide.orbit.uaa.service.permission.RoleService;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.Serializable;
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

    private final RoleAssociationService roleAssociationService;

    private final AuthorityService authorityService;

    public SystemUserServiceImpl(UserService userService, UserAssociationService userAssociationService, OrganizationService organizationService, PositionService positionService, RoleService roleService, RoleAssociationService roleAssociationService, AuthorityService authorityService) {
        this.userService = userService;
        this.userAssociationService = userAssociationService;
        this.organizationService = organizationService;
        this.positionService = positionService;
        this.roleService = roleService;
        this.roleAssociationService = roleAssociationService;
        this.authorityService = authorityService;
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
        setOrganizations(systemUser);
        setPositions(systemUser);
        setRoles(systemUser);
        setPermissions(systemUser);
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

    private void setRoles(SystemUser user) {
        List<Long> ids = userAssociationService
                .findByUserAndCategory(user.getId(), UserAssociationCategory.ROLE)
                .stream()
                .map(UserAssociation::getAssociated)
                .toList();
        List<Role> roles = roleService.findByIds(ids).stream().map(e -> createObjectCopiedFields(e, Role::new)).toList();
        user.setRoles(roles);
    }

    private void setPermissions(SystemUser user) {
        Permission permission = new Permission();
        List<Long> roleIds = user.getRoles().stream().map(Role::getId).toList();
        permission.setRoles(user.getRoles().stream().map(Role::getCode).toList());
        permission.setAuthorities(authorityService.findCodesByRoleIds(roleIds));
        permission.setOrganizations(roleAssociationService.findByRolesAndCategory(roleIds, RoleAssociationCategory.ORGANIZATION).stream().map(RoleAssociation::getAssociated).toList());
        permission.setSpecifiedOrganizations(permission.getOrganizations());
        permission.setAreas(roleAssociationService.findByRolesAndCategory(roleIds, RoleAssociationCategory.AREA).stream().map(RoleAssociation::getAssociated).toList());
        permission.setSpecifiedAreas(permission.getAreas());
        user.setPermission(permission);
    }

}
