package com.inmaytide.orbit.uaa.service.account.dto;

import com.inmaytide.orbit.Version;
import com.inmaytide.orbit.commons.constants.MessageSendingMode;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author inmaytide
 * @since 2024/2/27
 */
@Schema(title = "通过手机号码/电子邮箱重置密码-申请验证码接口参数")
public class ApplyVerificationCode implements Serializable {

    @Serial
    private static final long serialVersionUID = Version.SERIAL_VERSION_UID;

    @Schema(title = "用户登录名/手机号/邮箱/员工编号")
    private String loginName;

    @Schema(title = "验证码发送方式", pattern = "SMS|MAIL")
    private MessageSendingMode sendingMode;

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public MessageSendingMode getSendingMode() {
        return sendingMode;
    }

    public void setSendingMode(MessageSendingMode sendingMode) {
        this.sendingMode = sendingMode;
    }
}
