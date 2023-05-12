package com.inmaytide.orbit.uaa.configuration;

/**
 * @author inmaytide
 * @since 2023/5/6
 */
public enum ErrorCode implements com.inmaytide.exception.web.domain.ErrorCode {

    E_0x00100001("0x00100001", "用户已在其他地方登录"),
    E_0x00100002("0x00100002", "系统中不存在用户名为 {0} 的用户信息"),
    E_0x00100003("0x00100003", "用户密码输入错误");

    private final String value;

    private final String description;

    ErrorCode(String value, String description) {
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
