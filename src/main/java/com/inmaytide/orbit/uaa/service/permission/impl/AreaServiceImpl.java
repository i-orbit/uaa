package com.inmaytide.orbit.uaa.service.permission.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
import com.inmaytide.orbit.uaa.domain.permission.Area;
import com.inmaytide.orbit.uaa.mapper.permission.AreaMapper;
import com.inmaytide.orbit.uaa.service.permission.AreaService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author inmaytide
 * @since 2023/6/26
 */
@Service
public class AreaServiceImpl extends ServiceImpl<AreaMapper, Area> implements AreaService {


    private final GeographicCoordinateService geographicCoordinateService;

    public AreaServiceImpl(GeographicCoordinateService geographicCoordinateService) {
        this.geographicCoordinateService = geographicCoordinateService;
    }

    @Override
    @CacheEvict(cacheNames = CACHE_NAME_ALL_AREAS, key = "#entity.tenant")
    public Area create(Area entity) {
        setDefaultValueForFields(entity);
        if (exists(entity)) {
            throw new BadRequestException(ErrorCode.E_0x00100007);
        }
        getBaseMapper().insert(entity);
        persistGeolocation(entity);
        return get(entity.getId()).orElseThrow(() -> new ObjectNotFoundException(String.valueOf(entity.getId())));
    }

    @Override
    @CacheEvict(cacheNames = CACHE_NAME_ALL_AREAS, key = "#entity.tenant")
    public Area update(Area entity) {
        setDefaultValueForFields(entity);
        if (exists(entity)) {
            throw new BadRequestException(ErrorCode.E_0x00100007);
        }
        getBaseMapper().updateById(entity);
        persistGeolocation(entity);
        return get(entity.getId()).orElseThrow(() -> new ObjectNotFoundException(String.valueOf(entity.getId())));
    }

    @Override
    @Cacheable(cacheNames = CACHE_NAME_ALL_AREAS, key = "#tenant")
    public List<Area> all(Long tenant) {
        LambdaQueryWrapper<Area> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Area::getTenant, tenant);
        wrapper.eq(Area::getDeleted, Is.N.name());
        wrapper.orderByAsc(Area::getSequence);
        return getBaseMapper().selectList(wrapper);
    }

    @Override
    public List<TreeNode<Area>> getTreeOfAreas() {
        List<Area> all = ApplicationContextHolder.getInstance() // 这里不适用this是因为使用this会导致AOP代理失效, 缓存失效
                .getBean(AreaService.class)
                .all(SecurityUtils.getAuthorizedUser().getTenantId());


        return null;
    }

    private void persistGeolocation(Area entity) {
        if (CollectionUtils.isNotEmpty(entity.getGeolocation())) {
            entity.getGeolocation().forEach(e -> e.setAttribution(entity.getId()));
            geographicCoordinateService.persist(entity.getGeolocation());
        }
    }

    private void setDefaultValueForFields(Area entity) {
        if (StringUtils.isBlank(entity.getAlias())) {
            entity.setAlias(entity.getName());
        }
        if (entity.getParent() == null) {
            entity.setParent(NumberUtils.createLong(Marks.TREE_ROOT.getValue()));
        }
        entity.setTenant(SecurityUtils.getAuthorizedUser().getTenantId());
        // 生成树路径信息
        Map<Long, Area> all = ApplicationContextHolder.getInstance() // 这里不适用this是因为使用this会导致AOP代理失效, 缓存失效
                .getBean(AreaService.class)
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

    private boolean exists(Area entity) {
        LambdaQueryWrapper<Area> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Area::getDeleted, Is.N.name());
        wrapper.eq(Area::getParent, entity.getParent());
        wrapper.eq(Area::getName, entity.getName());
        wrapper.ne(entity.getId() != null, Area::getId, entity.getId());
        return getBaseMapper().exists(wrapper);
    }

}
