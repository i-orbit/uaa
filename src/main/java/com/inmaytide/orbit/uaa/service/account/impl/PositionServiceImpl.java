package com.inmaytide.orbit.uaa.service.account.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.inmaytide.orbit.commons.domain.dto.result.TreeNode;
import com.inmaytide.orbit.uaa.domain.account.Position;
import com.inmaytide.orbit.uaa.mapper.account.PositionMapper;
import com.inmaytide.orbit.uaa.service.account.PositionService;
import org.springframework.stereotype.Service;

import javax.swing.text.html.parser.Entity;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * @author inmaytide
 * @since 2024/5/28
 */
@Service
public class PositionServiceImpl implements PositionService {

    private final PositionMapper baseMapper;

    public PositionServiceImpl(PositionMapper baseMapper) {
        this.baseMapper = baseMapper;
    }

    private boolean exist(Position entity) {
        LambdaQueryWrapper<Position> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Position::getCode, entity.getCode());
        wrapper.eq(Position::getTenant, entity.getTenant());
        wrapper.ne(entity.getId() != null, Position::getId, entity.getId());
        return baseMapper.exists(wrapper);
    }

    @Override
    public Map<String, String> findNamesByIds(List<String> ids) {
        return findFieldValueByIds(ids, Position::getName);
    }

    @Override
    public TreeSet<TreeNode<? extends Entity>> treeOfPositions() {
        return null;
    }

    @Override
    public PositionMapper getBaseMapper() {
        return baseMapper;
    }
}
