package com.inmaytide.orbit.uaa.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.inmaytide.orbit.commons.domain.dto.result.AffectedResult;
import com.inmaytide.orbit.uaa.domain.consts.UserAssociationCategory;
import com.inmaytide.orbit.uaa.domain.user.User;
import com.inmaytide.orbit.uaa.domain.user.UserAssociation;
import com.inmaytide.orbit.uaa.mapper.user.UserAssociationMapper;
import com.inmaytide.orbit.uaa.service.OrganizationService;
import com.inmaytide.orbit.uaa.service.PositionService;
import com.inmaytide.orbit.uaa.service.role.RoleService;
import com.inmaytide.orbit.uaa.service.user.UserAssociationService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author inmaytide
 * @since 2023/6/29
 */
@Service
public class UserAssociationServiceImpl implements UserAssociationService {

    private final UserAssociationMapper mapper;

    private final OrganizationService organizationService;

    private final RoleService roleService;

    private final PositionService positionService;

    public UserAssociationServiceImpl(UserAssociationMapper mapper, OrganizationService organizationService, RoleService roleService, PositionService positionService) {
        this.mapper = mapper;
        this.organizationService = organizationService;
        this.roleService = roleService;
        this.positionService = positionService;
    }

    @Override
    public AffectedResult deleteByUsers(UserAssociationCategory category, Collection<Long> users) {
        if (CollectionUtils.isEmpty(users)) {
            return AffectedResult.notAffected();
        }
        LambdaQueryWrapper<UserAssociation> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(UserAssociation::getUser, users);
        wrapper.eq(category != null, UserAssociation::getCategory, category);
        return AffectedResult.of(mapper.delete(wrapper));
    }

    @Override
    public AffectedResult deleteByUser(UserAssociationCategory category, Long user) {
        return deleteByUsers(category, Collections.singleton(user));
    }

    @Override
    public AffectedResult deleteByUser(Long user) {
        return deleteByUser(null, user);
    }

    @Override
    public AffectedResult deleteByUsers(Collection<Long> users) {
        return deleteByUsers(null, users);
    }

    @Override
    @Transactional
    public void persistForUser(User user) {
        deleteByUser(user.getId());
        persist(user, user.getOrganizations(), UserAssociationCategory.ORGANIZATION);
        persist(user, user.getPositions(), UserAssociationCategory.POSITION);
        persist(user, user.getRoles(), UserAssociationCategory.ROLE);
    }

    private void persist(User user, List<UserAssociation> associations, UserAssociationCategory category) {
        for (UserAssociation e : associations) {
            e.setUser(user.getId());
            e.setCategory(category);
            mapper.insert(e);
        }
    }

    @Override
    public Map<Long, Map<UserAssociationCategory, List<UserAssociation>>> findByUsers(Collection<Long> users) {
        if (CollectionUtils.isEmpty(users)) {
            return Collections.emptyMap();
        }
        Map<Long, List<UserAssociation>> organizations = findByUsers(UserAssociationCategory.ORGANIZATION, users);
        Map<Long, List<UserAssociation>> positions = findByUsers(UserAssociationCategory.POSITION, users);
        Map<Long, List<UserAssociation>> roles = findByUsers(UserAssociationCategory.ROLE, users);
        return users.stream().collect(
                Collectors.toMap(
                        Function.identity(),
                        user -> {
                            Map<UserAssociationCategory, List<UserAssociation>> element = new HashMap<>();
                            element.put(UserAssociationCategory.ORGANIZATION, organizations.getOrDefault(user, Collections.emptyList()));
                            element.put(UserAssociationCategory.POSITION, positions.getOrDefault(user, Collections.emptyList()));
                            element.put(UserAssociationCategory.ROLE, roles.getOrDefault(user, Collections.emptyList()));
                            return element;
                        }
                )
        );
    }

    @Override
    public Map<Long, List<UserAssociation>> findByUsers(@NonNull UserAssociationCategory category, Collection<Long> users) {
        if (CollectionUtils.isEmpty(users)) {
            return Collections.emptyMap();
        }
        LambdaQueryWrapper<UserAssociation> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(UserAssociation::getUser, users);
        wrapper.eq(UserAssociation::getCategory, Objects.requireNonNull(category));
        List<UserAssociation> associations = mapper.selectList(wrapper);
        Map<Long, String> associatedNames = getAssociatedNames(category, associations.stream().map(UserAssociation::getAssociated).collect(Collectors.toList()));
        associations.forEach(e -> e.setAssociatedName(associatedNames.get(e.getAssociated())));
        return associations.stream().collect(Collectors.groupingBy(UserAssociation::getUser, Collectors.toList()));
    }

    private Map<Long, String> getAssociatedNames(UserAssociationCategory category, List<Long> associatedIds) {
        Map<Long, String> associatedNames = Collections.emptyMap();
        switch (category) {
            case ORGANIZATION -> associatedNames = organizationService.findNamesByIds(associatedIds);
            case POSITION -> associatedNames = positionService.findNamesByIds(associatedIds);
            case ROLE -> associatedNames = roleService.findNamesByIds(associatedIds);
        }
        return associatedNames;
    }
}
