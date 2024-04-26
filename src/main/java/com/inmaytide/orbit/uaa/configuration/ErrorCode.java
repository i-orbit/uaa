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
    E_0x00100007("0x00100007", "区域位置名称重复"),
    E_0x00100008("0x00100008", "密码传输格式错误"),
    E_0x00100009("0x00100009", "原密码输入错误"),
    E_0x00100010("0x00100010", "两次输入的密码不一致"),
    E_0x00100011("0x00100011", "密码长度最少 8 位且至少包含1个字母、1个数字和1个特殊字符"),
    E_0x00100012("0x00100012", "角色名称或编码重复"),
    /**
     * 通过手机号码/电子邮箱获取验证码修改密码时，验证码输入错误
     */
    E_0x00100013("0x00100013", "验证码输入错误"),
    E_0x00100014("0x00100014", "申请重置密码的验证码失败"),
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
