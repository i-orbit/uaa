package com.inmaytide.orbit.uaa.service.account;

import com.inmaytide.orbit.commons.domain.dto.result.AffectedResult;
import com.inmaytide.orbit.uaa.domain.account.User;
import com.inmaytide.orbit.uaa.service.account.dto.ApplyVerificationCode;
import com.inmaytide.orbit.uaa.service.account.dto.ChangePassword;
import com.inmaytide.orbit.uaa.service.account.dto.ResetPassword;

import java.time.Instant;
import java.util.regex.Pattern;

/**
 * @author inmaytide
 * @since 2024/2/26
 */
public interface UserPasswordService {

    /**
     * 密码规则正则表达式，至少8个字符，至少1个字母，1个数字和1个特殊字符
     */
    Pattern REG_PASSWORD = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,}$");

    /**
     * 用户自己通过原密码修改登录密码(仅允许修改当前登录用户自己的密码)
     *
     * @param dto 请求参数
     * @return 影响数据行数
     */
    AffectedResult change(ChangePassword dto);

    /**
     * 用户通过手机号码/电子邮箱获取验证码后修改登录密码(不需要登录)
     *
     * @param dto 请求参数
     * @return 影响数据行数
     */
    AffectedResult changeWithValidationCode(ChangePassword dto);

    AffectedResult reset(ResetPassword dto);

    String getEncryptedPassword(String rawPassword);

    String generateDefaultPassword(User entity);

    Instant getPasswordExpireAt(String tenant);

    void applyVerificationCode(ApplyVerificationCode dto);
}
