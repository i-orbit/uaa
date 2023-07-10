package com.inmaytide.orbit.uaa.service.user;

import com.inmaytide.orbit.commons.business.BasicService;
import com.inmaytide.orbit.commons.domain.dto.result.AffectedResult;
import com.inmaytide.orbit.commons.provider.UserDetailsProvider;
import com.inmaytide.orbit.uaa.domain.user.ChangePassword;
import com.inmaytide.orbit.uaa.domain.user.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * @author inmaytide
 * @since 2023/4/7
 */
public interface UserService extends UserDetailsProvider, BasicService<User> {

    /**
     * 密码规则正则表达式，密码长度最少 8 位且至少包含1个字母、1个数字和1个特殊字符
     */
    Pattern REG_PASSWORD = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,}$");

    /**
     * 通过用户登录名查询指定用户信息
     *
     * @param username 用户登录名(username)/邮箱地址(email)/手机号(telephoneNumber)/员工编号(employeeId)都可以作为用户登录名
     * @return 对应的用户信息
     */
    Optional<User> findUserByUsername(String username);


    Map<Long, String> findNamesByIds(List<Long> ids);

    AffectedResult changePasswordWithOriginalPassword(ChangePassword body);

    AffectedResult changePasswordWithCaptcha(ChangePassword body);
}
