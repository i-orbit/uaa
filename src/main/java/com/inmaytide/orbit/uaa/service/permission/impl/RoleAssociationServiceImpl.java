package com.inmaytide.orbit.uaa.service.permission.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.inmaytide.orbit.uaa.domain.permission.AuthorityCategory;
import com.inmaytide.orbit.uaa.domain.permission.Role;
import com.inmaytide.orbit.uaa.domain.permission.RoleAssociation;
import com.inmaytide.orbit.uaa.mapper.permission.RoleAssociationMapper;
import com.inmaytide.orbit.uaa.service.permission.RoleAssociationService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author inmaytide
 * @since 2023/7/21
 */
@Service
public class RoleAssociationServiceImpl extends ServiceImpl<RoleAssociationMapper, RoleAssociation> implements RoleAssociationService {

    @Override
    public void deleteByRole(Long roleId) {
        LambdaQueryWrapper<RoleAssociation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoleAssociation::getRole, roleId);
        getBaseMapper().delete(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void save(Role entity) {
        List<RoleAssociation> associations = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(entity.getOrganizations())) {
            entity.getOrganizations().stream().map(e -> new RoleAssociation(entity.getId(), AuthorityCategory.ORGANIZATION, e)).forEach(associations::add);
        }
        if (CollectionUtils.isNotEmpty(entity.getAppMenus())) {
            entity.getAppMenus().stream().map(e -> new RoleAssociation(entity.getId(), AuthorityCategory.APP_MENU, e)).forEach(associations::add);
        }
        if (CollectionUtils.isNotEmpty(entity.getWebMenus())) {
            entity.getWebMenus().stream().map(e -> new RoleAssociation(entity.getId(), AuthorityCategory.WEB_MENU, e)).forEach(associations::add);
        }
        if (CollectionUtils.isNotEmpty(entity.getAuthorities())) {
            entity.getAuthorities().stream().map(e -> new RoleAssociation(entity.getId(), AuthorityCategory.AUTHORITY, e)).forEach(associations::add);
        }
        saveBatch(associations);
    }

    @Override
    public List<RoleAssociation> findByRoles(List<Long> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<RoleAssociation> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(RoleAssociation::getRole, roleIds);
        return getBaseMapper().selectList(wrapper);
    }
}
