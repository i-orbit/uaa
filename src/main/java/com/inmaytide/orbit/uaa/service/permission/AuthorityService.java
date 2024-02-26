package com.inmaytide.orbit.uaa.service.permission;

import java.util.List;

/**
 * @author inmaytide
 * @since 2024/2/23
 */
public interface AuthorityService {

    List<String> findCodesByRoleCodes(List<String> roleCodes);

}
