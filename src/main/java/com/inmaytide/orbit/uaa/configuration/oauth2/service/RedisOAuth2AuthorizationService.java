package com.inmaytide.orbit.uaa.configuration.oauth2.service;

import com.inmaytide.orbit.commons.consts.CacheNames;
import com.inmaytide.orbit.uaa.configuration.oauth2.authentication.OAuth2ResourceOwnerPasswordAuthenticationProvider;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RedisOAuth2AuthorizationService implements OAuth2AuthorizationService {

    private final Map<String, OAuth2Authorization> initializedAuthorizations = new ConcurrentHashMap<>();

    private final RedisTemplate<String, OAuth2Authorization> authorizationStore;

    private final RedisTemplate<String, OAuth2AccessToken> accessTokenStore;

    public RedisOAuth2AuthorizationService(RedisTemplate<String, OAuth2Authorization> authorizationStore, RedisTemplate<String, OAuth2AccessToken> accessTokenStore) {
        this.authorizationStore = authorizationStore;
        this.accessTokenStore = accessTokenStore;
    }

    private String getAuthorizationStoreKey(String id) {
        return CacheNames.AUTHORIZATION_STORE.getValue() + "::" + id;
    }

    private String getAccessTokenStoreKey(OAuth2AccessToken accessToken) {
        return CacheNames.ACCESS_TOKEN_STORE.getValue() + "::" + accessToken.getTokenValue();
    }

    private void storeAuthorization(OAuth2Authorization authorization) {
        OAuth2AccessToken accessToken = authorization.getAccessToken().getToken();
        OAuth2RefreshToken refreshToken = authorization.getRefreshToken() == null ? null : authorization.getRefreshToken().getToken();
        authorizationStore.opsForValue().set(getAuthorizationStoreKey(authorization.getId()), authorization, Duration.between(Instant.now(), refreshToken == null ? accessToken.getExpiresAt() : refreshToken.getExpiresAt()));
        storeAccessToken(accessToken);
    }

    private void storeAccessToken(OAuth2AccessToken accessToken) {
        accessTokenStore.opsForValue().set(getAccessTokenStoreKey(accessToken), accessToken, Duration.between(Instant.now(), accessToken.getExpiresAt()));
    }

    @Override
    public void save(OAuth2Authorization authorization) {
        Assert.notNull(authorization, "Authorization cannot be null");
        if (isComplete(authorization)) {
            if (authorization.getRefreshToken() != null) {
                OAuth2Authorization old = findByToken(authorization.getRefreshToken().getToken().getTokenValue(), OAuth2TokenType.REFRESH_TOKEN);
                if (old != null) {
                    authorizationStore.delete(getAuthorizationStoreKey(old.getId()));
                }
            }
            storeAuthorization(authorization);
        } else {
            this.initializedAuthorizations.put(authorization.getId(), authorization);
        }
    }

    @Override
    public void remove(OAuth2Authorization authorization) {
        Assert.notNull(authorization, "authorization cannot be null");
        if (isComplete(authorization)) {
            authorizationStore.delete(getAuthorizationStoreKey(authorization.getId()));
            accessTokenStore.delete(getAccessTokenStoreKey(authorization.getAccessToken().getToken()));
        } else {
            this.initializedAuthorizations.remove(authorization.getId(), authorization);
        }
    }

    @Nullable
    @Override
    public OAuth2Authorization findById(String id) {
        Assert.hasText(id, "id cannot be empty");
        OAuth2Authorization authorization = authorizationStore.opsForValue().get(getAuthorizationStoreKey(id));
        return authorization == null ? initializedAuthorizations.get(id) : authorization;
    }

    @Nullable
    @Override
    public OAuth2Authorization findByToken(String token, @Nullable OAuth2TokenType tokenType) {
        Assert.hasText(token, "token cannot be empty");
        Set<String> keys = authorizationStore.keys(CacheNames.AUTHORIZATION_STORE.getValue() + "::*");
        if (CollectionUtils.isNotEmpty(keys)) {
            for (String key : keys) {
                OAuth2Authorization authorization = authorizationStore.opsForValue().get(key);
                if (authorization != null && hasToken(authorization, token, tokenType)) {
                    return authorization;
                }
            }
        }
        for (OAuth2Authorization authorization : this.initializedAuthorizations.values()) {
            if (hasToken(authorization, token, tokenType)) {
                return authorization;
            }
        }
        return null;
    }

    public boolean matchUsernameAndPlatform(OAuth2Authorization authorization, String username, String platform) {
        if (authorization == null) {
            return false;
        }
        return Objects.equals(username, authorization.getAttribute(OAuth2ParameterNames.USERNAME))
                && Objects.equals(platform, authorization.getAttribute(OAuth2ResourceOwnerPasswordAuthenticationProvider.PARAMETER_NAME_PLATFORM));

    }

    @NonNull
    public List<OAuth2Authorization> findByUsernameAndPlatform(String username, String platform) {
        Assert.hasText(username, "username can't be empty");
        Assert.hasText(platform, "platform can't be empty");
        Set<String> keys = authorizationStore.keys(CacheNames.AUTHORIZATION_STORE.getValue() + "::*");
        List<OAuth2Authorization> res = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(keys)) {
            for (String key : keys) {
                OAuth2Authorization authorization = authorizationStore.opsForValue().get(key);
                if (matchUsernameAndPlatform(authorization, username, platform)) {
                    res.add(authorization);
                }
            }
        }
        for (OAuth2Authorization authorization : this.initializedAuthorizations.values()) {
            if (matchUsernameAndPlatform(authorization, username, platform)) {
                res.add(authorization);
            }
        }
        return res;
    }

    private static boolean isComplete(OAuth2Authorization authorization) {
        return authorization.getAccessToken() != null;
    }

    private static boolean hasToken(OAuth2Authorization authorization, String token, @Nullable OAuth2TokenType tokenType) {
        if (tokenType == null) {
            return matchesState(authorization, token) ||
                    matchesAuthorizationCode(authorization, token) ||
                    matchesAccessToken(authorization, token) ||
                    matchesRefreshToken(authorization, token);
        } else if (OAuth2ParameterNames.STATE.equals(tokenType.getValue())) {
            return matchesState(authorization, token);
        } else if (OAuth2ParameterNames.CODE.equals(tokenType.getValue())) {
            return matchesAuthorizationCode(authorization, token);
        } else if (OAuth2TokenType.ACCESS_TOKEN.equals(tokenType)) {
            return matchesAccessToken(authorization, token);
        } else if (OAuth2TokenType.REFRESH_TOKEN.equals(tokenType)) {
            return matchesRefreshToken(authorization, token);
        }
        return false;
    }

    private static boolean matchesState(OAuth2Authorization authorization, String token) {
        return token.equals(authorization.getAttribute(OAuth2ParameterNames.STATE));
    }

    private static boolean matchesAuthorizationCode(OAuth2Authorization authorization, String token) {
        OAuth2Authorization.Token<OAuth2AuthorizationCode> authorizationCode =
                authorization.getToken(OAuth2AuthorizationCode.class);
        return authorizationCode != null && authorizationCode.getToken().getTokenValue().equals(token);
    }

    private static boolean matchesAccessToken(OAuth2Authorization authorization, String token) {
        OAuth2Authorization.Token<OAuth2AccessToken> accessToken =
                authorization.getToken(OAuth2AccessToken.class);
        return accessToken != null && accessToken.getToken().getTokenValue().equals(token);
    }

    private static boolean matchesRefreshToken(OAuth2Authorization authorization, String token) {
        OAuth2Authorization.Token<OAuth2RefreshToken> refreshToken =
                authorization.getToken(OAuth2RefreshToken.class);
        return refreshToken != null && refreshToken.getToken().getTokenValue().equals(token);
    }

}
