package com.inmaytide.orbit.uaa.service.permission.impl;

import com.inmaytide.orbit.commons.business.impl.BasicServiceImpl;
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
public class AuthorityServiceImpl extends BasicServiceImpl<AuthorityMapper, Authority> implements AuthorityService {

    private final RoleAssociationService roleAssociationService;

    public AuthorityServiceImpl(RoleAssociationService roleAssociationService) {
        this.roleAssociationService = roleAssociationService;
    }

    @Override
    public List<String> findCodesByRoleIds(List<Long> roleIds) {
        List<Long> ids = roleAssociationService.findByRolesAndCategory(roleIds, RoleAssociationCategory.AUTHORITY)
                .stream()
                .map(RoleAssociation::getAssociated)
                .toList();
        return new ArrayList<>(findFieldValueByIds(ids, Authority::getCode).values());
    }

}
