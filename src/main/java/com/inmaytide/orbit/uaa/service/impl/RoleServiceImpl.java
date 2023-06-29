package com.inmaytide.orbit.uaa.service.impl;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inmaytide.orbit.commons.consts.Is;
import com.inmaytide.orbit.commons.consts.Roles;
import com.inmaytide.orbit.commons.domain.Robot;
import com.inmaytide.orbit.uaa.configuration.ApplicationProperties;
import com.inmaytide.orbit.uaa.domain.role.Role;
import com.inmaytide.orbit.uaa.domain.user.User;
import com.inmaytide.orbit.uaa.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author inmaytide
 * @since 2023/5/19
 */
@Service
public class RoleServiceImpl implements RoleService {

    private final ApplicationProperties properties;

    public RoleServiceImpl(ApplicationProperties properties) {
        this.properties = properties;
    }

    @Override
    public BaseMapper<Role> getMapper() {
        return null;
    }

    @Override
    public Class<Role> getEntityClass() {
        return null;
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
}
