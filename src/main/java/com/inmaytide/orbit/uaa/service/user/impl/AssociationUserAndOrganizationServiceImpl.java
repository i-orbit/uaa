package com.inmaytide.orbit.uaa.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.inmaytide.orbit.commons.domain.dto.result.AffectedResult;
import com.inmaytide.orbit.commons.utils.CommonUtils;
import com.inmaytide.orbit.uaa.domain.user.AssociationUserAndOrganization;
import com.inmaytide.orbit.uaa.domain.user.AssociationUserAndPosition;
import com.inmaytide.orbit.uaa.mapper.user.AssociationUserAndOrganizationMapper;
import com.inmaytide.orbit.uaa.service.OrganizationService;
import com.inmaytide.orbit.uaa.service.user.AssociationUserAndOrganizationService;
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
public class AssociationUserAndOrganizationServiceImpl implements AssociationUserAndOrganizationService {

    private final AssociationUserAndOrganizationMapper mapper;

    private final OrganizationService organizationService;

    public AssociationUserAndOrganizationServiceImpl(AssociationUserAndOrganizationMapper mapper, OrganizationService organizationService) {
        this.mapper = mapper;
        this.organizationService = organizationService;
    }

    @Override
    public AffectedResult deleteByUsers(Collection<Long> users) {
        LambdaQueryWrapper<AssociationUserAndOrganization> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(AssociationUserAndOrganization::getUserId, users);
        return AffectedResult.of(mapper.delete(wrapper));
    }

    @Override
    public void save(Long user, Collection<AssociationUserAndOrganization> associations) {
        deleteByUser(user);
        if (CollectionUtils.isNotEmpty(associations)) {
            associations.stream()
                    .peek(e -> e.setUserId(user))
                    .forEach(mapper::insert);
        }
    }

    @Override
    public Map<Long, List<AssociationUserAndOrganization>> findByUsers(Collection<Long> users) {
        LambdaQueryWrapper<AssociationUserAndOrganization> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(AssociationUserAndOrganization::getUserId, users);
        List<AssociationUserAndOrganization> entities = mapper.selectList(wrapper);
        Map<Long, String> organizationNames = organizationService.findNamesByIds(CommonUtils.map(entities, Function.identity(), AssociationUserAndOrganization::getOrganizationId));
        return entities.stream()
                .peek(e -> e.setOrganizationName(organizationNames.get(e.getOrganizationId())))
                .collect(Collectors.groupingBy(
                        AssociationUserAndOrganization::getUserId,
                        Collectors.toList()
                ));
    }
}
