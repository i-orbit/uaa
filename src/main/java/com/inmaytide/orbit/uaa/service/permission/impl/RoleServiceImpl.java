package com.inmaytide.orbit.uaa.service.permission.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.inmaytide.exception.web.BadRequestException;
import com.inmaytide.orbit.commons.consts.CacheNames;
import com.inmaytide.orbit.commons.consts.Is;
import com.inmaytide.orbit.commons.consts.Roles;
import com.inmaytide.orbit.commons.domain.Robot;
import com.inmaytide.orbit.commons.domain.pattern.Entity;
import com.inmaytide.orbit.commons.utils.CommonUtils;
import com.inmaytide.orbit.uaa.configuration.ApplicationProperties;
import com.inmaytide.orbit.uaa.configuration.ErrorCode;
import com.inmaytide.orbit.uaa.domain.user.UserAssociationCategory;
import com.inmaytide.orbit.uaa.domain.permission.Role;
import com.inmaytide.orbit.uaa.domain.permission.RoleAssociation;
import com.inmaytide.orbit.uaa.domain.user.User;
import com.inmaytide.orbit.uaa.domain.user.UserAssociation;
import com.inmaytide.orbit.uaa.mapper.permission.RoleMapper;
import com.inmaytide.orbit.uaa.service.permission.RoleAssociationService;
import com.inmaytide.orbit.uaa.service.permission.RoleService;
import com.inmaytide.orbit.uaa.service.user.UserAssociationService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author inmaytide
 * @since 2023/5/19
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    private final ApplicationProperties properties;

    private final RoleAssociationService associationService;

    private final UserAssociationService userAssociationService;

    private final CacheManager cacheManager;

    public RoleServiceImpl(ApplicationProperties properties, RoleAssociationService associationService, UserAssociationService userAssociationService, CacheManager cacheManager) {
        this.properties = properties;
        this.associationService = associationService;
        this.userAssociationService = userAssociationService;
        this.cacheManager = cacheManager;
    }

    private boolean exists(Role entity) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getCode, entity.getCode());
        wrapper.eq(Role::getName, entity.getName());
        wrapper.ne(entity.getId() != null, Role::getId, entity.getId());
        return baseMapper.exists(wrapper);
    }

    @Override
    public Role create(Role entity) {
        setDefaultValueForFields(entity);
        if (exists(entity)) {
            throw new BadRequestException(ErrorCode.E_0x00100012);
        }

        baseMapper.insert(entity);
        associationService.save(entity);
        updated();
        return entity;
    }

    @Override
    public Role update(Role entity) {
        setDefaultValueForFields(entity);
        if (exists(entity)) {
            throw new BadRequestException(ErrorCode.E_0x00100012);
        }
        baseMapper.updateById(entity);
        associationService.deleteByRole(entity.getId());
        associationService.save(entity);
        updated();
        return entity;
    }

    @Override
    public List<String> findCodesByUser(User user) {
        List<String> res = new ArrayList<>();
        // 如果系统启用了超级管理员
        if (properties.isEnableSuperAdministrator() && Objects.equals(properties.getSuperAdministratorUsername(), user.getUsername())) {
            res.add(Roles.ROLE_S_ADMINISTRATOR.name());
        }
        // 如果用户是租户管理员
        if (Is.Y == user.getIsTenantAdministrator()) {
            res.add(Roles.ROLE_T_ADMINISTRATOR.name());
        }
        // 如果是机器人
        if (Robot.getInstance().getUsername().equals(user.getUsername())) {
            res.add(Roles.ROLE_ROBOT.name());
        }
        List<UserAssociation> userAssociations = userAssociationService.findByUser(UserAssociationCategory.ROLE, user.getId());
        if (CollectionUtils.isNotEmpty(userAssociations)) {
            res.addAll(findCodesByIds(userAssociations.stream().map(UserAssociation::getAssociated).collect(Collectors.toList())));
        }
        return res;
    }

    @Override
    public List<String> findCodesByIds(List<Long> ids) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Role::getCode);
        wrapper.in(Role::getId, ids);
        return getBaseMapper().selectList(wrapper).stream().map(Role::getCode).collect(Collectors.toList());
    }

    @Override
    public Map<Long, String> findNamesByIds(List<Long> ids) {
        if (CollectionUtils.isNotEmpty(ids)) {
            return Collections.emptyMap();
        }
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Role::getId, Role::getName);
        wrapper.in(Role::getId, ids);
        return baseMapper.selectList(wrapper).stream().collect(Collectors.toMap(Entity::getId, Role::getName));
    }

    @Override
    public List<RoleAssociation> findAssociationsByCodes(List<String> roles) {
        if (CollectionUtils.isEmpty(roles)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Role::getId);
        wrapper.in(Role::getCode, roles);
        return associationService.findByRoles(CommonUtils.map(getBaseMapper().selectList(wrapper), Function.identity(), Role::getId));
    }

    private void setDefaultValueForFields(Role entity) {
        if (entity.getWeights() == null) {
            entity.setWeights(100);
        }
    }

    @Override
    public void updated() {
        Cache cache = cacheManager.getCache(CacheNames.USER_DETAILS);
        if (cache != null) {
            cache.clear();
        }
    }
}
