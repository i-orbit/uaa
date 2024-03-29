package com.inmaytide.orbit.uaa.api;

import com.inmaytide.exception.web.ObjectNotFoundException;
import com.inmaytide.orbit.commons.domain.SystemUser;
import com.inmaytide.orbit.commons.domain.dto.result.AffectedResult;
import com.inmaytide.orbit.commons.security.SecurityUtils;
import com.inmaytide.orbit.uaa.domain.account.User;
import com.inmaytide.orbit.uaa.domain.account.dto.ChangePassword;
import com.inmaytide.orbit.uaa.service.account.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author inmaytide
 * @since 2024/1/31
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("/api/users")
public class UserResource {

    private final UserService service;

    public UserResource(UserService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "创建用户")
    public User create(@Validated @RequestBody User entity) {
        return service.create(entity);
    }

    @GetMapping("{id}")
    @Operation(summary = "查询指定用户详情")
    public User get(@PathVariable Long id) {
        return service.get(id).orElseThrow(() -> new ObjectNotFoundException(String.valueOf(id)));
    }

    @GetMapping("authorized")
    public SystemUser getAuthorizedUser() {
        return SecurityUtils.getAuthorizedUser();
    }


}
