package com.inmaytide.orbit.uaa.configuration.oauth2.store;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inmaytide.orbit.commons.constants.Constants;
import com.inmaytide.orbit.commons.utils.ApplicationContextHolder;
import com.inmaytide.orbit.commons.utils.ValueCaches;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author inmaytide
 * @since 2024/1/25
 */
@Component
@ConditionalOnMissingBean(OAuth2AuthorizationStore.class)
public class RedisOAuth2AuthorizationStore implements OAuth2AuthorizationStore {

    @Override
    public void store(OAuth2Authorization authorization) {
        OAuth2AccessToken accessToken = authorization.getAccessToken().getToken();
        OAuth2RefreshToken refreshToken = authorization.getRefreshToken() == null ? null : authorization.getRefreshToken().getToken();
        long timeout = Duration.between(Instant.now(), refreshToken == null ? accessToken.getExpiresAt() : refreshToken.getExpiresAt()).getSeconds();
        ObjectMapper mapper = ApplicationContextHolder.getInstance().getBean(ObjectMapper.class);
        try {
            String value = mapper.writeValueAsString(authorization);
            ValueCaches.put(Constants.CacheNames.AUTHORIZATION_STORE, authorization.getId(), value, timeout, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(OAuth2Authorization authorization) {
        ValueCaches.delete(Constants.CacheNames.AUTHORIZATION_STORE, authorization.getId());
    }

    @Override
    public Optional<OAuth2Authorization> get(String id) {
        return ValueCaches.getFor(Constants.CacheNames.AUTHORIZATION_STORE, id, OAuth2Authorization.class);
    }

    @Override
    public List<OAuth2Authorization> all() {
        return ValueCaches.keys(Constants.CacheNames.AUTHORIZATION_STORE)
                .stream()
                .map(key -> StringUtils.removeStart(key, Constants.CacheNames.AUTHORIZATION_STORE + "::"))
                .map(this::get)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

}
