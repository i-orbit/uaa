package com.inmaytide.orbit.uaa.configuration.oauth2.service;

import com.inmaytide.orbit.commons.constants.Platforms;
import com.inmaytide.orbit.uaa.configuration.oauth2.authentication.OAuth2ResourceOwnerPasswordAuthenticationProvider;
import com.inmaytide.orbit.uaa.configuration.oauth2.store.OAuth2AccessTokenStore;
import com.inmaytide.orbit.uaa.configuration.oauth2.store.OAuth2AuthorizationStore;
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

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author inmaytide
 * @since 2023/04/29
 */
@Component
public class RedisOAuth2AuthorizationService implements OAuth2AuthorizationService {

    private final Map<String, OAuth2Authorization> initializedAuthorizations = new ConcurrentHashMap<>();

    private final OAuth2AuthorizationStore authorizationStore;

    private final OAuth2AccessTokenStore accessTokenStore;

    public RedisOAuth2AuthorizationService(OAuth2AuthorizationStore authorizationStore, OAuth2AccessTokenStore accessTokenStore) {
        this.authorizationStore = authorizationStore;
        this.accessTokenStore = accessTokenStore;
    }

    private void storeAuthorization(OAuth2Authorization authorization) {
        authorizationStore.store(authorization);
        accessTokenStore.store(authorization.getAccessToken().getToken());
    }

    @Override
    public void save(OAuth2Authorization authorization) {
        Assert.notNull(authorization, "Authorization cannot be null");
        if (isComplete(authorization)) {
            if (authorization.getRefreshToken() != null) {
                OAuth2Authorization old = findByToken(authorization.getRefreshToken().getToken().getTokenValue(), OAuth2TokenType.REFRESH_TOKEN);
                if (old != null) {
                    authorizationStore.remove(old);
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
            authorizationStore.remove(authorization);
            accessTokenStore.remove(authorization.getAccessToken().getToken());
        } else {
            this.initializedAuthorizations.remove(authorization.getId(), authorization);
        }
    }

    @Nullable
    @Override
    public OAuth2Authorization findById(String id) {
        Assert.hasText(id, "id cannot be empty");
        return authorizationStore.get(id).orElse(initializedAuthorizations.get(id));
    }

    @Nullable
    @Override
    public OAuth2Authorization findByToken(String token, @Nullable OAuth2TokenType tokenType) {
        Assert.hasText(token, "token cannot be empty");
        return Stream.concat(authorizationStore.all().stream(), initializedAuthorizations.values().stream())
                .filter(authorization -> hasToken(authorization, token, tokenType))
                .findFirst()
                .orElse(null);
    }

    public boolean matchUsernameAndPlatform(OAuth2Authorization authorization, String username, Platforms platform) {
        if (authorization == null) {
            return false;
        }
        return Objects.equals(username, authorization.getPrincipalName())
                && platform == Platforms.valueOf(authorization.getAttribute(OAuth2ResourceOwnerPasswordAuthenticationProvider.PARAMETER_NAME_PLATFORM));

    }

    @NonNull
    public List<OAuth2Authorization> findByUsernameAndPlatform(String username, Platforms platform) {
        Assert.hasText(username, "username can't be empty");
        Assert.notNull(platform, "platform can't be null");
        return Stream.concat(authorizationStore.all().stream(), initializedAuthorizations.values().stream())
                .filter(authorization -> matchUsernameAndPlatform(authorization, username, platform))
                .collect(Collectors.toList());
    }

    private static boolean isComplete(OAuth2Authorization authorization) {
        return authorization.getAccessToken() != null;
    }

    private static boolean hasToken(OAuth2Authorization authorization, String token, @Nullable OAuth2TokenType tokenType) {
        if (tokenType == null) {
            return matchesState(authorization, token) || matchesAuthorizationCode(authorization, token) || matchesAccessToken(authorization, token) || matchesRefreshToken(authorization, token);
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
        OAuth2Authorization.Token<OAuth2AuthorizationCode> authorizationCode = authorization.getToken(OAuth2AuthorizationCode.class);
        return authorizationCode != null && authorizationCode.getToken().getTokenValue().equals(token);
    }

    private static boolean matchesAccessToken(OAuth2Authorization authorization, String token) {
        OAuth2Authorization.Token<OAuth2AccessToken> accessToken = authorization.getToken(OAuth2AccessToken.class);
        return accessToken != null && accessToken.getToken().getTokenValue().equals(token);
    }

    private static boolean matchesRefreshToken(OAuth2Authorization authorization, String token) {
        OAuth2Authorization.Token<OAuth2RefreshToken> refreshToken = authorization.getToken(OAuth2RefreshToken.class);
        return refreshToken != null && refreshToken.getToken().getTokenValue().equals(token);
    }

}
