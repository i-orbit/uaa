package com.inmaytide.orbit.uaa.service.account.impl;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inmaytide.orbit.uaa.domain.account.UserActivity;
import com.inmaytide.orbit.uaa.mapper.account.UserActivityMapper;
import com.inmaytide.orbit.uaa.service.account.UserActivityService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author inmaytide
 * @since 2024/4/29
 */
@Service
public class UserActivityServiceImpl implements UserActivityService {

    private static final Logger log = LoggerFactory.getLogger(UserActivityService.class);

    private final UserActivityMapper baseMapper;

    public UserActivityServiceImpl(UserActivityMapper baseMapper) {
        this.baseMapper = baseMapper;
    }

    @Override
    public void persist(List<UserActivity> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return;
        }
        entities.forEach(baseMapper::insert);
    }

    @Override
    public BaseMapper<UserActivity> getBaseMapper() {
        return baseMapper;
    }
}
