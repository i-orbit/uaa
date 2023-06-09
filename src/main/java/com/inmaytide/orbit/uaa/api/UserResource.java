package com.inmaytide.orbit.uaa.api;

import com.inmaytide.exception.web.ObjectNotFoundException;
import com.inmaytide.orbit.commons.consts.Platforms;
import com.inmaytide.orbit.commons.domain.GlobalUser;
import com.inmaytide.orbit.commons.domain.dto.result.AffectedResult;
import com.inmaytide.orbit.commons.domain.dto.result.PageResult;
import com.inmaytide.orbit.commons.security.SecurityUtils;
import com.inmaytide.orbit.commons.utils.CommonUtils;
import com.inmaytide.orbit.uaa.domain.user.ChangePassword;
import com.inmaytide.orbit.uaa.domain.user.User;
import com.inmaytide.orbit.uaa.domain.user.UserQuery;
import com.inmaytide.orbit.uaa.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author inmaytide
 * @since 2023/4/7
 */
@RestController
@Tag(name = "用户信息")
@RequestMapping("/api/users")
public class UserResource {

    private final UserService service;

    public UserResource(UserService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "新增用户信息")
    public User create(@RequestBody @Validated User user) {
        return service.create(user);
    }

    @PutMapping
    @Operation(summary = "修改用户信息")
    public User update(@RequestBody @Validated User user) {
        return service.update(user);
    }

    @DeleteMapping
    @Operation(summary = "删除指定用户信息")
    public AffectedResult deleteByIds(@Parameter(description = "指定用户唯一标识, 多个用英文逗号隔开") @RequestParam String ids) {
        return service.deleteByIds(CommonUtils.splitToLongByCommas(ids));
    }

    @GetMapping
    @Operation(summary = "分页查询用户信息列表")
    public PageResult<User> pagination(@ParameterObject @ModelAttribute UserQuery params) {
        return service.pagination(params);
    }

    @GetMapping("{id}")
    @Operation(summary = "查询指定用户信息")
    public User get(@PathVariable Long id) {
        return service.get(id).orElseThrow(() -> new ObjectNotFoundException(String.valueOf(id)));
    }

    @GetMapping("/current")
    @Operation(summary = "获取当前登录用户信息")
    public GlobalUser getCurrentUser() {
        return SecurityUtils.getAuthorizedUser();
    }

    @GetMapping("/current/platform")
    @Operation(summary = "获取当前登录用户的登录平台")
    public Platforms getCurrentPlatform() {
        return SecurityUtils.getPlatform().orElse(null);
    }

    @GetMapping("/by-username")
    @Operation(summary = "根据用户登录名获取用户ID")
    public User getIdByUsername(@RequestParam("username") String username) {
        return service.findUserByUsername(username).orElse(null);
    }

    @PutMapping("/change-password-with-original-password")
    public AffectedResult changePasswordWithOriginalPassword(@Validated @RequestBody ChangePassword body) {
        return service.changePasswordWithOriginalPassword(body);
    }

    @PutMapping("/change-password-with-captcha")
    public AffectedResult changePasswordWithCaptcha(@Validated @RequestBody ChangePassword body) {
        return service.changePasswordWithCaptcha(body);
    }

}
