package com.inmaytide.orbit.uaa.api;

import com.inmaytide.orbit.commons.domain.dto.result.AffectedResult;
import com.inmaytide.orbit.uaa.domain.account.dto.ChangePassword;
import com.inmaytide.orbit.uaa.domain.account.dto.ResetPassword;
import com.inmaytide.orbit.uaa.service.account.UserPasswordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author inmaytide
 * @since 2024/2/26
 */
@Tag(name = "用户管理-密码管理接口")
@RestController
@RequestMapping("/api/users/passwords")
public class UserPasswordResource {

    private final UserPasswordService service;

    public UserPasswordResource(UserPasswordService service) {
        this.service = service;
    }

    @PutMapping("change")
    @Operation(summary = "通过原密码修改用户密码", description = "用于用户自主修改密码")
    public AffectedResult change(@RequestBody @Validated ChangePassword dto) {
        return service.change(dto);
    }

    @PutMapping("change-with-validation-code")
    @Operation(summary = "通过手机号码/电子邮箱修改用户密码", description = "用于忘记密码自主找回或用户自主修改密码")
    public AffectedResult changeWithValidationCode(@RequestBody @Validated ChangePassword dto) {
        return service.changeWithValidationCode(dto);
    }

    @PutMapping("reset")
    @Operation(summary = "重置用户密码到默认密码", description = "仅验证操作人权限")
    public AffectedResult reset(@RequestBody @Validated ResetPassword dto) {
        return service.reset(dto);
    }

}
