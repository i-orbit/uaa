package com.inmaytide.orbit.uaa.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.inmaytide.exception.web.AccessDeniedException;
import com.inmaytide.exception.web.BadRequestException;
import com.inmaytide.exception.web.ObjectNotFoundException;
import com.inmaytide.orbit.commons.consts.CacheNames;
import com.inmaytide.orbit.commons.consts.Is;
import com.inmaytide.orbit.commons.domain.GlobalUser;
import com.inmaytide.orbit.commons.domain.Perspective;
import com.inmaytide.orbit.commons.domain.Robot;
import com.inmaytide.orbit.commons.domain.dto.result.AffectedResult;
import com.inmaytide.orbit.commons.domain.pattern.Entity;
import com.inmaytide.orbit.commons.security.SecurityUtils;
import com.inmaytide.orbit.commons.utils.CommonUtils;
import com.inmaytide.orbit.uaa.domain.consts.UserAssociationCategory;
import com.inmaytide.orbit.uaa.domain.user.ChangePassword;
import com.inmaytide.orbit.uaa.domain.user.User;
import com.inmaytide.orbit.uaa.domain.user.UserAssociation;
import com.inmaytide.orbit.uaa.mapper.user.UserMapper;
import com.inmaytide.orbit.uaa.service.AuthorityService;
import com.inmaytide.orbit.uaa.service.OrganizationService;
import com.inmaytide.orbit.uaa.service.RoleService;
import com.inmaytide.orbit.uaa.service.user.UserAssociationService;
import com.inmaytide.orbit.uaa.service.user.UserService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.inmaytide.orbit.uaa.configuration.ErrorCode.E_0x00100005;
import static com.inmaytide.orbit.uaa.configuration.ErrorCode.E_0x00100006;

/**
 * @author inmaytide
 * @since 2023/4/7
 */
@Primary
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper mapper;

    private final RoleService roleService;

    private final AuthorityService authorityService;

    private final OrganizationService organizationService;

    private final UserAssociationService associationService;

    public UserServiceImpl(UserMapper mapper, RoleService roleService, AuthorityService authorityService, OrganizationService organizationService, UserAssociationService associationService) {
        this.mapper = mapper;
        this.roleService = roleService;
        this.authorityService = authorityService;
        this.organizationService = organizationService;
        this.associationService = associationService;
    }


    @Override
    public BaseMapper<User> getMapper() {
        return mapper;
    }

    @Override
    public Class<User> getEntityClass() {
        return User.class;
    }

    @Override
    public Optional<User> findUserByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getDeleted, Is.N.name());
        wrapper.and(w ->
                w.eq(User::getUsername, username)
                        .or().eq(User::getTelephoneNumber, username)
                        .or().eq(User::getEmail, username)
                        .or().eq(User::getEmployeeId, username)
        );
        return Optional.ofNullable(mapper.selectOne(wrapper));
    }

    private boolean exist(User user) {
        // 如果与机器人重名
        if (Objects.equals(user.getUsername(), Robot.getInstance().getUsername())) {
            return true;
        }
        // 登录名/手机号码/邮箱地址/员工编号重复
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getDeleted, Is.N.name());
        wrapper.ne(user.getId() != null, User::getId, user.getId());
        wrapper.and(w -> {
            w.eq(User::getUsername, user.getUsername());
            if (StringUtils.isNotBlank(user.getTelephoneNumber())) {
                w.or().eq(User::getTelephoneNumber, user.getTelephoneNumber());
            }
            if (StringUtils.isNotBlank(user.getEmail())) {
                w.or().eq(User::getEmail, user.getEmail());
            }
            if (StringUtils.isNotBlank(user.getEmployeeId())) {
                w.or().eq(User::getEmployeeId, user.getEmployeeId());
            }
        });
        return mapper.selectCount(wrapper) > 0;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public User create(User entity) {
        if (exist(entity)) {
            throw new BadRequestException(E_0x00100005);
        }
        // 添加租户管理员时验证权限
        if (entity.getIsTenantAdministrator() == Is.Y) {
            if (!SecurityUtils.isSuperAdministrator() && !SecurityUtils.isTenantAdministrator(entity.getTenantId())) {
                throw new AccessDeniedException(E_0x00100006);
            }
        }
        getMapper().insert(entity);
        associationService.persistForUser(entity);
        updated();
        return get(entity.getId()).orElseThrow(() -> new ObjectNotFoundException(String.valueOf(entity.getId())));
    }

    @Override
    @CacheEvict(cacheNames = CacheNames.USER_DETAILS, key = "#entity.id")
    @Transactional(rollbackFor = Throwable.class)
    public User update(User entity) {
        User original = mapper.selectById(entity.getId());
        if (original == null) {
            throw new ObjectNotFoundException(String.valueOf(entity.getId()));
        }
        if (exist(entity)) {
            throw new BadRequestException(E_0x00100005);
        }
        // 修改租户管理员时验证权限
        if (entity.getIsTenantAdministrator() != original.getIsTenantAdministrator()) {
            if (!SecurityUtils.isSuperAdministrator() && !SecurityUtils.isTenantAdministrator(entity.getTenantId())) {
                throw new AccessDeniedException(E_0x00100006);
            }
        }
        getMapper().updateById(entity);
        associationService.persistForUser(entity);
        updated();
        return get(entity.getId()).orElseThrow(() -> new ObjectNotFoundException(String.valueOf(entity.getId())));
    }

    @Override
    public Map<Long, String> findNamesByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyMap();
        }
        LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery(User.class);
        wrapper.select(User::getId, User::getName);
        wrapper.in(User::getId, ids);
        return getMapper().selectList(wrapper)
                .stream()
                .collect(Collectors.toMap(Entity::getId, User::getName));
    }

    @Override
    public void setExtraAttributes(Collection<User> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return;
        }
        List<Long> ids = CommonUtils.map(entities, Function.identity(), User::getId);
        Map<Long, Map<UserAssociationCategory, List<UserAssociation>>> allUserAssociations = associationService.findByUsers(ids);
        entities.forEach(user -> {
            Map<UserAssociationCategory, List<UserAssociation>> associations = allUserAssociations.get(user.getId());
            if (associations != null) {
                user.setOrganizations(associations.get(UserAssociationCategory.ORGANIZATION));
                user.setPositions(associations.get(UserAssociationCategory.POSITION));
                user.setRoles(associations.get(UserAssociationCategory.ROLE));
            }
        });
    }

    @Override
    @Cacheable(cacheNames = CacheNames.USER_DETAILS, key = "#id", unless = "#result == null")
    public GlobalUser get(Serializable id) {
        User user = mapper.selectById(id);
        GlobalUser globalUser = new GlobalUser();
        BeanUtils.copyProperties(user, globalUser);
        globalUser.setRoles(roleService.findRoleCodesByUser(user));
        globalUser.setAuthorities(authorityService.findAuthoritiesByUser(user));



//        globalUser.setDefaultUnderOrganization();
//        globalUser.setUnderOrganizations();

        Perspective perspective = new Perspective();
        globalUser.setPerspective(perspective);
//        perspective.setAuthorizedOrganizations();
//        perspective.setOrganizations();
//
//        perspective.setAuthorizedAreas();
//        perspective.setAreas();

        return globalUser;
    }

    @Override
    public AffectedResult changePasswordWithOriginalPassword(ChangePassword body) {
        return null;
    }

    @Override
    public AffectedResult changePasswordWithCaptcha(ChangePassword body) {
        return null;
    }
}
