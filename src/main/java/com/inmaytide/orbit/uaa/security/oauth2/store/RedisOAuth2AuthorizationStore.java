package com.inmaytide.orbit.uaa.security.oauth2.store;

import com.inmaytide.orbit.commons.constants.Constants;
import com.inmaytide.orbit.commons.utils.ValueCaches;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * @author inmaytide
 * @since 2024/1/25
 */
public class RedisOAuth2AuthorizationStore implements OAuth2AuthorizationStore {

    private final RedisTemplate<String, OAuth2Authorization> redisTemplate;

    public RedisOAuth2AuthorizationStore(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, OAuth2Authorization> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(StringRedisSerializer.UTF_8);
        template.setValueSerializer(new JdkSerializationRedisSerializer());
        template.opsForValue();
        template.afterPropertiesSet();
        this.redisTemplate = template;
    }

    @Override
    public void store(OAuth2Authorization authorization) {
        OAuth2AccessToken accessToken = authorization.getAccessToken().getToken();
        OAuth2RefreshToken refreshToken = authorization.getRefreshToken() == null ? null : authorization.getRefreshToken().getToken();
        long timeout = Duration.between(Instant.now(), refreshToken == null ? accessToken.getExpiresAt() : refreshToken.getExpiresAt()).getSeconds();
        redisTemplate.opsForValue().set(ValueCaches.getCacheKey(Constants.CacheNames.AUTHORIZATION_STORE, authorization.getId()), authorization, timeout, TimeUnit.SECONDS);
    }

    @Override
    public void remove(OAuth2Authorization authorization) {
        redisTemplate.delete(ValueCaches.getCacheKey(Constants.CacheNames.AUTHORIZATION_STORE, authorization.getId()));
    }

    @Override
    public Optional<OAuth2Authorization> get(String id) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(ValueCaches.getCacheKey(Constants.CacheNames.AUTHORIZATION_STORE, id)));
    }

    @Override
    public List<OAuth2Authorization> all() {
        Set<String> keys = redisTemplate.keys(Constants.CacheNames.AUTHORIZATION_STORE + "::*");
        if (keys == null || keys.isEmpty()) {
            return Collections.emptyList();
        }
        return keys.stream()
                .map(key -> StringUtils.removeStart(key, Constants.CacheNames.AUTHORIZATION_STORE + "::"))
                .map(this::get)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

}
