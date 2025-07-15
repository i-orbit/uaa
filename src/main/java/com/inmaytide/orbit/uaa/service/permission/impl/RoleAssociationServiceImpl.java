package com.inmaytide.orbit.uaa.service.permission.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.inmaytide.orbit.commons.constants.RoleAssociationCategory;
import com.inmaytide.orbit.uaa.domain.permission.RoleAssociation;
import com.inmaytide.orbit.uaa.mapper.permission.RoleAssociationMapper;
import com.inmaytide.orbit.uaa.service.permission.RoleAssociationService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author inmaytide
 * @since 2024/5/31
 */
@Service
public class RoleAssociationServiceImpl implements RoleAssociationService {

    private final RoleAssociationMapper baseMapper;

    public RoleAssociationServiceImpl(RoleAssociationMapper baseMapper) {
        this.baseMapper = baseMapper;
    }

    @Override
    public List<RoleAssociation> findByRolesAndCategory(List<String> roles, RoleAssociationCategory category) {
        if (roles == null || roles.isEmpty()) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<RoleAssociation> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(RoleAssociation::getRole, roles);
        wrapper.eq(RoleAssociation::getCategory, category);
        return baseMapper.selectList(wrapper);
    }

}
