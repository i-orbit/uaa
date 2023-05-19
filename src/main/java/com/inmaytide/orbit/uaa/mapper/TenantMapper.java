package com.inmaytide.orbit.uaa.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inmaytide.orbit.uaa.domain.Tenant;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author inmaytide
 * @since 2023/5/19
 */
@Mapper
public interface TenantMapper extends BaseMapper<Tenant> {
}
