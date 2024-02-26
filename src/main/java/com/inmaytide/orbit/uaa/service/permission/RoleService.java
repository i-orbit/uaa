package com.inmaytide.orbit.uaa.service.permission;

import com.inmaytide.orbit.commons.business.BasicService;
import com.inmaytide.orbit.uaa.domain.permission.Role;

import java.util.List;

/**
 * @author inmaytide
 * @since 2024/2/23
 */
public interface RoleService extends BasicService<Role> {

    List<String> findCodesByUser(Long user);

    List<String> findCodesByIds(List<Long> ids);

}
