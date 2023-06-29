package com.inmaytide.orbit.uaa.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inmaytide.orbit.commons.domain.pattern.Entity;
import com.inmaytide.orbit.uaa.domain.Position;
import com.inmaytide.orbit.uaa.domain.role.Role;
import com.inmaytide.orbit.uaa.mapper.PositionMapper;
import com.inmaytide.orbit.uaa.service.PositionService;
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
public class PositionServiceImpl implements PositionService {

    private final PositionMapper mapper;

    public PositionServiceImpl(PositionMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Map<Long, String> findNamesByIds(List<Long> ids) {
        if (CollectionUtils.isNotEmpty(ids)) {
            return Collections.emptyMap();
        }
        LambdaQueryWrapper<Position> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Position::getId, Position::getName);
        wrapper.in(Position::getId, ids);
        return mapper.selectList(wrapper).stream().collect(Collectors.toMap(Entity::getId, Position::getName));
    }

    @Override
    public BaseMapper<Position> getMapper() {
        return mapper;
    }

    @Override
    public Class<Position> getEntityClass() {
        return Position.class;
    }
}
