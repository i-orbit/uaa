package com.inmaytide.orbit.uaa.api;

import com.inmaytide.exception.web.ObjectNotFoundException;
import com.inmaytide.orbit.commons.domain.SystemUser;
import com.inmaytide.orbit.commons.domain.dto.result.PageResult;
import com.inmaytide.orbit.commons.security.SecurityUtils;
import com.inmaytide.orbit.commons.utils.CommonUtils;
import com.inmaytide.orbit.uaa.domain.account.User;
import com.inmaytide.orbit.uaa.service.account.UserService;
import com.inmaytide.orbit.uaa.service.account.dto.UserQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

    @PutMapping
    @Operation(summary = "修改用户")
    public User update(@Validated @RequestBody User entity) {
        return service.update(entity);
    }

    @GetMapping("{id}")
    @Operation(summary = "查询指定用户详情")
    public User get(@PathVariable Long id) {
        return service.get(id).orElseThrow(() -> new ObjectNotFoundException(String.valueOf(id)));
    }

    @GetMapping
    @Operation(summary = "分页查询用户列表")
    public PageResult<User> pagination(@ModelAttribute UserQuery params) {
        return service.pagination(params);
    }

    @GetMapping("authorized")
    @Operation(summary = "获取当前登录用户详细信息(包含权限、视角等相关信息)")
    public SystemUser getAuthorizedUser() {
        return SecurityUtils.getAuthorizedUser();
    }

    @GetMapping("names")
    @Operation(summary = "查询指定用户的姓名")
    public Map<Long, String> findNamesByIds(@RequestParam("ids") String ids) {
        return service.findNamesByIds(CommonUtils.splitToLongByCommas(ids));
    }

    @GetMapping("emails")
    @Operation(summary = "查询指定用户的邮箱地址")
    public Map<Long, String> findEmailsByIds(@RequestParam("ids") String ids) {
        return service.findEmailsByIds(CommonUtils.splitToLongByCommas(ids));
    }

    @GetMapping("telephone-numbers")
    @Operation(summary = "查询指定用户的手机号码")
    public Map<Long, String> findTelephoneNumbersByIds(@RequestParam("ids") String ids) {
        return service.findTelephoneNumbersByIds(CommonUtils.splitToLongByCommas(ids));
    }


}
