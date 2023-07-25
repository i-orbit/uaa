package com.inmaytide.orbit.uaa.service.tenant.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.inmaytide.exception.web.ObjectNotFoundException;
import com.inmaytide.orbit.commons.consts.CacheNames;
import com.inmaytide.orbit.commons.consts.Is;
import com.inmaytide.orbit.commons.consts.TenantState;
import com.inmaytide.orbit.commons.domain.pattern.Entity;
import com.inmaytide.orbit.commons.utils.CommonUtils;
import com.inmaytide.orbit.uaa.domain.tenant.Tenant;
import com.inmaytide.orbit.uaa.mapper.tenant.TenantMapper;
import com.inmaytide.orbit.uaa.service.tenant.TenantService;
import com.inmaytide.orbit.uaa.service.user.UserService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author inmaytide
 * @since 2023/5/19
 */
@Service
public class TenantServiceImpl extends ServiceImpl<TenantMapper, Tenant> implements TenantService {

    private final UserService userService;

    public TenantServiceImpl(UserService userService) {
        this.userService = userService;
    }

    private void setDefaultValueForFields(Tenant entity) {
        if (StringUtils.isBlank(entity.getAlias())) {
            entity.setAlias(entity.getName());
        }
        if (entity.getMenuSynced() == null) {
            entity.setMenuSynced(Is.N);
        }
        if (entity.getState() == null) {
            entity.setState(TenantState.INITIALIZATION);
        }
    }

    @Override
    public Tenant create(Tenant entity) {
        setDefaultValueForFields(entity);
        return TenantService.super.create(entity);
    }

    @Override
    @CacheEvict(cacheNames = CacheNames.TENANT_DETAILS, key = "#entity.id")
    public Tenant update(Tenant entity) {
        Tenant tenant = get(entity.getId()).orElseThrow(() -> new ObjectNotFoundException(Objects.toString(entity.getId())));
        tenant.setName(entity.getName());
        tenant.setAlias(entity.getAlias());
        tenant.setLogo(entity.getLogo());
        setDefaultValueForFields(tenant);
        return TenantService.super.update(tenant);
    }

    @Override
    @Cacheable(cacheNames = CacheNames.TENANT_DETAILS, key = "#id", unless = "#result == null")
    public Optional<Tenant> get(Long id) {
        return TenantService.super.get(id);
    }

    @Override
    public Map<Long, String> getNamesByIds(String ids) {
        List<Long> params = CommonUtils.splitToLongByCommas(ids);
        if (CollectionUtils.isEmpty(params)) {
            return Collections.emptyMap();
        }
        LambdaQueryWrapper<Tenant> wrapper = Wrappers.lambdaQuery(Tenant.class)
                .select(Tenant::getId, Tenant::getName)
                .in(Tenant::getId, params);
        return baseMapper.selectList(wrapper).stream().collect(Collectors.toMap(Entity::getId, Tenant::getName));
    }

    @Override
    public void setExtraAttributes(Collection<Tenant> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return;
        }
        List<Long> userIds = CommonUtils.map(entities, Function.identity(), Tenant::getCreatedBy, Tenant::getModifiedBy);
        Map<Long, String> usernames = userService.findNamesByIds(userIds);
        for (Tenant entity : entities) {
            entity.setCreatedByName(usernames.get(entity.getCreatedBy()));
            entity.setModifiedByName(usernames.get(entity.getModifiedBy()));
        }
    }
}
