package com.inmaytide.orbit.uaa.service.account.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.inmaytide.orbit.uaa.consts.UserAssociationCategory;
import com.inmaytide.orbit.uaa.domain.account.UserAssociation;
import com.inmaytide.orbit.uaa.mapper.account.UserAssociationMapper;
import com.inmaytide.orbit.uaa.service.account.UserAssociationService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author inmaytide
 * @since 2024/2/23
 */
@Service
public class UserAssociationServiceImpl implements UserAssociationService {

    private final UserAssociationMapper mapper;

    public UserAssociationServiceImpl(UserAssociationMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<UserAssociation> findByUserAndCategory(Long user, UserAssociationCategory category) {
        LambdaQueryWrapper<UserAssociation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserAssociation::getUser, user);
        wrapper.eq(UserAssociation::getCategory, category);
        return mapper.selectList(wrapper);
    }

    @Override
    public Map<Long, Map<UserAssociationCategory, List<UserAssociation>>> findByUsers(List<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return Collections.emptyMap();
        }
        LambdaQueryWrapper<UserAssociation> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(UserAssociation::getUser, userIds);
        return mapper.selectList(wrapper).stream().collect(
                Collectors.groupingBy(
                        UserAssociation::getUser,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> list.stream().collect(Collectors.groupingBy(UserAssociation::getCategory, Collectors.toList()))
                        )
                ));
    }

}
