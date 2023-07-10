package com.inmaytide.orbit.uaa.domain.user;

import com.inmaytide.exception.web.BadRequestException;
import com.inmaytide.orbit.commons.utils.CodecUtils;
import com.inmaytide.orbit.uaa.configuration.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

/**
 * @author inmaytide
 * @since 2023/7/7
 */
@Schema(title = "密码修改请求内容实体")
public class ChangePassword implements Serializable {

    @Schema(title = "原密码", description = "通过原密码修改密码时不能为空")
    private String originalValue;

    @NotBlank
    @Schema(title = "新密码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String newValue;

    @NotBlank
    @Schema(title = "确认新密码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String confirmValue;

    @Schema(title = "验证码", description = "通过验证码修改密码时不能为空")
    private String captchaValue;

    @Schema(title = "用户名", description = "通过验证码修改密码时不能为空")
    private String username;

    public String getOriginalValue() {
        try {
            return CodecUtils.decrypt(originalValue, CodecUtils.RSA_PRIVATE_KEY);
        } catch (Exception e) {
            throw new BadRequestException(ErrorCode.E_0x00100008);
        }
    }

    public void setOriginalValue(String originalValue) {
        this.originalValue = originalValue;
    }

    public String getNewValue() {
        try {
            return CodecUtils.decrypt(newValue, CodecUtils.RSA_PRIVATE_KEY);
        } catch (Exception e) {
            throw new BadRequestException(ErrorCode.E_0x00100008);
        }
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public String getConfirmValue() {
        try {
            return CodecUtils.decrypt(confirmValue, CodecUtils.RSA_PRIVATE_KEY);
        } catch (Exception e) {
            throw new BadRequestException(ErrorCode.E_0x00100008);
        }
    }

    public void setConfirmValue(String confirmValue) {
        this.confirmValue = confirmValue;
    }

    public String getCaptchaValue() {
        return captchaValue;
    }

    public void setCaptchaValue(String captchaValue) {
        this.captchaValue = captchaValue;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
