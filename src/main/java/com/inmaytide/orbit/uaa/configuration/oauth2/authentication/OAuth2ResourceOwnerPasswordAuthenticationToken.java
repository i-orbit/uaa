package com.inmaytide.orbit.uaa.configuration.oauth2.authentication;

import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.util.Assert;

import java.io.Serial;
import java.util.*;

public class OAuth2ResourceOwnerPasswordAuthenticationToken extends AbstractAuthenticationToken {

    @Serial
    private static final long serialVersionUID = -8824025217860487220L;

    private final AuthorizationGrantType authorizationGrantType;

    private final Authentication clientPrincipal;

    private final Set<String> scopes;

    private final Map<String, Object> additionalParameters;

    public OAuth2ResourceOwnerPasswordAuthenticationToken(AuthorizationGrantType authorizationGrantType,
                                                          Authentication clientPrincipal,
                                                          @Nullable Set<String> scopes,
                                                          @Nullable Map<String, Object> additionalParameters) {
        super(Collections.emptyList());
        Assert.notNull(authorizationGrantType, "authorizationGrantType cannot be null");
        Assert.notNull(clientPrincipal, "clientPrincipal cannot be null");
        this.authorizationGrantType = authorizationGrantType;
        this.clientPrincipal = clientPrincipal;
        this.scopes = Collections.unmodifiableSet(scopes != null ? new HashSet<>(scopes) : Collections.emptySet());
        this.additionalParameters = Collections.unmodifiableMap(additionalParameters != null ? new HashMap<>(additionalParameters) : Collections.emptyMap());
    }


    @Override
    public Object getCredentials() {
        return StringUtils.EMPTY;
    }

    @Override
    public Object getPrincipal() {
        return clientPrincipal;
    }

    public AuthorizationGrantType getGrantType() {
        return authorizationGrantType;
    }

    public Set<String> getScopes() {
        return scopes;
    }

    public Map<String, Object> getAdditionalParameters() {
        return additionalParameters;
    }
}
