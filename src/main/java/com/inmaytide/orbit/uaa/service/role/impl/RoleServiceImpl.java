package com.inmaytide.orbit.uaa.service.role.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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
import com.inmaytide.orbit.uaa.domain.role.Role;
import com.inmaytide.orbit.uaa.domain.role.RoleAssociation;
import com.inmaytide.orbit.uaa.domain.user.User;
import com.inmaytide.orbit.uaa.mapper.role.RoleMapper;
import com.inmaytide.orbit.uaa.service.role.RoleAssociationService;
import com.inmaytide.orbit.uaa.service.role.RoleService;
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

    private final RoleMapper mapper;

    private final RoleAssociationService associationService;

    private final CacheManager cacheManager;

    public RoleServiceImpl(ApplicationProperties properties, RoleMapper mapper, RoleAssociationService associationService, CacheManager cacheManager) {
        this.properties = properties;
        this.mapper = mapper;
        this.associationService = associationService;
        this.cacheManager = cacheManager;
    }

    @Override
    public BaseMapper<Role> getMapper() {
        return getBaseMapper();
    }

    private boolean exists(Role entity) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getCode, entity.getCode());
        wrapper.eq(Role::getName, entity.getName());
        wrapper.ne(entity.getId() != null, Role::getId, entity.getId());
        return mapper.exists(wrapper);
    }

    @Override
    public Role create(Role entity) {
        setDefaultValueForFields(entity);
        if (exists(entity)) {
            throw new BadRequestException(ErrorCode.E_0x00100012);
        }

        mapper.insert(entity);
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
        mapper.updateById(entity);
        associationService.deleteByRole(entity.getId());
        associationService.save(entity);
        updated();
        return entity;
    }

    @Override
    public List<String> findRoleCodesByUser(User user) {
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
        return res;
    }

    @Override
    public Map<Long, String> findNamesByIds(List<Long> ids) {
        if (CollectionUtils.isNotEmpty(ids)) {
            return Collections.emptyMap();
        }
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Role::getId, Role::getName);
        wrapper.in(Role::getId, ids);
        return mapper.selectList(wrapper).stream().collect(Collectors.toMap(Entity::getId, Role::getName));
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
