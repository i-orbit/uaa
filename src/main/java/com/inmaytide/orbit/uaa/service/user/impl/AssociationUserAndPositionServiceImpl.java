package com.inmaytide.orbit.uaa.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.inmaytide.orbit.commons.domain.dto.result.AffectedResult;
import com.inmaytide.orbit.commons.utils.CommonUtils;
import com.inmaytide.orbit.uaa.domain.user.AssociationUserAndPosition;
import com.inmaytide.orbit.uaa.domain.user.AssociationUserAndRole;
import com.inmaytide.orbit.uaa.mapper.user.AssociationUserAndPositionMapper;
import com.inmaytide.orbit.uaa.service.PositionService;
import com.inmaytide.orbit.uaa.service.user.AssociationUserAndPositionService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author inmaytide
 * @since 2023/6/29
 */
@Service
public class AssociationUserAndPositionServiceImpl implements AssociationUserAndPositionService {

    private final AssociationUserAndPositionMapper mapper;

    private final PositionService positionService;

    public AssociationUserAndPositionServiceImpl(AssociationUserAndPositionMapper mapper, PositionService positionService) {
        this.mapper = mapper;
        this.positionService = positionService;
    }

    @Override
    public AffectedResult deleteByUsers(Collection<Long> users) {
        LambdaQueryWrapper<AssociationUserAndPosition> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(AssociationUserAndPosition::getUserId, users);
        return AffectedResult.of(mapper.delete(wrapper));
    }

    @Override
    public void save(Long user, Collection<AssociationUserAndPosition> associations) {
        deleteByUser(user);
        if (CollectionUtils.isNotEmpty(associations)) {
            associations.stream()
                    .peek(e -> e.setUserId(user))
                    .forEach(mapper::insert);
        }
    }

    @Override
    public Map<Long, List<AssociationUserAndPosition>> findByUsers(Collection<Long> users) {
        LambdaQueryWrapper<AssociationUserAndPosition> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(AssociationUserAndPosition::getUserId, users);
        List<AssociationUserAndPosition> entities = mapper.selectList(wrapper);
        Map<Long, String> positionNames = positionService.findNamesByIds(CommonUtils.map(entities, Function.identity(), AssociationUserAndPosition::getPositionId));
        return entities.stream()
                .peek(e -> e.setPositionName(positionNames.get(e.getPositionId())))
                .collect(Collectors.groupingBy(
                        AssociationUserAndPosition::getUserId,
                        Collectors.toList()
                ));
    }
}
