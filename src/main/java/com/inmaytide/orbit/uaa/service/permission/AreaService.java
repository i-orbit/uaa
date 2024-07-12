package com.inmaytide.orbit.uaa.service.permission;

import com.inmaytide.orbit.commons.business.BasicService;
import com.inmaytide.orbit.commons.domain.SystemUser;
import com.inmaytide.orbit.uaa.domain.permission.Area;

import java.util.List;

/**
 * @author inmaytide
 * @since 2024/7/12
 */
public interface AreaService extends BasicService<Area> {

    List<Long> findAuthorizedIds(SystemUser user);

}
