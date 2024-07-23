package com.inmaytide.orbit.uaa.mapper.permission;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inmaytide.orbit.uaa.domain.permission.TenantPermission;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author inmaytide
 * @since 2024/7/23
 */
@Mapper
public interface TenantPermissionMapper extends BaseMapper<TenantPermission> {
}
