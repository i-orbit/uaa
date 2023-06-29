package com.inmaytide.orbit.uaa.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inmaytide.orbit.commons.consts.Is;
import com.inmaytide.orbit.commons.consts.Roles;
import com.inmaytide.orbit.commons.domain.Robot;
import com.inmaytide.orbit.commons.domain.pattern.Entity;
import com.inmaytide.orbit.uaa.configuration.ApplicationProperties;
import com.inmaytide.orbit.uaa.domain.role.Role;
import com.inmaytide.orbit.uaa.domain.user.User;
import com.inmaytide.orbit.uaa.mapper.role.RoleMapper;
import com.inmaytide.orbit.uaa.service.RoleService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author inmaytide
 * @since 2023/5/19
 */
@Service
public class RoleServiceImpl implements RoleService {

    private final ApplicationProperties properties;

    private final RoleMapper mapper;

    public RoleServiceImpl(ApplicationProperties properties, RoleMapper mapper) {
        this.properties = properties;
        this.mapper = mapper;
    }

    @Override
    public BaseMapper<Role> getMapper() {
        return mapper;
    }

    @Override
    public Class<Role> getEntityClass() {
        return Role.class;
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
}
