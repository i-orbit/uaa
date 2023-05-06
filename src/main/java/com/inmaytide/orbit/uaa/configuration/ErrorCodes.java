package com.inmaytide.orbit.uaa.configuration;

import com.inmaytide.exception.web.domain.ErrorCode;

/**
 * @author inmaytide
 * @since 2023/5/6
 */
public enum ErrorCodes implements ErrorCode {

    E_0x00100001("0x00100001", "用户已在其他地方登录");

    private final String value;

    private final String description;

    ErrorCodes(String value, String description) {
        this.value = value;
        this.description = description;
    }

    @Override
    public String value() {
        return this.value;
    }

    @Override
    public String description() {
        return this.description;
    }
}
