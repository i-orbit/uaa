package com.inmaytide.orbit.uaa.service.role;

import com.baomidou.mybatisplus.extension.service.IService;
import com.inmaytide.orbit.uaa.domain.role.Role;
import com.inmaytide.orbit.uaa.domain.role.RoleAssociation;

import java.util.List;

/**
 * @author inmaytide
 * @since 2023/7/21
 */
public interface RoleAssociationService extends IService<RoleAssociation> {

    void deleteByRole(Long roleId);

    void save(Role entity);

    List<RoleAssociation> findByRoles(List<Long> roleIds);
}