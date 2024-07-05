package com.inmaytide.orbit.uaa.service.permission.impl;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inmaytide.orbit.uaa.consts.RoleAssociationCategory;
import com.inmaytide.orbit.uaa.domain.permission.Authority;
import com.inmaytide.orbit.uaa.domain.permission.RoleAssociation;
import com.inmaytide.orbit.uaa.mapper.permission.AuthorityMapper;
import com.inmaytide.orbit.uaa.service.permission.AuthorityService;
import com.inmaytide.orbit.uaa.service.permission.RoleAssociationService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author inmaytide
 * @since 2024/2/23
 */
@Service
public class AuthorityServiceImpl implements AuthorityService {

    private final RoleAssociationService roleAssociationService;

    private final AuthorityMapper baseMapper;

    public AuthorityServiceImpl(RoleAssociationService roleAssociationService, AuthorityMapper baseMapper) {
        this.roleAssociationService = roleAssociationService;
        this.baseMapper = baseMapper;
    }

    @Override
    public List<String> findCodesByRoleIds(List<Long> roleIds) {
        List<Long> ids = roleAssociationService.findByRolesAndCategory(roleIds, RoleAssociationCategory.AUTHORITY)
                .stream()
                .map(RoleAssociation::getAssociated)
                .toList();
        return new ArrayList<>(findFieldValueByIds(ids, Authority::getCode).values());
    }

    @Override
    public BaseMapper<Authority> getBaseMapper() {
        return baseMapper;
    }
}
