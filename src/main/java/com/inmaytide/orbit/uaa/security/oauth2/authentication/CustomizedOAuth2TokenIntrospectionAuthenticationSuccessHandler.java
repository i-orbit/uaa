package com.inmaytide.orbit.uaa.security.oauth2.authentication;

import com.inmaytide.orbit.commons.utils.ApplicationContextHolder;
import com.inmaytide.orbit.uaa.service.account.UserActivityService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

/**
 * @author inmaytide
 * @since 2024/4/23
 */
public class CustomizedOAuth2TokenIntrospectionAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final static Logger log = LoggerFactory.getLogger(CustomizedOAuth2TokenIntrospectionAuthenticationSuccessHandler.class);

    private final HttpMessageConverter<OAuth2TokenIntrospection> tokenIntrospectionHttpResponseConverter = new OAuth2TokenIntrospectionHttpMessageConverter();

    private UserActivityService userActivityService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2TokenIntrospectionAuthenticationToken tokenIntrospectionAuthentication = (OAuth2TokenIntrospectionAuthenticationToken) authentication;
        OAuth2TokenIntrospection tokenClaims = tokenIntrospectionAuthentication.getTokenClaims();
        ServletServerHttpResponse httpResponse = new ServletServerHttpResponse(response);
        this.tokenIntrospectionHttpResponseConverter.write(tokenClaims, null, httpResponse);

        if (tokenClaims.isActive()) {
            Thread.ofVirtual().start(() -> getUserActivityService().alterUserActivity(request, tokenClaims));
        }
    }

    public UserActivityService getUserActivityService() {
        if (userActivityService == null) {
            userActivityService = ApplicationContextHolder.getInstance().getBean(UserActivityService.class);
        }
        return userActivityService;
    }
}
