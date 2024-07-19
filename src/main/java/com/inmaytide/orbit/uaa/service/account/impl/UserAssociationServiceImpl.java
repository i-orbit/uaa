package com.inmaytide.orbit.uaa.service.account.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.inmaytide.orbit.uaa.consts.UserAssociationCategory;
import com.inmaytide.orbit.uaa.domain.account.Position;
import com.inmaytide.orbit.uaa.domain.account.User;
import com.inmaytide.orbit.uaa.domain.account.UserAssociation;
import com.inmaytide.orbit.uaa.domain.permission.Organization;
import com.inmaytide.orbit.uaa.domain.permission.Role;
import com.inmaytide.orbit.uaa.mapper.account.UserAssociationMapper;
import com.inmaytide.orbit.uaa.service.account.UserAssociationService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author inmaytide
 * @since 2024/2/23
 */
@Service
public class UserAssociationServiceImpl implements UserAssociationService {

    private final UserAssociationMapper baseMapper;

    public UserAssociationServiceImpl(UserAssociationMapper baseMapper) {
        this.baseMapper = baseMapper;
    }

    @Override
    public List<UserAssociation> findByUserAndCategory(Long user, UserAssociationCategory category) {
        LambdaQueryWrapper<UserAssociation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserAssociation::getUser, user);
        wrapper.eq(UserAssociation::getCategory, category);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public Map<Long, Map<UserAssociationCategory, List<UserAssociation>>> findByUsers(List<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return Collections.emptyMap();
        }
        LambdaQueryWrapper<UserAssociation> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(UserAssociation::getUser, userIds);
        return baseMapper.selectList(wrapper).stream().collect(
                Collectors.groupingBy(
                        UserAssociation::getUser,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> list.stream().collect(Collectors.groupingBy(UserAssociation::getCategory, Collectors.toList()))
                        )
                ));
    }

    @Override
    public void erase(Long userId) {
        LambdaQueryWrapper<UserAssociation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserAssociation::getUser, Objects.requireNonNull(userId));
        baseMapper.delete(wrapper);
    }

    @Override
    public void erase(Long userId, UserAssociationCategory category) {
        LambdaQueryWrapper<UserAssociation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserAssociation::getUser, Objects.requireNonNull(userId));
        wrapper.eq(UserAssociation::getCategory, Objects.requireNonNull(category));
        baseMapper.delete(wrapper);
    }

    @Override
    @Transactional
    public void persist(User user) {
        erase(user.getId());
        List<UserAssociation> associations = new ArrayList<>();
        for (Organization e : user.getOrganizations()) {
            boolean defaulted = Objects.equals(user.getDefaultOrganization(), e.getId());
            associations.add(UserAssociation.builder(UserAssociationCategory.ORGANIZATION).user(user).associated(e).defaulted(defaulted).build());
        }
        for (Position e : user.getPositions()) {
            boolean defaulted = Objects.equals(user.getDefaultPosition(), e.getId());
            associations.add(UserAssociation.builder(UserAssociationCategory.POSITION).user(user).associated(e).defaulted(defaulted).build());
        }
        for (Role e : user.getRoles()) {
            associations.add(UserAssociation.builder(UserAssociationCategory.ROLE).user(user).associated(e).build());
        }
        if (!associations.isEmpty()) {
            associations.forEach(baseMapper::insert);
        }
    }
}
