package com.inmaytide.orbit.uaa.service.account.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.inmaytide.orbit.uaa.consts.UserAssociationCategory;
import com.inmaytide.orbit.uaa.domain.account.UserAssociation;
import com.inmaytide.orbit.uaa.mapper.account.UserAssociationMapper;
import com.inmaytide.orbit.uaa.service.account.UserAssociationService;
import org.springframework.stereotype.Service;

import java.util.List;

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

}
