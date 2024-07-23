package com.inmaytide.orbit.uaa.service.permission;

import com.inmaytide.orbit.uaa.consts.TenantPermissionCategory;
import com.inmaytide.orbit.uaa.domain.permission.TenantPermission;

import java.util.List;

/**
 * @author inmaytide
 * @since 2024/7/23
 */
public interface TenantPermissionService {

    List<TenantPermission> findByTenantAndCategory(Long tenant, TenantPermissionCategory category);

}
