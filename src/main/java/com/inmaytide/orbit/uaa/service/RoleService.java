package com.inmaytide.orbit.uaa.service;

import com.inmaytide.orbit.commons.business.BasicService;
import com.inmaytide.orbit.uaa.domain.role.Role;
import com.inmaytide.orbit.uaa.domain.user.User;

import java.util.List;
import java.util.Map;

/**
 * @author inmaytide
 * @since 2023/5/19
 */
public interface RoleService extends BasicService<Role> {

    List<String> findRoleCodesByUser(User user);

    Map<Long, String> findNamesByIds(List<Long> ids);

}
