package com.inmaytide.orbit.uaa.service.account;

import com.inmaytide.orbit.commons.business.BasicService;
import com.inmaytide.orbit.uaa.domain.account.Position;

import java.util.List;
import java.util.Map;

/**
 * @author inmaytide
 * @since 2024/5/28
 */
public interface PositionService extends BasicService<Position> {

    Map<Long, String> findNamesByIds(List<Long> ids);

}
