package com.inmaytide.orbit.uaa.service.permission.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.inmaytide.orbit.uaa.consts.TenantPermissionCategory;
import com.inmaytide.orbit.uaa.domain.permission.TenantPermission;
import com.inmaytide.orbit.uaa.mapper.permission.TenantPermissionMapper;
import com.inmaytide.orbit.uaa.service.permission.TenantPermissionService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author inmaytide
 * @since 2024/7/23
 */
@Service
public class TenantPermissionServiceImpl implements TenantPermissionService {

    private final TenantPermissionMapper baseMapper;

    public TenantPermissionServiceImpl(TenantPermissionMapper baseMapper) {
        this.baseMapper = baseMapper;
    }

    @Override
    public List<TenantPermission> findByTenantAndCategory(String tenant, TenantPermissionCategory category) {
        LambdaQueryWrapper<TenantPermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TenantPermission::getTenant, tenant);
        wrapper.eq(TenantPermission::getCategory, category);
        return baseMapper.selectList(wrapper);
    }

}
