package com.inmaytide.orbit.uaa.security.oauth2.authentication;

import com.inmaytide.exception.web.AccessDeniedException;
import com.inmaytide.orbit.commons.constants.Bool;
import com.inmaytide.orbit.commons.constants.Constants;
import com.inmaytide.orbit.commons.constants.Platforms;
import com.inmaytide.orbit.commons.domain.SystemUser;
import com.inmaytide.orbit.commons.security.SecurityUtils;
import com.inmaytide.orbit.commons.utils.ApplicationContextHolder;
import com.inmaytide.orbit.uaa.configuration.ApplicationProperties;
import com.inmaytide.orbit.uaa.configuration.ErrorCode;
import com.inmaytide.orbit.uaa.security.oauth2.service.ExtensibleOAuth2AuthorizationService;
import com.inmaytide.orbit.uaa.service.account.UserService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

import java.security.Principal;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class OAuth2PasswordAuthenticationProvider implements AuthenticationProvider {

    private static final Logger LOG = LoggerFactory.getLogger(OAuth2PasswordAuthenticationProvider.class);

    private static final StringKeyGenerator DEFAULT_TOKEN_GENERATOR = new Base64StringKeyGenerator(Base64.getUrlEncoder().withoutPadding(), 96);

    public static final String PARAMETER_NAME_PLATFORM = "platform";

    public static final String PARAMETER_NAME_FORCED_REPLACEMENT = "forcedReplacement";

    private final AuthenticationManager authenticationManager;

    private final OAuth2AuthorizationService authorizationService;

    private final ApplicationProperties properties;

    public OAuth2PasswordAuthenticationProvider(AuthenticationManager authenticationManager, OAuth2AuthorizationService authorizationService, ApplicationProperties properties) {
        this.authenticationManager = authenticationManager;
        this.authorizationService = authorizationService;
        this.properties = properties;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        OAuth2PasswordAuthenticationToken resourceOwnerPasswordAuthentication = (OAuth2PasswordAuthenticationToken) authentication;
        OAuth2ClientAuthenticationToken clientPrincipal = getAuthenticatedClientElseThrowInvalidClient(resourceOwnerPasswordAuthentication);
        RegisteredClient registeredClient = clientPrincipal.getRegisteredClient();

        if (registeredClient == null || !registeredClient.getAuthorizationGrantTypes().contains(AuthorizationGrantType.PASSWORD)) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.UNAUTHORIZED_CLIENT);
        }
        Map<String, Object> additionalParameters = resourceOwnerPasswordAuthentication.getAdditionalParameters();
        String username = (String) additionalParameters.get(OAuth2ParameterNames.USERNAME);
        String password = (String) additionalParameters.get(OAuth2ParameterNames.PASSWORD);
        Platforms platform = Platforms.valueOf((String) additionalParameters.get(PARAMETER_NAME_PLATFORM));

        // 当用户在其他地方已登录时, 是否强制登录
        Object value = additionalParameters.get(PARAMETER_NAME_FORCED_REPLACEMENT);
        if (value == null || !Pattern.compile("[YN]").asPredicate().test(value.toString())) {
            value = "N";
        }
        Bool forcedReplacement = Bool.valueOf(value.toString());

        // 免密码登录标记
        if (Objects.equals(password, Constants.Markers.LOGIN_WITHOUT_PASSWORD)) {
            username += Constants.Markers.LOGIN_WITHOUT_PASSWORD;
        }

        try {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, password);
            LOG.debug("Got UsernamePasswordAuthenticationToken={}", usernamePasswordAuthenticationToken);

            Authentication usernamePasswordAuthentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

            if (usernamePasswordAuthentication.isAuthenticated() && authorizationService instanceof ExtensibleOAuth2AuthorizationService service) {
                // 移除已过期的
                List<OAuth2Authorization> authorizations = service.findByUsernameAndPlatform(usernamePasswordAuthentication.getName(), platform);
                authorizations.stream().filter(e -> e.getAccessToken().isExpired()).forEach(authorizationService::remove);
                authorizations = service.findByUsernameAndPlatform(usernamePasswordAuthentication.getName(), platform);

                if (CollectionUtils.isNotEmpty(authorizations)) {
                    // 不允许用户在多个地方同时登录
                    if (!properties.isAllowUsersToLoginSimultaneously()) {
                        // 当用户在其他地方已登录时, 强制登录
                        if (Bool.Y == forcedReplacement) {
                            // 重新登录时将上一次登陆的authorization移除，并将token标记为强制登出
                            for (OAuth2Authorization authorization : authorizations) {
                                service.remove(authorization);
                            }
                        } else {
                            throw new AccessDeniedException(ErrorCode.E_0x00100001);
                        }
                    }
                }
            }

            Set<String> authorizedScopes = registeredClient.getScopes();        // Default to configured scopes

            if (!CollectionUtils.isEmpty(resourceOwnerPasswordAuthentication.getScopes())) {
                Set<String> unauthorizedScopes = resourceOwnerPasswordAuthentication.getScopes().stream()
                        .filter(requestedScope -> !registeredClient.getScopes().contains(requestedScope))
                        .collect(Collectors.toSet());
                if (!CollectionUtils.isEmpty(unauthorizedScopes)) {
                    throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_SCOPE);
                }

                authorizedScopes = new LinkedHashSet<>(resourceOwnerPasswordAuthentication.getScopes());
            }

            OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER,
                    DEFAULT_TOKEN_GENERATOR.generateKey(),
                    Instant.now(),
                    Instant.now().plusSeconds(registeredClient.getTokenSettings().getAccessTokenTimeToLive().toSeconds()),
                    authorizedScopes);

            OAuth2RefreshToken refreshToken = null;
            if (registeredClient.getAuthorizationGrantTypes().contains(AuthorizationGrantType.REFRESH_TOKEN)) {
                refreshToken = generateRefreshToken(registeredClient.getTokenSettings().getRefreshTokenTimeToLive());
            }

            OAuth2Authorization.Builder authorizationBuilder = OAuth2Authorization.withRegisteredClient(registeredClient)
                    .principalName(usernamePasswordAuthentication.getName())
                    .authorizationGrantType(AuthorizationGrantType.PASSWORD)
                    .accessToken(accessToken)
                    .authorizedScopes(authorizedScopes)
                    .attribute(PARAMETER_NAME_PLATFORM, platform.name())
                    .attribute(Principal.class.getName(), usernamePasswordAuthentication);


            if (refreshToken != null) {
                authorizationBuilder.refreshToken(refreshToken);
            }

            authorizationService.save(authorizationBuilder.build());
            LOG.debug("OAuth2Authorization saved successfully");
            LOG.debug("Returning OAuth2AccessTokenAuthenticationToken");

            Map<String, Object> additionalResponseValues = new HashMap<>();
            ApplicationContextHolder.getInstance().getBean(UserService.class).findByLoginName(username).ifPresent(user -> {
                additionalResponseValues.put("userId", user.getId());
                additionalResponseValues.put("tenant", user.getTenant());
                additionalResponseValues.put("username", user.getName());
            });
            return new OAuth2AccessTokenAuthenticationToken(registeredClient, clientPrincipal, accessToken, refreshToken, additionalResponseValues);

        } catch (Exception ex) {
            if (ex instanceof BadCredentialsException) {
                throw new OAuth2AuthenticationException(
                        new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST),
                        new com.inmaytide.exception.web.BadCredentialsException(ErrorCode.E_0x00100003)
                );
            }
            throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR), ex);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OAuth2PasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private OAuth2ClientAuthenticationToken getAuthenticatedClientElseThrowInvalidClient(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() != null && OAuth2ClientAuthenticationToken.class.isAssignableFrom(authentication.getPrincipal().getClass())) {
            OAuth2ClientAuthenticationToken principal = (OAuth2ClientAuthenticationToken) authentication.getPrincipal();
            if (principal.isAuthenticated()) {
                return principal;
            }
        }
        throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_CLIENT);
    }

    private OAuth2RefreshToken generateRefreshToken(Duration tokenTimeToLive) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(tokenTimeToLive);
        return new OAuth2RefreshToken(DEFAULT_TOKEN_GENERATOR.generateKey(), issuedAt, expiresAt);
    }

}
