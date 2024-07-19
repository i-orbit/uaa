package com.inmaytide.orbit.uaa.service.feature.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inmaytide.exception.web.BadRequestException;
import com.inmaytide.exception.web.ObjectNotFoundException;
import com.inmaytide.orbit.commons.constants.Bool;
import com.inmaytide.orbit.commons.domain.dto.result.TreeNode;
import com.inmaytide.orbit.uaa.configuration.ErrorCode;
import com.inmaytide.orbit.uaa.domain.feature.Feature;
import com.inmaytide.orbit.uaa.mapper.feature.FeatureMapper;
import com.inmaytide.orbit.uaa.service.feature.FeatureMenuService;
import com.inmaytide.orbit.uaa.service.feature.FeatureService;
import org.springframework.stereotype.Service;

import java.util.TreeSet;

/**
 * @author inmaytide
 * @since 2024/7/19
 */
@Service
public class FeatureServiceImpl implements FeatureService {

    private final FeatureMapper baseMapper;

    private final FeatureMenuService featureMenuService;

    public FeatureServiceImpl(FeatureMapper baseMapper, FeatureMenuService featureMenuService) {
        this.baseMapper = baseMapper;
        this.featureMenuService = featureMenuService;
    }

    @Override
    public BaseMapper<Feature> getBaseMapper() {
        return baseMapper;
    }

    private boolean exist(Feature feature) {
        LambdaQueryWrapper<Feature> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Feature::getCode, feature.getCode());
        wrapper.ne(feature.getId() != null, Feature::getId, feature.getId());
        return baseMapper.exists(wrapper);
    }

    @Override
    public Feature create(Feature entity) {
        if (exist(entity)) {
            throw new BadRequestException(ErrorCode.E_0x00100021);
        }
        baseMapper.insert(entity);
        featureMenuService.persist(entity);
        return get(entity.getId()).orElseThrow(ObjectNotFoundException::new);
    }

    @Override
    public Feature update(Feature entity) {
        if (exist(entity)) {
            throw new BadRequestException(ErrorCode.E_0x00100021);
        }
        Feature original = baseMapper.selectById(entity.getId());
        original.setDescription(entity.getDescription());
        original.setName(entity.getCode());
        original.setNecessary(entity.getNecessary());
        baseMapper.updateById(entity);
        featureMenuService.persist(entity);
        return get(entity.getId()).orElseThrow(ObjectNotFoundException::new);
    }

    @Override
    public TreeSet<TreeNode<Object>> treeOfFeatures(Bool includeMenus) {
        return null;
    }
}
