package com.inmaytide.orbit.uaa.configuration.oauth2.authentication;

import com.inmaytide.exception.web.HttpResponseException;
import com.inmaytide.orbit.commons.consts.Marks;
import com.inmaytide.orbit.uaa.configuration.ApplicationProperties;
import com.inmaytide.orbit.uaa.configuration.oauth2.service.RedisOAuth2AuthorizationService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
import java.util.stream.Collectors;

import static com.carrot.commons.consts.Constants.CacheNames.REFRESH_TOKEN;
import static com.carrot.commons.consts.Constants.Marks.USER_FORCE_LOGOUT;

public class OAuth2ResourceOwnerPasswordAuthenticationProvider implements AuthenticationProvider {

    private static final Logger LOG = LoggerFactory.getLogger(OAuth2ResourceOwnerPasswordAuthenticationProvider.class);

    private static final StringKeyGenerator DEFAULT_TOKEN_GENERATOR = new Base64StringKeyGenerator(Base64.getUrlEncoder().withoutPadding(), 96);

    public static final String PARAMETER_NAME_PLATFORM = "platform";

    private final AuthenticationManager authenticationManager;

    private final OAuth2AuthorizationService authorizationService;

    private final ApplicationProperties properties;

    public OAuth2ResourceOwnerPasswordAuthenticationProvider(AuthenticationManager authenticationManager, OAuth2AuthorizationService authorizationService, ApplicationProperties properties) {
        this.authenticationManager = authenticationManager;
        this.authorizationService = authorizationService;
        this.properties = properties;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        OAuth2ResourceOwnerPasswordAuthenticationToken resourceOwnerPasswordAuthentication = (OAuth2ResourceOwnerPasswordAuthenticationToken) authentication;
        OAuth2ClientAuthenticationToken clientPrincipal = getAuthenticatedClientElseThrowInvalidClient(resourceOwnerPasswordAuthentication);
        RegisteredClient registeredClient = clientPrincipal.getRegisteredClient();

        if (registeredClient == null || !registeredClient.getAuthorizationGrantTypes().contains(AuthorizationGrantType.PASSWORD)) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.UNAUTHORIZED_CLIENT);
        }
        Map<String, Object> additionalParameters = resourceOwnerPasswordAuthentication.getAdditionalParameters();
        String username = (String) additionalParameters.get(OAuth2ParameterNames.USERNAME);
        String password = (String) additionalParameters.get(OAuth2ParameterNames.PASSWORD);
        String platform = (String) additionalParameters.get(PARAMETER_NAME_PLATFORM);

        // APP扫码免密码登录标记
        if (Objects.equals(password, Marks.LOGIN_WITHOUT_PASSWORD.getValue())) {
            username += Marks.LOGIN_WITHOUT_PASSWORD.getValue();
        }

        try {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, password);
            LOG.debug("got usernamePasswordAuthenticationToken=" + usernamePasswordAuthenticationToken);

            Authentication usernamePasswordAuthentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

            if (usernamePasswordAuthentication.isAuthenticated() && authorizationService instanceof RedisOAuth2AuthorizationService) {
                RedisOAuth2AuthorizationService service = (RedisOAuth2AuthorizationService) authorizationService;
                List<OAuth2Authorization> authorizations = service.findByUsernameAndPlatform(username, platform);

                if (properties.isEnableSuperAdministrator()
                        && properties.isAllowAdministratorLogInMultipleTimes()
                        && usernamePasswordAuthentication.getAuthorities().contains(new SimpleGrantedAuthority(Constants.Roles.PREFIX_AUTHORITY_ROLE_TYPE + Constants.Roles.SUPER_ADMIN))) {
                    // 如果超过允许同时登录的最大数量
                    // 将最老的剔除，并将token标记为强制登出
//                    if (authorizations.size() >= env.getAllowAdministratorLogInMultipleTimesNumber()) {
//                        OAuth2Authorization authorization = authorizations.get(0);
//                        service.remove(authorization);
//                        ValueCaches.put(REFRESH_TOKEN, authorization.getAccessToken().getToken().getTokenValue(), USER_FORCE_LOGOUT);
//                    }
                } else {
                    // 非系统管理员账户同一用户同一平台同时只能登陆一次
                    // 重新登录时将上一次登陆的authorization移除，并将token标记为强制登出
                    for (OAuth2Authorization authorization : authorizations) {
                        service.remove(authorization);
                        ValueCaches.put(REFRESH_TOKEN, authorization.getAccessToken().getToken().getTokenValue(), USER_FORCE_LOGOUT);
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
                    .attribute(OAuth2ParameterNames.USERNAME, username)
                    .attribute(PARAMETER_NAME_PLATFORM, platform)
                    .attribute(OAuth2Authorization.AUTHORIZED_SCOPE_ATTRIBUTE_NAME, authorizedScopes)
                    .attribute(Principal.class.getName(), usernamePasswordAuthentication);


            if (refreshToken != null) {
                authorizationBuilder.refreshToken(refreshToken);
            }

            authorizationService.save(authorizationBuilder.build());
            LOG.debug("OAuth2Authorization saved successfully");
            LOG.debug("returning OAuth2AccessTokenAuthenticationToken");
            return new OAuth2AccessTokenAuthenticationToken(registeredClient, clientPrincipal, accessToken, refreshToken);

        } catch (Exception ex) {
            LOG.error("problem in authenticate", ex);
            if (ex instanceof HttpResponseException) {
                throw ex;
            }
            throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR), ex);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        boolean supports = OAuth2ResourceOwnerPasswordAuthenticationToken.class.isAssignableFrom(authentication);
        LOG.debug("supports authentication=" + authentication + " returning " + supports);
        return supports;
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
