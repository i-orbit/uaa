package com.inmaytide.orbit.uaa.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.inmaytide.orbit.commons.domain.pattern.Entity;
import com.inmaytide.orbit.uaa.domain.user.Position;
import com.inmaytide.orbit.uaa.mapper.user.PositionMapper;
import com.inmaytide.orbit.uaa.service.user.PositionService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author inmaytide
 * @since 2023/6/29
 */
@Service
public class PositionServiceImpl extends ServiceImpl<PositionMapper, Position> implements PositionService {

    @Override
    public Map<Long, String> findNamesByIds(List<Long> ids) {
        if (CollectionUtils.isNotEmpty(ids)) {
            return Collections.emptyMap();
        }
        LambdaQueryWrapper<Position> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Position::getId, Position::getName);
        wrapper.in(Position::getId, ids);
        return getBaseMapper().selectList(wrapper).stream().collect(Collectors.toMap(Entity::getId, Position::getName));
    }

}
