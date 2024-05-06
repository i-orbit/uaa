package com.inmaytide.orbit.uaa.service.permission.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.inmaytide.exception.web.ObjectNotFoundException;
import com.inmaytide.orbit.commons.business.impl.BasicServiceImpl;
import com.inmaytide.orbit.commons.constants.Bool;
import com.inmaytide.orbit.commons.constants.Roles;
import com.inmaytide.orbit.commons.domain.Robot;
import com.inmaytide.orbit.uaa.configuration.ApplicationProperties;
import com.inmaytide.orbit.uaa.consts.UserAssociationCategory;
import com.inmaytide.orbit.uaa.domain.account.User;
import com.inmaytide.orbit.uaa.domain.account.UserAssociation;
import com.inmaytide.orbit.uaa.domain.permission.Role;
import com.inmaytide.orbit.uaa.mapper.permission.RoleMapper;
import com.inmaytide.orbit.uaa.service.account.UserAssociationService;
import com.inmaytide.orbit.uaa.service.account.UserService;
import com.inmaytide.orbit.uaa.service.permission.RoleService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author inmaytide
 * @since 2024/2/23
 */
@Service
public class RoleServiceImpl extends BasicServiceImpl<RoleMapper, Role> implements RoleService {

    private final UserAssociationService userAssociationService;

    private UserService userService;

    private final ApplicationProperties properties;

    public RoleServiceImpl(UserAssociationService userAssociationService, ApplicationProperties properties) {
        this.userAssociationService = userAssociationService;
        this.properties = properties;
    }

    @Override
    public List<String> findCodesByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Role::getCode);
        wrapper.in(Role::getId, ids);
        return baseMapper.selectList(wrapper).stream().map(Role::getCode).collect(Collectors.toList());
    }

    @Override
    public List<String> findCodesByUser(Long userId) {
        Optional<User> op = userService.get(userId);
        if (op.isEmpty()) {
            throw new ObjectNotFoundException(String.valueOf(userId));
        }
        User user = op.get();
        // 用户绑定的角色
        List<UserAssociation> userAssociations = userAssociationService.findByUserAndCategory(user.getId(), UserAssociationCategory.ROLE);
        List<String> codes = findCodesByIds(userAssociations.stream().map(UserAssociation::getAssociated).collect(Collectors.toList()));
        // 系统是否启用了超级管理员&&用户的登录名和配置指定的超级管理员用户名是否匹配
        if (properties.isEnableSuperAdministrator() && properties.getSuperAdministratorLoginNames().contains(user.getLoginName())) {
            codes.add(Roles.ROLE_S_ADMINISTRATOR.name());
        }
        // 用户是否是租户管理员
        if (user.getIsTenantAdministrator() == Bool.Y) {
            codes.add(Roles.ROLE_T_ADMINISTRATOR.name());
        }
        // 用户是否为配置的系统内部机器人
        if (Objects.equals(userId, Robot.getInstance().getId())) {
            codes.add(Roles.ROLE_ROBOT.name());
        }
        return codes;
    }

    @Lazy
    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
