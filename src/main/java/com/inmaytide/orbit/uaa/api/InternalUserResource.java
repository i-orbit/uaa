package com.inmaytide.orbit.uaa.api;

import com.inmaytide.orbit.uaa.domain.User;
import com.inmaytide.orbit.uaa.service.UserService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <ol>
 *     <li>系统微服务间调用的内部接口，外部拒绝访问</li>
 *     <li>不需要登录验证，所以不要在接口中调用{@link com.inmaytide.orbit.commons.security.SecurityUtils#getAuthorizedUser}获取当前用户信息</li>
 * </ol>
 *
 * @author inmaytide
 * @since 2023/5/15
 */
@RestController
@Api(tags = "用户信息(后端微服务内部接口)")
@RequestMapping("/api/internal/users")
public class InternalUserResource {

    private final UserService service;

    public InternalUserResource(UserService service) {
        this.service = service;
    }

    @GetMapping("get-id-by-username")
    public Long getIdByUsername(@RequestParam("username") String username) {
        User user = service.findUserByUsername(username);
        return user != null ? user.getId() : null;
    }

}
