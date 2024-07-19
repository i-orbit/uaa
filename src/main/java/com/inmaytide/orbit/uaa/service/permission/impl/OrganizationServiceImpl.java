package com.inmaytide.orbit.uaa.service.permission.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inmaytide.exception.web.BadRequestException;
import com.inmaytide.exception.web.ObjectNotFoundException;
import com.inmaytide.orbit.commons.constants.Constants;
import com.inmaytide.orbit.commons.constants.Roles;
import com.inmaytide.orbit.commons.domain.Role;
import com.inmaytide.orbit.commons.domain.SystemUser;
import com.inmaytide.orbit.commons.domain.dto.params.BatchUpdate;
import com.inmaytide.orbit.commons.domain.dto.result.AffectedResult;
import com.inmaytide.orbit.commons.domain.dto.result.TreeNode;
import com.inmaytide.orbit.commons.domain.pattern.Entity;
import com.inmaytide.orbit.commons.security.SecurityUtils;
import com.inmaytide.orbit.commons.service.core.GeographicCoordinateService;
import com.inmaytide.orbit.uaa.configuration.ErrorCode;
import com.inmaytide.orbit.uaa.consts.RoleAssociationCategory;
import com.inmaytide.orbit.uaa.domain.permission.Organization;
import com.inmaytide.orbit.uaa.domain.permission.RoleAssociation;
import com.inmaytide.orbit.uaa.mapper.permission.OrganizationMapper;
import com.inmaytide.orbit.uaa.service.permission.OrganizationService;
import com.inmaytide.orbit.uaa.service.permission.RoleAssociationService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author inmaytide
 * @since 2024/5/28
 */
@Service
public class OrganizationServiceImpl implements OrganizationService {

    private final GeographicCoordinateService geographicCoordinateService;

    private final OrganizationMapper baseMapper;

    private final RoleAssociationService roleAssociationService;

    public OrganizationServiceImpl(GeographicCoordinateService geographicCoordinateService, OrganizationMapper baseMapper, RoleAssociationService roleAssociationService) {
        this.geographicCoordinateService = geographicCoordinateService;
        this.baseMapper = baseMapper;
        this.roleAssociationService = roleAssociationService;
    }

    private boolean exist(Organization entity) {
        LambdaQueryWrapper<Organization> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Organization::getCode, entity.getCode());
        wrapper.eq(Organization::getTenant, entity.getTenant());
        wrapper.ne(entity.getId() != null, Organization::getId, entity.getId());
        return getBaseMapper().exists(wrapper);
    }

    @Override
    public List<Long> findAuthorizedIds(SystemUser user) {
        List<String> roleCodes = user.getRoles().stream().map(Role::getCode).toList();
        // 超级管理员没有组织权限
        if (roleCodes.contains(Roles.ROLE_S_ADMINISTRATOR.name())) {
            return List.of();
        }
        // 租户管理员拥有租户所有组织权限
        if (roleCodes.contains(Roles.ROLE_T_ADMINISTRATOR.name())) {
            LambdaQueryWrapper<Organization> wrapper = new LambdaQueryWrapper<>();
            wrapper.select(Organization::getId);
            wrapper.eq(Organization::getTenant, user.getTenant());
            return baseMapper.selectList(wrapper).stream().map(Entity::getId).toList();
        }
        // 内部机器人拥有所有权限
        if (roleCodes.contains(Roles.ROLE_ROBOT.name())) {
            LambdaQueryWrapper<Organization> wrapper = new LambdaQueryWrapper<>();
            wrapper.select(Organization::getId);
            return baseMapper.selectList(wrapper).stream().map(Entity::getId).toList();
        }
        return roleAssociationService
                .findByRolesAndCategory(user.getRoles().stream().map(Role::getId).toList(), RoleAssociationCategory.ORGANIZATION)
                .stream()
                .map(RoleAssociation::getAssociated)
                .toList();
    }

    @Override
    public Organization create(Organization entity) {
        if (exist(entity)) {
            throw new BadRequestException(ErrorCode.E_0x00100016, entity.getCode());
        }
        entity.setSequence(baseMapper.findNextSequence());
        if (entity.getParent() == null) {
            entity.setParent(Constants.Markers.TREE_ROOT);
        }
        baseMapper.insert(entity);
        if (entity.getLocation() != null) {
            geographicCoordinateService.persist(new BatchUpdate<>(entity.getId(), List.of(entity.getLocation())));
            entity.setLocation(entity.getLocation());
        }
        return get(entity.getId()).orElseThrow(ObjectNotFoundException::new);
    }

    @Override
    public Organization update(Organization entity) {
        if (exist(entity)) {
            throw new BadRequestException(ErrorCode.E_0x00100016, entity.getCode());
        }
        Organization original = baseMapper.selectById(entity.getId());
        if (original == null) {
            throw new ObjectNotFoundException(ErrorCode.E_0x00100015, String.valueOf(entity.getId()));
        }
        BeanUtils.copyProperties(entity, original, "parent", "tenant", "sequence", "deleted", "createdBy", "createdTime");
        baseMapper.updateById(entity);
        if (entity.getLocation() != null) {
            geographicCoordinateService.persist(new BatchUpdate<>(entity.getId(), List.of(entity.getLocation())));
        } else {
            geographicCoordinateService.deleteByBusinessDataId(entity.getId());
        }
        return get(entity.getId()).orElseThrow(() -> new ObjectNotFoundException(ErrorCode.E_0x00100015, String.valueOf(entity.getId())));
    }

    @Override
    public AffectedResult deleteByIds(List<Long> ids) {
        return AffectedResult.withAffected(getBaseMapper().deleteBatchIds(ids));
    }

    @Override
    public Map<Long, String> findNamesByIds(List<Long> ids) {
        return findFieldValueByIds(ids, Organization::getName);
    }

    @Override
    public TreeSet<TreeNode<Organization>> treeOfOrganizations() {
        SystemUser user = SecurityUtils.getAuthorizedUser();
        Map<Long, Organization> all = all(user.getTenant()).stream().collect(Collectors.toMap(Entity::getId, Function.identity()));
        List<Long> requiredIds = getRequiredIds(all, user);
        return findTreeNodes(Constants.Markers.TREE_ROOT, 1, all, requiredIds, user);
    }

    private TreeSet<TreeNode<Organization>> findTreeNodes(Long parentId, int level, Map<Long, Organization> all, List<Long> requiredIds, SystemUser user) {
        return all.values().stream()
                .filter(e -> Objects.equals(e.getParent(), parentId))
                .filter(e -> requiredIds.contains(e.getId()))
                .map(e -> toTreeNode(e, level, all, requiredIds, user))
                .collect(Collectors.toCollection(TreeSet::new));
    }

    private TreeNode<Organization> toTreeNode(Organization organization, int level, Map<Long, Organization> all, List<Long> requiredIds, SystemUser user) {
        TreeNode<Organization> node = new TreeNode<>(organization);
        node.setId(organization.getId());
        node.setSymbol(SYMBOL);
        node.setLevel(level);
        node.setName(organization.getName());
        node.setParent(organization.getParent());
        node.setChildren(findTreeNodes(organization.getId(), level + 1, all, requiredIds, user));
        node.setAuthorized(user.getPermission().getOrganizations().contains(organization.getId()));
        node.setSequence(organization.getSequence());
        return node;
    }

    private List<Long> getRequiredIds(Map<Long, Organization> all, SystemUser user) {
        return all.keySet().stream()
                .filter(id -> user.getPermission().getSpecifiedOrganizations().contains(id))
                .map(id -> getChainIds(id, all, new ArrayList<>()))
                .flatMap(Collection::stream)
                .distinct()
                .toList();
    }

    private List<Long> getChainIds(Long id, Map<Long, Organization> all, List<Long> res) {
        res.add(id);
        Organization organization = all.get(id);
        if (organization != null) {
            if (Constants.Markers.TREE_ROOT.equals(organization.getParent())) {
                res.add(organization.getId());
            } else {
                getChainIds(organization.getParent(), all, res);
            }
        }
        return res;
    }

    private List<Organization> all(Long tenant) {
        LambdaQueryWrapper<Organization> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Organization::getTenant, tenant);
        return getBaseMapper().selectList(wrapper);
    }

    @Override
    public BaseMapper<Organization> getBaseMapper() {
        return baseMapper;
    }

}
