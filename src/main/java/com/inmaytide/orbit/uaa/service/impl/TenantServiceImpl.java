package com.inmaytide.orbit.uaa.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.inmaytide.exception.web.ObjectNotFoundException;
import com.inmaytide.orbit.commons.consts.CacheNames;
import com.inmaytide.orbit.commons.consts.Is;
import com.inmaytide.orbit.commons.consts.TenantState;
import com.inmaytide.orbit.commons.domain.pattern.Entity;
import com.inmaytide.orbit.commons.utils.Assert;
import com.inmaytide.orbit.commons.utils.CommonUtils;
import com.inmaytide.orbit.uaa.configuration.ErrorCode;
import com.inmaytide.orbit.uaa.domain.tenant.Tenant;
import com.inmaytide.orbit.uaa.mapper.TenantMapper;
import com.inmaytide.orbit.uaa.service.TenantService;
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
public class TenantServiceImpl implements TenantService {

    private final TenantMapper mapper;

    private final UserService userService;

    public TenantServiceImpl(TenantMapper mapper, UserService userService) {
        this.mapper = mapper;
        this.userService = userService;
    }

    @Override
    public BaseMapper<Tenant> getMapper() {
        return mapper;
    }

    @Override
    public Class<Tenant> getEntityClass() {
        return Tenant.class;
    }

    @Override
    public Tenant create(Tenant entity) {
        if (StringUtils.isBlank(entity.getAlias())) {
            entity.setAlias(entity.getName());
        }
        entity.setMenuSynced(Is.N);
        entity.setState(TenantState.INITIALIZATION);
        return TenantService.super.create(entity);
    }

    @Override
    @CacheEvict(cacheNames = CacheNames.TENANT_DETAILS, key = "#entity.id")
    public Tenant update(Tenant entity) {
        Assert.notNull(entity.getId(), ErrorCode.E_0x00100004);
        Tenant tenant = get(entity.getId()).orElseThrow(() -> new ObjectNotFoundException(Objects.toString(entity.getId())));
        tenant.setName(entity.getName());
        tenant.setAlias(entity.getAlias());
        tenant.setLogo(entity.getLogo());
        if (StringUtils.isBlank(tenant.getAlias())) {
            tenant.setAlias(tenant.getName());
        }
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
        return getMapper().selectList(wrapper).stream().collect(Collectors.toMap(Entity::getId, Tenant::getName));
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
