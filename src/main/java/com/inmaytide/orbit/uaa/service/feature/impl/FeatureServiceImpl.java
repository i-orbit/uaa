package com.inmaytide.orbit.uaa.service.feature.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.inmaytide.exception.web.BadRequestException;
import com.inmaytide.exception.web.ObjectNotFoundException;
import com.inmaytide.orbit.commons.constants.Bool;
import com.inmaytide.orbit.commons.domain.SystemUser;
import com.inmaytide.orbit.commons.domain.dto.result.TreeNode;
import com.inmaytide.orbit.commons.security.SecurityUtils;
import com.inmaytide.orbit.uaa.configuration.ErrorCode;
import com.inmaytide.orbit.uaa.consts.TenantPermissionCategory;
import com.inmaytide.orbit.uaa.domain.feature.Feature;
import com.inmaytide.orbit.uaa.domain.permission.TenantPermission;
import com.inmaytide.orbit.uaa.mapper.feature.FeatureMapper;
import com.inmaytide.orbit.uaa.service.feature.FeatureMenuService;
import com.inmaytide.orbit.uaa.service.feature.FeatureService;
import com.inmaytide.orbit.uaa.service.permission.TenantPermissionService;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author inmaytide
 * @since 2024/7/19
 */
@Service
public class FeatureServiceImpl implements FeatureService {

    private final FeatureMapper baseMapper;

    private final FeatureMenuService featureMenuService;

    private final TenantPermissionService tenantPermissionService;

    public FeatureServiceImpl(FeatureMapper baseMapper, FeatureMenuService featureMenuService, TenantPermissionService tenantPermissionService) {
        this.baseMapper = baseMapper;
        this.featureMenuService = featureMenuService;
        this.tenantPermissionService = tenantPermissionService;
    }

    @Override
    public BaseMapper<Feature> getBaseMapper() {
        return baseMapper;
    }

    private boolean exist(Feature feature) {
        LambdaQueryWrapper<Feature> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Feature::getCode, feature.getCode());
        wrapper.ne(feature.getId() != null, Feature::getId, feature.getId());
        return baseMapper.exists(wrapper);
    }

    @Override
    public Feature create(Feature entity) {
        if (exist(entity)) {
            throw new BadRequestException(ErrorCode.E_0x00100021);
        }
        baseMapper.insert(entity);
        featureMenuService.persist(entity);
        return get(entity.getId()).orElseThrow(ObjectNotFoundException::new);
    }

    @Override
    public Feature update(Feature entity) {
        if (exist(entity)) {
            throw new BadRequestException(ErrorCode.E_0x00100021);
        }
        Feature original = baseMapper.selectById(entity.getId());
        original.setDescription(entity.getDescription());
        original.setName(entity.getCode());
        original.setNecessary(entity.getNecessary());
        baseMapper.updateById(entity);
        featureMenuService.persist(entity);
        return get(entity.getId()).orElseThrow(ObjectNotFoundException::new);
    }

    @Override
    public TreeSet<TreeNode<Object>> treeOfFeatures(Bool includeMenus) {
        SystemUser user = SecurityUtils.getAuthorizedUser();
        Map<String, Feature> all = baseMapper.selectList(Wrappers.emptyWrapper())
                .stream()
                .collect(Collectors.toMap(Feature::getCode, Function.identity()));
        List<String> authorizedCodes = SecurityUtils.isSuperAdministrator()
                ? new ArrayList<>(all.keySet())
                : tenantPermissionService.findByTenantAndCategory(user.getTenant(), TenantPermissionCategory.FEATURE).stream().map(TenantPermission::getValue).toList();
        List<String> requiredCodes = getRequiredCodes(all, authorizedCodes);
        TreeSet<TreeNode<Object>> res = new TreeSet<>(findTreeNodes(ROOT_CODE, 1, all, requiredCodes, authorizedCodes).stream().map(TreeNode::toNonGenericNode).toList());
        if (includeMenus == Bool.Y) {
            // TODO 添加菜单
        }
        return res;
    }

    private TreeSet<TreeNode<Feature>> findTreeNodes(Serializable parentCode, int level, Map<String, Feature> all, List<String> requiredCodes, List<String> authorizedCodes) {
        return all.values().stream()
                .filter(e -> Objects.equals(e.getParent(), parentCode))
                .filter(e -> requiredCodes.contains(e.getCode()))
                .map(e -> toTreeNode(e, level, all, requiredCodes, authorizedCodes))
                .collect(Collectors.toCollection(TreeSet::new));
    }

    private TreeNode<Feature> toTreeNode(Feature feature, int level, Map<String, Feature> all, List<String> requiredCodes, List<String> authorizedCodes) {
        TreeNode<Feature> node = new TreeNode<>();
        node.setId(feature.getCode());
        node.setSymbol(SYMBOL);
        node.setLevel(level);
        node.setName(feature.getName());
        node.setParent(feature.getParent());
        node.setEntity(feature);
        node.setChildren(findTreeNodes(feature.getCode(), level + 1, all, requiredCodes, authorizedCodes));
        node.setAuthorized(authorizedCodes.contains(feature.getCode()));
        node.setSequence(feature.getSequence());
        return node;
    }

    private List<String> getRequiredCodes(Map<String, Feature> all, List<String> authorizedCodes) {
        return all.values().parallelStream()
                .map(Feature::getCode)
                .filter(authorizedCodes::contains)
                .map(code -> getChainCodes(code, all, new ArrayList<>()))
                .flatMap(Collection::stream)
                .distinct()
                .toList();
    }

    private List<String> getChainCodes(String code, Map<String, Feature> all, List<String> res) {
        res.add(code);
        Feature feature = all.get(code);
        if (feature != null) {
            if (ROOT_CODE.equals(feature.getParent())) {
                res.add(feature.getCode());
            } else {
                getChainCodes(feature.getParent(), all, res);
            }
        }
        return res;
    }
}
