package com.inmaytide.orbit.uaa.configuration;

/**
 * @author inmaytide
 * @since 2023/5/6
 */
public enum ErrorCode implements com.inmaytide.exception.web.domain.ErrorCode {

    E_0x00100001("0x00100001", "用户已在其他地方登录"),
    E_0x00100002("0x00100002", "系统中不存在用户名为 {0} 的用户信息"),
    E_0x00100003("0x00100003", "用户密码输入错误"),
    E_0x00100004("0x00100004", "修改数据时, 唯一标识不能为空"),
    E_0x00100005("0x00100005", "用户已存在(登录名/手机号码/邮箱地址/员工编号重复)"),
    E_0x00100006("0x00100006", "您没有权限创建/修改该用户信息"),
    ;

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
