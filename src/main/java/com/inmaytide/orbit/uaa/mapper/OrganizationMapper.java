package com.inmaytide.orbit.uaa.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inmaytide.orbit.uaa.domain.organization.Organization;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author inmaytide
 * @since 2023/6/27
 */
@Mapper
public interface OrganizationMapper extends BaseMapper<Organization> {
}
