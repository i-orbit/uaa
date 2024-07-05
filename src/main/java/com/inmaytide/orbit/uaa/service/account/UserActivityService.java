package com.inmaytide.orbit.uaa.service.account;

import com.inmaytide.orbit.commons.business.BasicService;
import com.inmaytide.orbit.uaa.domain.account.UserActivity;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenIntrospection;

import java.util.List;

/**
 * @author inmaytide
 * @since 2024/4/29
 */
public interface UserActivityService extends BasicService<UserActivity> {

    void alterUserActivity(HttpServletRequest request, OAuth2TokenIntrospection tokenClaims);

    /**
     * 获取指定租户当前在线用户数
     *
     * @param tenant 指定租户ID
     * @return 租户当前在线用户数量
     */
    Long getNumberOfOnlineUsers(Long tenant);

    void persist(List<UserActivity> entities);
}
