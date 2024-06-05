package com.inmaytide.orbit.uaa.service.account.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.inmaytide.exception.web.BadRequestException;
import com.inmaytide.exception.web.ObjectNotFoundException;
import com.inmaytide.orbit.commons.business.impl.BasicServiceImpl;
import com.inmaytide.orbit.commons.constants.Constants;
import com.inmaytide.orbit.commons.domain.SystemUser;
import com.inmaytide.orbit.commons.domain.dto.params.BatchUpdate;
import com.inmaytide.orbit.commons.domain.dto.result.AffectedResult;
import com.inmaytide.orbit.commons.domain.dto.result.TreeNode;
import com.inmaytide.orbit.commons.domain.pattern.Entity;
import com.inmaytide.orbit.commons.security.SecurityUtils;
import com.inmaytide.orbit.commons.service.core.GeographicCoordinateService;
import com.inmaytide.orbit.uaa.configuration.ErrorCode;
import com.inmaytide.orbit.uaa.domain.account.Organization;
import com.inmaytide.orbit.uaa.mapper.account.OrganizationMapper;
import com.inmaytide.orbit.uaa.service.account.OrganizationService;
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
public class OrganizationServiceImpl extends BasicServiceImpl<OrganizationMapper, Organization> implements OrganizationService {

    private final GeographicCoordinateService geographicCoordinateService;

    public OrganizationServiceImpl(GeographicCoordinateService geographicCoordinateService) {
        this.geographicCoordinateService = geographicCoordinateService;
    }

    private boolean exist(Organization entity) {
        LambdaQueryWrapper<Organization> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Organization::getCode, entity.getCode());
        wrapper.eq(Organization::getTenant, entity.getTenant());
        wrapper.ne(entity.getId() != null, Organization::getId, entity.getId());
        return baseMapper.exists(wrapper);
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
        Organization res = super.create(entity);
        if (entity.getLocation() != null) {
            geographicCoordinateService.persist(new BatchUpdate<>(res.getId(), List.of(entity.getLocation())));
            res.setLocation(entity.getLocation());
        }
        return res;
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
        return super.deleteByIds(ids);
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
        return baseMapper.selectList(wrapper);
    }

}
