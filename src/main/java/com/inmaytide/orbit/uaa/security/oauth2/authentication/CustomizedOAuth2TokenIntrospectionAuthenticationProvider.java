package com.inmaytide.orbit.uaa.security.oauth2.authentication;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.converter.ClaimConversionService;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenIntrospection;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2TokenIntrospectionAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomizedOAuth2TokenIntrospectionAuthenticationProvider implements AuthenticationProvider {

    private static final TypeDescriptor OBJECT_TYPE_DESCRIPTOR = TypeDescriptor.valueOf(Object.class);

    private static final TypeDescriptor LIST_STRING_TYPE_DESCRIPTOR = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(String.class));

    private final RegisteredClientRepository registeredClientRepository;

    private final OAuth2AuthorizationService authorizationService;

    /**
     * Constructs an {@code OAuth2TokenIntrospectionAuthenticationProvider} using the provided parameters.
     *
     * @param registeredClientRepository the repository of registered clients
     * @param authorizationService       the authorization service
     */
    public CustomizedOAuth2TokenIntrospectionAuthenticationProvider(RegisteredClientRepository registeredClientRepository,
                                                                    OAuth2AuthorizationService authorizationService) {
        Assert.notNull(registeredClientRepository, "registeredClientRepository cannot be null");
        Assert.notNull(authorizationService, "authorizationService cannot be null");
        this.registeredClientRepository = registeredClientRepository;
        this.authorizationService = authorizationService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        OAuth2TokenIntrospectionAuthenticationToken tokenIntrospectionAuthentication = (OAuth2TokenIntrospectionAuthenticationToken) authentication;
        OAuth2ClientAuthenticationToken clientPrincipal = getAuthenticatedClientElseThrowInvalidClient(tokenIntrospectionAuthentication);

        OAuth2Authorization authorization = this.authorizationService.findByToken(tokenIntrospectionAuthentication.getToken(), null);
        if (authorization == null) {
            // Return the authentication request when token not found
            return tokenIntrospectionAuthentication;
        }

        OAuth2Authorization.Token<AbstractOAuth2Token> authorizedToken = authorization.getToken(tokenIntrospectionAuthentication.getToken());
        if (authorizedToken == null || !authorizedToken.isActive()) {
            return new OAuth2TokenIntrospectionAuthenticationToken(tokenIntrospectionAuthentication.getToken(), clientPrincipal, OAuth2TokenIntrospection.builder().build());
        }

        RegisteredClient authorizedClient = this.registeredClientRepository.findById(authorization.getRegisteredClientId());
        OAuth2TokenIntrospection tokenClaims = withActiveTokenClaims(authorization, authorizedToken, authorizedClient);

        return new OAuth2TokenIntrospectionAuthenticationToken(authorizedToken.getToken().getTokenValue(), clientPrincipal, tokenClaims);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OAuth2TokenIntrospectionAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private static OAuth2TokenIntrospection withActiveTokenClaims(OAuth2Authorization authorization, OAuth2Authorization.Token<AbstractOAuth2Token> authorizedToken, RegisteredClient authorizedClient) {

        OAuth2TokenIntrospection.Builder tokenClaims;
        if (!CollectionUtils.isEmpty(authorizedToken.getClaims())) {
            Map<String, Object> claims = convertClaimsIfNecessary(authorizedToken.getClaims());
            tokenClaims = OAuth2TokenIntrospection.withClaims(claims).active(true);
        } else {
            tokenClaims = OAuth2TokenIntrospection.builder(true);
        }

        tokenClaims.clientId(authorizedClient.getClientId());
        tokenClaims.username(authorization.getPrincipalName());
        if (authorization.getAttribute(OAuth2PasswordAuthenticationProvider.PARAMETER_NAME_PLATFORM) != null) {
            tokenClaims.claim(OAuth2PasswordAuthenticationProvider.PARAMETER_NAME_PLATFORM, authorization.getAttribute(OAuth2PasswordAuthenticationProvider.PARAMETER_NAME_PLATFORM));
        }
        UsernamePasswordAuthenticationToken principal = authorization.getAttribute("java.security.Principal");
        if (principal != null && org.apache.commons.collections4.CollectionUtils.isNotEmpty(principal.getAuthorities())) {
            tokenClaims.claim("authorities", principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        } else {
            tokenClaims.claim("authorities", Collections.emptyList());
        }
        AbstractOAuth2Token token = authorizedToken.getToken();
        if (token.getIssuedAt() != null) {
            tokenClaims.issuedAt(token.getIssuedAt());
        }
        if (token.getExpiresAt() != null) {
            tokenClaims.expiresAt(token.getExpiresAt());
        }

        if (OAuth2AccessToken.class.isAssignableFrom(token.getClass())) {
            OAuth2AccessToken accessToken = (OAuth2AccessToken) token;
            tokenClaims.tokenType(accessToken.getTokenType().getValue());
        }

        return tokenClaims.build();
    }

    private static Map<String, Object> convertClaimsIfNecessary(Map<String, Object> claims) {
        Map<String, Object> convertedClaims = new HashMap<>(claims);

        Object value = claims.get(OAuth2TokenIntrospectionClaimNames.ISS);
        if (value != null && !(value instanceof URL)) {
            URL convertedValue = ClaimConversionService.getSharedInstance()
                    .convert(value, URL.class);
            if (convertedValue != null) {
                convertedClaims.put(OAuth2TokenIntrospectionClaimNames.ISS, convertedValue);
            }
        }

        value = claims.get(OAuth2TokenIntrospectionClaimNames.SCOPE);
        if (value != null && !(value instanceof List)) {
            Object convertedValue = ClaimConversionService.getSharedInstance()
                    .convert(value, OBJECT_TYPE_DESCRIPTOR, LIST_STRING_TYPE_DESCRIPTOR);
            if (convertedValue != null) {
                convertedClaims.put(OAuth2TokenIntrospectionClaimNames.SCOPE, convertedValue);
            }
        }

        value = claims.get(OAuth2TokenIntrospectionClaimNames.AUD);
        if (value != null && !(value instanceof List)) {
            Object convertedValue = ClaimConversionService.getSharedInstance()
                    .convert(value, OBJECT_TYPE_DESCRIPTOR, LIST_STRING_TYPE_DESCRIPTOR);
            if (convertedValue != null) {
                convertedClaims.put(OAuth2TokenIntrospectionClaimNames.AUD, convertedValue);
            }
        }

        return convertedClaims;
    }

    static OAuth2ClientAuthenticationToken getAuthenticatedClientElseThrowInvalidClient(Authentication authentication) {
        OAuth2ClientAuthenticationToken clientPrincipal = null;
        if (OAuth2ClientAuthenticationToken.class.isAssignableFrom(authentication.getPrincipal().getClass())) {
            clientPrincipal = (OAuth2ClientAuthenticationToken) authentication.getPrincipal();
        }
        if (clientPrincipal != null && clientPrincipal.isAuthenticated()) {
            return clientPrincipal;
        }
        throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_CLIENT);
    }
}
