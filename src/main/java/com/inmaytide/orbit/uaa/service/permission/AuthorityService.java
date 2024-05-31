package com.inmaytide.orbit.uaa.service.permission;

import com.inmaytide.orbit.commons.business.BasicService;
import com.inmaytide.orbit.uaa.domain.permission.Authority;

import java.util.List;

/**
 * @author inmaytide
 * @since 2024/2/23
 */
public interface AuthorityService extends BasicService<Authority> {

    List<String> findCodesByRoleIds(List<Long> roleIds);

}
