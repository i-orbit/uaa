package com.inmaytide.orbit.uaa.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inmaytide.exception.web.BadRequestException;
import com.inmaytide.exception.web.ObjectNotFoundException;
import com.inmaytide.orbit.commons.consts.Is;
import com.inmaytide.orbit.commons.consts.Marks;
import com.inmaytide.orbit.commons.domain.dto.result.TreeNode;
import com.inmaytide.orbit.commons.domain.pattern.Entity;
import com.inmaytide.orbit.commons.security.SecurityUtils;
import com.inmaytide.orbit.commons.service.library.GeographicCoordinateService;
import com.inmaytide.orbit.commons.utils.ApplicationContextHolder;
import com.inmaytide.orbit.uaa.configuration.ErrorCode;
import com.inmaytide.orbit.uaa.domain.organization.Organization;
import com.inmaytide.orbit.uaa.mapper.OrganizationMapper;
import com.inmaytide.orbit.uaa.service.OrganizationService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author inmaytide
 * @since 2023/6/27
 */
@Service
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationMapper mapper;

    private final GeographicCoordinateService geographicCoordinateService;

    public OrganizationServiceImpl(OrganizationMapper mapper, GeographicCoordinateService geographicCoordinateService) {
        this.mapper = mapper;
        this.geographicCoordinateService = geographicCoordinateService;
    }

    @Override
    public Organization create(Organization entity) {
        setDefaultValueForFields(entity);
        if (exists(entity)) {
            throw new BadRequestException(ErrorCode.E_0x00100007);
        }
        mapper.insert(entity);
        persistGeolocation(entity);
        updated();
        return get(entity.getId()).orElseThrow(() -> new ObjectNotFoundException(String.valueOf(entity.getId())));
    }

    @Override
    public Organization update(Organization entity) {
        setDefaultValueForFields(entity);
        if (exists(entity)) {
            throw new BadRequestException(ErrorCode.E_0x00100007);
        }
        mapper.updateById(entity);
        persistGeolocation(entity);
        updated();
        return get(entity.getId()).orElseThrow(() -> new ObjectNotFoundException(String.valueOf(entity.getId())));
    }

    @Override
    public List<TreeNode<Organization>> getTreeOfOrganizations() {
        List<Organization> all = all(SecurityUtils.getAuthorizedUser().getTenantId());
        List<Long> authorizedIds = SecurityUtils.getAuthorizedUser().getPerspective().getOrganizations();
        List<Long> requiredIds = getRequiredIds(authorizedIds, all);
        return getChildren(NumberUtils.createLong(Marks.TREE_ROOT.getValue()), all, authorizedIds, requiredIds);
    }

    @Override
    public Map<Long, String> findNamesByIds(List<Long> ids) {
        if (CollectionUtils.isNotEmpty(ids)) {
            return Collections.emptyMap();
        }
        LambdaQueryWrapper<Organization> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Organization::getId, Organization::getName);
        wrapper.in(Organization::getId, ids);
        return mapper.selectList(wrapper).stream().collect(Collectors.toMap(Entity::getId, Organization::getName));
    }

    private List<TreeNode<Organization>> getChildren(Long parent, List<Organization> all, List<Long> authorizedIds, List<Long> requiredIds) {
        return all.stream().filter(e -> Objects.equals(parent, e.getParent()))
                .filter(e -> requiredIds.contains(e.getId()))
                .map(e -> createTreeNode(e, all, authorizedIds, requiredIds))
                .collect(Collectors.toList());
    }

    private TreeNode<Organization> createTreeNode(Organization o, List<Organization> all, List<Long> authorizedIds, List<Long> requiredIds) {
        TreeNode<Organization> node = new TreeNode<>(o);
        node.setId(o.getId());
        node.setName(o.getName());
        node.setParent(o.getParent());
        node.setSymbol(TREE_NODE_SYMBOL);
        node.setAuthorized(authorizedIds.contains(o.getId()));
        node.setChildren(getChildren(o.getParent(), all, authorizedIds, requiredIds));
        return node;
    }

    private List<Long> getRequiredIds(List<Long> authorizedIds, List<Organization> all) {
        return all.stream().filter(e -> authorizedIds.contains(e.getId()))
                .map(Organization::getPath)
                .flatMap(e -> Pattern.compile(",").splitAsStream(e))
                .filter(StringUtils::isNotBlank)
                .distinct()
                .map(NumberUtils::createLong)
                .collect(Collectors.toList());
    }

    private boolean exists(Organization entity) {
        LambdaQueryWrapper<Organization> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Organization::getDeleted, Is.N.name());
        wrapper.eq(Organization::getParent, entity.getParent());
        wrapper.eq(Organization::getName, entity.getName());
        wrapper.ne(entity.getId() != null, Organization::getId, entity.getId());
        return mapper.exists(wrapper);
    }

    private void setDefaultValueForFields(Organization entity) {
        if (StringUtils.isBlank(entity.getAlias())) {
            entity.setAlias(entity.getName());
        }
        if (entity.getParent() == null) {
            entity.setParent(NumberUtils.createLong(Marks.TREE_ROOT.getValue()));
        }
        // 生成树路径信息
        Map<Long, Organization> all = ApplicationContextHolder.getInstance() // 这里不适用this是因为使用this会导致AOP代理失效, 缓存失效
                .getBean(OrganizationService.class)
                .all(entity.getTenant())
                .stream()
                .collect(Collectors.toMap(Entity::getId, Function.identity()));
        List<Long> path = new ArrayList<>();
        Long parent = entity.getParent();
        while (all.containsKey(parent)) {
            path.add(parent);
            parent = all.get(parent).getParent();
        }
        path.add(parent);
        Collections.reverse(path);
        entity.setPath(StringUtils.join(path, ","));
    }

    private void persistGeolocation(Organization entity) {
        if (entity.getGeolocation() != null) {
            entity.getGeolocation().setAttribution(entity.getId());
            geographicCoordinateService.persist(Collections.singletonList(entity.getGeolocation()));
        }
    }

    @Override
    public BaseMapper<Organization> getMapper() {
        return mapper;
    }

    @Override
    public Class<Organization> getEntityClass() {
        return Organization.class;
    }

    @Override
    public List<Organization> all(Long tenant) {
        LambdaQueryWrapper<Organization> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Organization::getTenant, tenant);
        wrapper.eq(Organization::getDeleted, Is.N.name());
        wrapper.orderByAsc(Organization::getSequence);
        return mapper.selectList(wrapper);
    }
}
