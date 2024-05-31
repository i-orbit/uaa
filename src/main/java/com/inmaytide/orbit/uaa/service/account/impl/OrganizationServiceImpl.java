package com.inmaytide.orbit.uaa.service.account.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.inmaytide.orbit.commons.business.impl.BasicServiceImpl;
import com.inmaytide.orbit.commons.constants.Constants;
import com.inmaytide.orbit.commons.domain.SystemUser;
import com.inmaytide.orbit.commons.domain.dto.result.TreeNode;
import com.inmaytide.orbit.commons.domain.pattern.Entity;
import com.inmaytide.orbit.commons.security.SecurityUtils;
import com.inmaytide.orbit.commons.service.core.GeographicCoordinateService;
import com.inmaytide.orbit.uaa.domain.account.Organization;
import com.inmaytide.orbit.uaa.mapper.account.OrganizationMapper;
import com.inmaytide.orbit.uaa.service.account.OrganizationService;
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

    @Override
    public Organization create(Organization entity) {
        entity.setSequence(baseMapper.findNextSequence());
        if (entity.getParent() == null) {
            entity.setParent(Constants.Markers.TREE_ROOT);
        }
        Organization res = super.create(entity);
        if (entity.getLocation() != null) {
            entity.getLocation().setBusinessDataId(res.getId());
            geographicCoordinateService.persist(Collections.singletonList(entity.getLocation()));
            res.setLocation(entity.getLocation());
        }
        return res;
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
        TreeNode<Organization> node = new TreeNode<>();
        node.setId(organization.getId());
        node.setSymbol(SYMBOL);
        node.setLevel(level);
        node.setName(organization.getName());
        node.setParent(organization.getParent());
        node.setEntity(organization);
        node.setChildren(findTreeNodes(organization.getId(), level + 1, all, requiredIds, user));
        node.setAuthorized(user.getPermission().getOrganizations().contains(organization.getId()));
        node.setSequence(organization.getSequence());
        return node;
    }

    /**
     * 查询当前用户所有有权限的数据字典数据从根节点开始构成一个树结构集合需要的所有数据字典编码集合
     *
     * @param all 所有数据字典集合
     */
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
