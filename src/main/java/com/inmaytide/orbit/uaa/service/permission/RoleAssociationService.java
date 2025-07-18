package com.inmaytide.orbit.uaa.service.permission;

import com.inmaytide.orbit.commons.constants.RoleAssociationCategory;
import com.inmaytide.orbit.uaa.domain.permission.RoleAssociation;

import java.util.List;

/**
 * @author inmaytide
 * @since 2024/5/31
 */
public interface RoleAssociationService {

    List<RoleAssociation> findByRolesAndCategory(List<String> roles, RoleAssociationCategory category);

}
