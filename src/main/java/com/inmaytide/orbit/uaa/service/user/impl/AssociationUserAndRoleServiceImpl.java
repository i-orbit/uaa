package com.inmaytide.orbit.uaa.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.inmaytide.orbit.commons.domain.dto.result.AffectedResult;
import com.inmaytide.orbit.commons.utils.CommonUtils;
import com.inmaytide.orbit.uaa.domain.user.AssociationUserAndRole;
import com.inmaytide.orbit.uaa.mapper.user.AssociationUserAndRoleMapper;
import com.inmaytide.orbit.uaa.service.RoleService;
import com.inmaytide.orbit.uaa.service.user.AssociationUserAndRoleService;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author inmaytide
 * @since 2023/6/29
 */
public class AssociationUserAndRoleServiceImpl implements AssociationUserAndRoleService {

    private final AssociationUserAndRoleMapper mapper;

    private final RoleService roleService;

    public AssociationUserAndRoleServiceImpl(AssociationUserAndRoleMapper mapper, RoleService roleService) {
        this.mapper = mapper;
        this.roleService = roleService;
    }

    @Override
    public AffectedResult deleteByUsers(Collection<Long> users) {
        LambdaQueryWrapper<AssociationUserAndRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(AssociationUserAndRole::getUserId, users);
        return AffectedResult.of(mapper.delete(wrapper));
    }

    @Override
    public void save(Long user, Collection<AssociationUserAndRole> associations) {
        deleteByUser(user);
        if (CollectionUtils.isNotEmpty(associations)) {
            associations.stream()
                    .peek(e -> e.setUserId(user))
                    .forEach(mapper::insert);
        }
    }

    @Override
    public Map<Long, List<AssociationUserAndRole>> findByUsers(Collection<Long> users) {
        LambdaQueryWrapper<AssociationUserAndRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(AssociationUserAndRole::getUserId, users);
        List<AssociationUserAndRole> entities = mapper.selectList(wrapper);
        Map<Long, String> roleNames = roleService.findNamesByIds(CommonUtils.map(entities, Function.identity(), AssociationUserAndRole::getRoleId));
        return entities.stream()
                .peek(e -> e.setRoleName(roleNames.get(e.getRoleId())))
                .collect(Collectors.groupingBy(
                        AssociationUserAndRole::getUserId,
                        Collectors.toList()
                ));
    }
}
