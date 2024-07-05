package com.inmaytide.orbit.uaa.service.account.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inmaytide.orbit.commons.constants.Platforms;
import com.inmaytide.orbit.commons.utils.ValueCaches;
import com.inmaytide.orbit.uaa.domain.account.User;
import com.inmaytide.orbit.uaa.domain.account.UserActivity;
import com.inmaytide.orbit.uaa.mapper.account.UserActivityMapper;
import com.inmaytide.orbit.uaa.mapper.account.UserMapper;
import com.inmaytide.orbit.uaa.service.account.UserActivityService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenIntrospection;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

import static com.inmaytide.orbit.commons.constants.Constants.CacheNames.USER_ACTIVITY;
import static com.inmaytide.orbit.commons.utils.HttpUtils.getClientIpAddress;

/**
 * @author inmaytide
 * @since 2024/4/29
 */
@Service
public class UserActivityServiceImpl implements UserActivityService {

    private static final Logger log = LoggerFactory.getLogger(UserActivityService.class);

    private final ObjectMapper objectMapper;

    private final UserMapper userMapper;

    private final UserActivityMapper baseMapper;

    public UserActivityServiceImpl(ObjectMapper objectMapper, UserMapper userMapper, UserActivityMapper baseMapper) {
        this.objectMapper = objectMapper;
        this.userMapper = userMapper;
        this.baseMapper = baseMapper;
    }

    @Override
    public void alterUserActivity(HttpServletRequest request, OAuth2TokenIntrospection tokenClaims) {
        try {
            Platforms platform = Platforms.valueOf(tokenClaims.getClaimAsString("platform"));
            Long userId = NumberUtils.createLong(tokenClaims.getUsername());
            try {
                UserActivity activity = ValueCaches
                        .getFor(USER_ACTIVITY, getUserActivityCacheKey(platform, userId), UserActivity.class)
                        .orElseGet(UserActivity::new);
                activity.setUser(userId);
                activity.setLastActivityTime(Instant.now());
                activity.setIpAddress(getClientIpAddress(request));
                activity.setPlatform(platform);
                ValueCaches.put(USER_ACTIVITY, getUserActivityCacheKey(platform, userId), objectMapper.writeValueAsString(activity));
            } catch (Exception e) {
                log.error("Record User{id = {}, platform = {}} activity failed", userId, platform.name(), e);
            }
        } catch (Exception e) {
            log.error("Get platform and userId from authentication failed", e);
        }
    }

    private String getUserActivityCacheKey(Platforms platform, Long userId) {
        return platform.name() + "::" + userId;
    }

    @Override
    public Long getNumberOfOnlineUsers(Long tenant) {
        List<Long> all = ValueCaches.keys(USER_ACTIVITY).stream().map(e -> StringUtils.split(e, "::")[1])
                .map(NumberUtils::createLong)
                .toList();
        if (all.isEmpty()) {
            return 0L;
        }
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(User::getId, all);
        wrapper.eq(User::getTenant, tenant);
        return userMapper.selectCount(wrapper);
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
