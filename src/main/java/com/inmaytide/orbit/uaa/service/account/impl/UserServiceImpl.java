package com.inmaytide.orbit.uaa.service.account.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inmaytide.exception.web.AccessDeniedException;
import com.inmaytide.exception.web.ObjectNotFoundException;
import com.inmaytide.orbit.commons.constants.Bool;
import com.inmaytide.orbit.commons.constants.Languages;
import com.inmaytide.orbit.commons.constants.TenantStatus;
import com.inmaytide.orbit.commons.constants.UserStatus;
import com.inmaytide.orbit.commons.domain.Robot;
import com.inmaytide.orbit.commons.domain.SystemUser;
import com.inmaytide.orbit.commons.domain.pattern.Entity;
import com.inmaytide.orbit.commons.security.SecurityUtils;
import com.inmaytide.orbit.commons.service.core.DictionaryService;
import com.inmaytide.orbit.commons.utils.CommonUtils;
import com.inmaytide.orbit.uaa.configuration.ErrorCode;
import com.inmaytide.orbit.uaa.consts.UserAssociationCategory;
import com.inmaytide.orbit.uaa.domain.account.Position;
import com.inmaytide.orbit.uaa.domain.account.User;
import com.inmaytide.orbit.uaa.domain.account.UserAssociation;
import com.inmaytide.orbit.uaa.domain.permission.Organization;
import com.inmaytide.orbit.uaa.domain.permission.Role;
import com.inmaytide.orbit.uaa.domain.permission.Tenant;
import com.inmaytide.orbit.uaa.mapper.account.UserMapper;
import com.inmaytide.orbit.uaa.service.account.PositionService;
import com.inmaytide.orbit.uaa.service.account.UserAssociationService;
import com.inmaytide.orbit.uaa.service.account.UserPasswordService;
import com.inmaytide.orbit.uaa.service.account.UserService;
import com.inmaytide.orbit.uaa.service.permission.OrganizationService;
import com.inmaytide.orbit.uaa.service.permission.RoleService;
import com.inmaytide.orbit.uaa.service.permission.TenantService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author inmaytide
 * @since 2024/1/19
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper baseMapper;

    private final UserPasswordService passwordService;

    private final UserAssociationService associationService;

    private final OrganizationService organizationService;

    private final PositionService positionService;

    private final RoleService roleService;

    private final DictionaryService dictionaryService;

    private final TenantService tenantService;

    public UserServiceImpl(UserMapper userMapper, UserPasswordService passwordService, UserAssociationService associationService, OrganizationService organizationService, PositionService positionService, RoleService roleService, DictionaryService dictionaryService, TenantService tenantService) {
        this.baseMapper = userMapper;
        this.passwordService = passwordService;
        this.associationService = associationService;
        this.organizationService = organizationService;
        this.positionService = positionService;
        this.roleService = roleService;
        this.dictionaryService = dictionaryService;
        this.tenantService = tenantService;
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
        return UserService.super.get(id);
    }

    @Override
    public BaseMapper<User> getBaseMapper() {
        return baseMapper;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public User create(User entity) {
        // 只有超级管理员和对应租户的租户管理员允许新建租户管理员
        if (entity.getIsTenantAdministrator() == Bool.Y) {
            if (!SecurityUtils.isSuperAdministrator() && !SecurityUtils.isTenantAdministrator(entity.getTenant())) {
                throw new AccessDeniedException(ErrorCode.E_0x00100006);
            }
        }
        SystemUser authorizedUser = SecurityUtils.getAuthorizedUser();
        if (entity.getTenant() == null) {
            entity.setTenant(authorizedUser.getTenant());
        }
        TenantStatus tenantStatus = tenantService.get(entity.getId()).map(Tenant::getStatus).orElse(TenantStatus.DISABLED);
        if (tenantStatus != TenantStatus.NORMAL) {
            throw new AccessDeniedException(ErrorCode.E_0x00100020);
        }
        entity.setStatus(UserStatus.INITIALIZATION);
        entity.setStateTime(Instant.now());
        entity.setPassword(passwordService.generateDefaultPassword(entity));
        entity.setPasswordExpireAt(passwordService.getPasswordExpireAt(entity.getTenant()));
        entity.setLang(Languages.SIMPLIFIED_CHINESE);
        entity.setSequence(baseMapper.findNewSequence());
        baseMapper.insert(entity);
        // 保存关联信息
        entity.setId(entity.getId());
        associationService.persist(entity);
        return get(entity.getId()).orElseThrow(ObjectNotFoundException::new);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public User update(User entity) {
        User original = baseMapper.selectById(entity.getId());
        if (original == null) {
            throw new ObjectNotFoundException(String.valueOf(entity.getId()));
        }
        BeanUtils.copyProperties(entity, original, "state", "stateTime", "password", "passwordExpireAt", "sequence");
        // 保存关联信息
        associationService.persist(entity);
        baseMapper.updateById(original);
        return get(entity.getId()).orElseThrow(ObjectNotFoundException::new);
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

    @Override
    public void setExtraFields(Collection<User> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return;
        }
        List<Long> ids = CommonUtils.map(entities, Function.identity(), User::getId);
        Map<Long, Map<UserAssociationCategory, List<UserAssociation>>> associations = associationService.findByUsers(ids);
        BiFunction<Collection<Map<UserAssociationCategory, List<UserAssociation>>>, UserAssociationCategory, List<Long>> fetchIds = (p, c) -> p.stream()
                .flatMap(e -> e.getOrDefault(c, Collections.emptyList()).stream())
                .map(UserAssociation::getAssociated)
                .toList();
        List<Long> organizationIds = fetchIds.apply(associations.values(), UserAssociationCategory.ORGANIZATION);
        Map<Long, Organization> organizations = organizationService.findByIds(organizationIds).stream().collect(Collectors.toMap(Entity::getId, Function.identity()));
        List<Long> positionsIds = fetchIds.apply(associations.values(), UserAssociationCategory.POSITION);
        Map<Long, Position> positions = positionService.findByIds(positionsIds).stream().collect(Collectors.toMap(Entity::getId, Function.identity()));
        List<Long> roleIds = fetchIds.apply(associations.values(), UserAssociationCategory.ROLE);
        Map<Long, Role> roles = roleService.findByIds(roleIds).stream().collect(Collectors.toMap(Entity::getId, Function.identity()));
        List<String> dictionaryCodes = CommonUtils.map(entities, Function.identity(), User::getGender, User::getPersonnelStatus, User::getRank);
        Map<String, String> dictionaryNames = dictionaryService.findNamesByCodes(dictionaryCodes);

        for (User entity : entities) {
            List<Role> userRoles = associations.getOrDefault(entity.getId(), Collections.emptyMap())
                    .getOrDefault(UserAssociationCategory.ROLE, Collections.emptyList())
                    .stream()
                    .map(UserAssociation::getAssociated)
                    .map(roles::get)
                    .toList();
            List<Position> userPositions = associations.getOrDefault(entity.getId(), Collections.emptyMap())
                    .getOrDefault(UserAssociationCategory.POSITION, Collections.emptyList())
                    .stream()
                    .peek(e -> {
                        if (e.getDefaulted() == Bool.Y) {
                            entity.setDefaultPosition(e.getAssociated());
                        }
                    })
                    .map(UserAssociation::getAssociated)
                    .map(positions::get)
                    .toList();
            List<Organization> userOrganizations = associations.getOrDefault(entity.getId(), Collections.emptyMap())
                    .getOrDefault(UserAssociationCategory.ORGANIZATION, Collections.emptyList())
                    .stream()
                    .peek(e -> {
                        if (e.getDefaulted() == Bool.Y) {
                            entity.setDefaultOrganization(e.getAssociated());
                        }
                    })
                    .map(UserAssociation::getAssociated)
                    .map(organizations::get)
                    .toList();
            entity.setRoles(userRoles);
            entity.setPositions(userPositions);
            entity.setOrganizations(userOrganizations);
            if (entity.getRank() != null) {
                entity.setRankName(dictionaryNames.get(entity.getRank()));
            }
            if (entity.getGender() != null) {
                entity.setGenderName(dictionaryNames.get(entity.getGender()));
            }
            if (entity.getPersonnelStatus() != null) {
                entity.setPersonnelStatusName(dictionaryNames.get(entity.getPersonnelStatus()));
            }
        }
    }
}
