package com.inmaytide.orbit.uaa.service.permission.impl;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inmaytide.orbit.uaa.domain.permission.Role;
import com.inmaytide.orbit.uaa.mapper.permission.RoleMapper;
import com.inmaytide.orbit.uaa.service.permission.RoleService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author inmaytide
 * @since 2024/2/23
 */
@Service
public class RoleServiceImpl implements RoleService {

    private final RoleMapper baseMapper;

    public RoleServiceImpl(RoleMapper baseMapper) {
        this.baseMapper = baseMapper;
    }

    @Override
    public List<String> findCodesByIds(List<Long> ids) {
        return new ArrayList<>(findFieldValueByIds(ids, Role::getCode).values());
    }

    @Override
    public BaseMapper<Role> getBaseMapper() {
        return baseMapper;
    }
}
