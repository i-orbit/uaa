package com.inmaytide.orbit.uaa.mapper.permission;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inmaytide.orbit.uaa.domain.permission.Tenant;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author inmaytide
 * @since 2024/1/19
 */
@Mapper
public interface TenantMapper extends BaseMapper<Tenant> {
}
