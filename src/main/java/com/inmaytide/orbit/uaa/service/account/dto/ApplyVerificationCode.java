package com.inmaytide.orbit.uaa.service.account.dto;

import com.inmaytide.orbit.Version;
import com.inmaytide.orbit.commons.constants.MessageSendingMode;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author inmaytide
 * @since 2024/2/27
 */
public class ApplyVerificationCode implements Serializable {

    @Serial
    private static final long serialVersionUID = Version.SERIAL_VERSION_UID;

    private String loginName;

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
