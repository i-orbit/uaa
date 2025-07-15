package com.inmaytide.orbit.uaa.service.feature.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.inmaytide.exception.web.BadRequestException;
import com.inmaytide.orbit.commons.domain.pattern.Entity;
import com.inmaytide.orbit.uaa.configuration.ErrorCode;
import com.inmaytide.orbit.uaa.domain.feature.Feature;
import com.inmaytide.orbit.uaa.domain.feature.FeatureMenu;
import com.inmaytide.orbit.uaa.mapper.feature.FeatureMenuMapper;
import com.inmaytide.orbit.uaa.service.feature.FeatureFunctionService;
import com.inmaytide.orbit.uaa.service.feature.FeatureMenuService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
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
public class FeatureMenuServiceImpl implements FeatureMenuService {

    private final FeatureMenuMapper baseMapper;

    private final FeatureFunctionService featureFunctionService;

    public FeatureMenuServiceImpl(FeatureMenuMapper baseMapper, FeatureFunctionService featureFunctionService) {
        this.baseMapper = baseMapper;
        this.featureFunctionService = featureFunctionService;
    }

    @Override
    public BaseMapper<FeatureMenu> getBaseMapper() {
        return baseMapper;
    }

    @Override
    public void persist(Feature entity) {
        if (CollectionUtils.isEmpty(entity.getMenus())) {
            return;
        }
        if (entity.getMenus().stream().collect(Collectors.groupingBy(FeatureMenu::getCode)).values().stream().anyMatch(e -> e.size() > 1)) {
            throw new BadRequestException(ErrorCode.E_0x00100022);
        }
        List<String> ids = entity.getMenus().stream().map(Entity::getId).toList();
        Map<String, FeatureMenu> originals = ids.isEmpty() ? Map.of() : baseMapper.selectBatchIds(ids).stream().collect(Collectors.toMap(Entity::getId, Function.identity()));
        Map<String, FeatureMenu> exists = findByCodes(entity.getMenus().stream().map(FeatureMenu::getCode).toList()).stream().collect(Collectors.toMap(FeatureMenu::getCode, Function.identity()));

        for (FeatureMenu menu : entity.getMenus()) {
            FeatureMenu exist = exists.get(menu.getCode());
            if (exist != null) {
                if (!Objects.equals(exist.getId(), menu.getId())) {
                    throw new BadRequestException(ErrorCode.E_0x00100022);
                }
            }
            if (menu.getId() != null && originals.containsKey(menu.getId())) {
                FeatureMenu original = originals.get(menu.getId());
                BeanUtils.copyProperties(menu, original, "deleted", "createdBy", "createdAt", "updatedBy", "updatedAt", "code", "platform");
                baseMapper.updateById(original);
            } else {
                baseMapper.insert(menu);
            }
            featureFunctionService.persist(menu);
        }
    }

    private List<FeatureMenu> findByCodes(List<String> codes) {
        LambdaQueryWrapper<FeatureMenu> wrapper = Wrappers.lambdaQuery(FeatureMenu.class)
                .in(FeatureMenu::getCode, codes);
        return baseMapper.selectList(wrapper);
    }
}
