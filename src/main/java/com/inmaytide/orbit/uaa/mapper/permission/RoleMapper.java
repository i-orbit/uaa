package com.inmaytide.orbit.uaa.mapper.permission;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inmaytide.orbit.uaa.domain.permission.Role;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author inmaytide
 * @since 2023/6/29
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {
}
