package com.inmaytide.orbit.uaa.service.permission.impl;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inmaytide.orbit.commons.domain.SystemUser;
import com.inmaytide.orbit.uaa.domain.permission.Authority;
import com.inmaytide.orbit.uaa.mapper.permission.AuthorityMapper;
import com.inmaytide.orbit.uaa.service.permission.AuthorityService;
import com.inmaytide.orbit.uaa.service.permission.RoleAssociationService;
import org.springframework.stereotype.Service;

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
    public BaseMapper<Authority> getBaseMapper() {
        return baseMapper;
    }

    @Override
    public List<String> findCodesBySystemUser(SystemUser user) {
        return List.of();
    }
}
