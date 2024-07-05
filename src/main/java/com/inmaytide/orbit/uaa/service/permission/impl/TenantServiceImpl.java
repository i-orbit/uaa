package com.inmaytide.orbit.uaa.service.permission.impl;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inmaytide.orbit.commons.constants.Constants;
import com.inmaytide.orbit.uaa.domain.permission.Tenant;
import com.inmaytide.orbit.uaa.mapper.permission.TenantMapper;
import com.inmaytide.orbit.uaa.service.permission.TenantService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author inmaytide
 * @since 2024/7/2
 */
@Primary
@Service
public class TenantServiceImpl implements TenantService {

    private final TenantMapper mapper;

    public TenantServiceImpl(TenantMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public BaseMapper<Tenant> getBaseMapper() {
        return mapper;
    }

    @Override
    @Cacheable(cacheNames = Constants.CacheNames.TENANT_DETAILS, key = "#id", condition = "#result.present")
    public Optional<Tenant> get(Long id) {
        return TenantService.super.get(id);
    }

}
