package com.inmaytide.orbit.uaa.service.feature.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inmaytide.exception.web.BadRequestException;
import com.inmaytide.orbit.commons.domain.pattern.Entity;
import com.inmaytide.orbit.uaa.configuration.ErrorCode;
import com.inmaytide.orbit.uaa.domain.feature.FeatureFunction;
import com.inmaytide.orbit.uaa.domain.feature.FeatureMenu;
import com.inmaytide.orbit.uaa.mapper.feature.FeatureFunctionMapper;
import com.inmaytide.orbit.uaa.service.feature.FeatureFunctionService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author inmaytide
 * @since 2024/7/19
 */
@Service
public class FeatureFunctionServiceImpl implements FeatureFunctionService {

    private final FeatureFunctionMapper baseMapper;

    public FeatureFunctionServiceImpl(FeatureFunctionMapper baseMapper) {
        this.baseMapper = baseMapper;
    }

    @Override
    public BaseMapper<FeatureFunction> getBaseMapper() {
        return baseMapper;
    }

    @Override
    public void persist(FeatureMenu menu) {
        if (CollectionUtils.isEmpty(menu.getFunctions())) {
            return;
        }
        if (menu.getFunctions().stream().collect(Collectors.groupingBy(FeatureFunction::getCode)).values().stream().anyMatch(e -> e.size() > 1)) {
            throw new BadRequestException(ErrorCode.E_0x00100023);
        }
        List<String> ids = menu.getFunctions().stream().map(Entity::getId).toList();
        Map<String, FeatureFunction> originals = ids.isEmpty() ? Map.of() : baseMapper.selectBatchIds(ids).stream().collect(Collectors.toMap(Entity::getId, Function.identity()));
        Map<String, FeatureFunction> exists = findByCodes(menu.getFunctions().stream().map(FeatureFunction::getCode).toList()).stream().collect(Collectors.toMap(FeatureFunction::getCode, Function.identity()));

        for (FeatureFunction f : menu.getFunctions()) {
            FeatureFunction exist = exists.get(f.getCode());
            if (exist != null) {
                if (!Objects.equals(exist.getId(), menu.getId())) {
                    throw new BadRequestException(ErrorCode.E_0x00100023);
                }
            }
            if (menu.getId() != null && originals.containsKey(menu.getId())) {
                FeatureFunction original = originals.get(f.getId());
                original.setName(menu.getName());
                baseMapper.updateById(original);
            } else {
                baseMapper.insert(f);
            }
        }
    }

    private List<FeatureFunction> findByCodes(List<String> codes) {
        LambdaQueryWrapper<FeatureFunction> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(FeatureFunction::getCode, codes);
        return baseMapper.selectList(wrapper);
    }
}
