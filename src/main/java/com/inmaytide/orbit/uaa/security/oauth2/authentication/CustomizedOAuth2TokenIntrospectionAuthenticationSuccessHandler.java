package com.inmaytide.orbit.uaa.security.oauth2.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inmaytide.orbit.commons.constants.Platforms;
import com.inmaytide.orbit.commons.utils.ApplicationContextHolder;
import com.inmaytide.orbit.commons.utils.ValueCaches;
import com.inmaytide.orbit.uaa.domain.account.UserActivity;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenIntrospection;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2TokenIntrospectionAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.http.converter.OAuth2TokenIntrospectionHttpMessageConverter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.time.Instant;

import static com.inmaytide.orbit.commons.constants.Constants.CacheNames.USER_ACTIVITY;
import static com.inmaytide.orbit.commons.utils.HttpUtils.getClientIpAddress;

/**
 * @author inmaytide
 * @since 2024/4/23
 */
public class CustomizedOAuth2TokenIntrospectionAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final static Logger log = LoggerFactory.getLogger(CustomizedOAuth2TokenIntrospectionAuthenticationSuccessHandler.class);

    private final HttpMessageConverter<OAuth2TokenIntrospection> tokenIntrospectionHttpResponseConverter = new OAuth2TokenIntrospectionHttpMessageConverter();

    private ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2TokenIntrospectionAuthenticationToken tokenIntrospectionAuthentication = (OAuth2TokenIntrospectionAuthenticationToken) authentication;
        OAuth2TokenIntrospection tokenClaims = tokenIntrospectionAuthentication.getTokenClaims();
        ServletServerHttpResponse httpResponse = new ServletServerHttpResponse(response);
        this.tokenIntrospectionHttpResponseConverter.write(tokenClaims, null, httpResponse);

        if (tokenClaims.isActive()) {
            Thread.ofVirtual().start(() -> setUserActivity(request, tokenClaims));
        }
    }

    private void setUserActivity(HttpServletRequest request, OAuth2TokenIntrospection tokenClaims) {
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
                ValueCaches.put(USER_ACTIVITY, getUserActivityCacheKey(platform, userId), getObjectMapper().writeValueAsString(activity));
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

    private ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            objectMapper = ApplicationContextHolder.getInstance().getBean(ObjectMapper.class);
        }
        return objectMapper;
    }
}
