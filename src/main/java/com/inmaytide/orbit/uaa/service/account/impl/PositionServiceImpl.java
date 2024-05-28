package com.inmaytide.orbit.uaa.service.account.impl;

import com.inmaytide.orbit.commons.business.impl.BasicServiceImpl;
import com.inmaytide.orbit.uaa.domain.account.Position;
import com.inmaytide.orbit.uaa.mapper.account.PositionMapper;
import com.inmaytide.orbit.uaa.service.account.PositionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author inmaytide
 * @since 2024/5/28
 */
@Service
public class PositionServiceImpl extends BasicServiceImpl<PositionMapper, Position> implements PositionService {

    @Override
    public Map<Long, String> findNamesByIds(List<Long> ids) {
        return findFieldValueByIds(ids, Position::getName);
    }

}
