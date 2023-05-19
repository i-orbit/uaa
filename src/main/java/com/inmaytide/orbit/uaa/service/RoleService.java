package com.inmaytide.orbit.uaa.service;

import com.inmaytide.orbit.commons.business.BasicService;
import com.inmaytide.orbit.uaa.domain.Role;
import com.inmaytide.orbit.uaa.domain.User;

import java.util.List;

/**
 * @author inmaytide
 * @since 2023/5/19
 */
public interface RoleService extends BasicService<Role> {

    List<String> findRoleCodesByUser(User user);

}
