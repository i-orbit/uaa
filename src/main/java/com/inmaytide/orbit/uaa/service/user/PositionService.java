package com.inmaytide.orbit.uaa.service.user;

import com.inmaytide.orbit.commons.business.BasicService;
import com.inmaytide.orbit.uaa.domain.user.Position;

import java.util.List;
import java.util.Map;

/**
 * @author inmaytide
 * @since 2023/6/29
 */
public interface PositionService extends BasicService<Position> {

    Map<Long, String> findNamesByIds(List<Long> ids);

}
