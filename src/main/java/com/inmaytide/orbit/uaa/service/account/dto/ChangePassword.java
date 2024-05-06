package com.inmaytide.orbit.uaa.service.account.dto;

import com.inmaytide.exception.web.BadRequestException;
import com.inmaytide.orbit.Version;
import com.inmaytide.orbit.commons.utils.CodecUtils;
import com.inmaytide.orbit.uaa.configuration.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author inmaytide
 * @since 2024/2/26
 */
@Schema(title = "修改用户密码的接口请求参数")
public class ChangePassword implements Serializable {

    @Serial
    private static final long serialVersionUID = Version.SERIAL_VERSION_UID;

    @NotBlank
    @Schema(title = "用户名")
    private String loginName;

    @Schema(title = "原密码", description = "通过原密码修改密码时不能为空")
    private String originalPassword;

    @NotBlank
    @Schema(title = "新密码")
    private String newPassword;

    @NotBlank
    @Schema(title = "确认新密码")
    private String confirmPassword;

    @Schema(title = "验证码", description = "通过手机号码/电子邮箱重置密码时不能为空")
    private String verificationCode;

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getOriginalPassword() {
        try {
            return CodecUtils.decrypt(originalPassword);
        } catch (Exception e) {
            throw new BadRequestException(ErrorCode.E_0x00100008);
        }
    }

    public void setOriginalPassword(String originalPassword) {
        this.originalPassword = originalPassword;
    }

    public String getNewPassword() {
        try {
            return CodecUtils.decrypt(newPassword);
        } catch (Exception e) {
            throw new BadRequestException(ErrorCode.E_0x00100008);
        }
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        try {
            return CodecUtils.decrypt(confirmPassword);
        } catch (Exception e) {
            throw new BadRequestException(ErrorCode.E_0x00100008);
        }
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }
}
