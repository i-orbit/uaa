package com.inmaytide.orbit.uaa.service.permission;

import java.util.List;

/**
 * @author inmaytide
 * @since 2023/5/19
 */
public interface AuthorityService {

    List<String> findCodesByIds(List<Long> ids);

    List<String> findCodesByRoleCodes(List<String> roleCodes);

}
