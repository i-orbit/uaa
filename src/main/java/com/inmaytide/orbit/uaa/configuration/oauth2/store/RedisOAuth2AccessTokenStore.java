package com.inmaytide.orbit.uaa.configuration.oauth2.store;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inmaytide.orbit.commons.constants.Constants;
import com.inmaytide.orbit.commons.utils.ApplicationContextHolder;
import com.inmaytide.orbit.commons.utils.ValueCaches;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.stereotype.Component;

import java.io.Serializable;
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
@ConditionalOnMissingBean(OAuth2AccessTokenStore.class)
public class RedisOAuth2AccessTokenStore implements OAuth2AccessTokenStore {
    @Override
    public void store(OAuth2AccessToken token) {
        long timeout = Duration.between(Instant.now(), token.getExpiresAt()).getSeconds();
        ObjectMapper mapper = ApplicationContextHolder.getInstance().getBean(ObjectMapper.class);
        try {
            String value = mapper.writeValueAsString(token);
            ValueCaches.put(Constants.CacheNames.ACCESS_TOKEN_STORE, token.getTokenValue(), value, timeout, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(OAuth2AccessToken token) {
        ValueCaches.delete(Constants.CacheNames.ACCESS_TOKEN_STORE, token.getTokenValue());
    }

    @Override
    public Optional<OAuth2AccessToken> get(String token) {
        return ValueCaches.getFor(Constants.CacheNames.ACCESS_TOKEN_STORE, token, OAuth2AccessToken.class);
    }

    @Override
    public List<OAuth2AccessToken> all() {
        return ValueCaches.keys(Constants.CacheNames.ACCESS_TOKEN_STORE)
                .stream()
                .map(key -> StringUtils.removeStart(key, Constants.CacheNames.ACCESS_TOKEN_STORE + "::"))
                .map(this::get)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

}
