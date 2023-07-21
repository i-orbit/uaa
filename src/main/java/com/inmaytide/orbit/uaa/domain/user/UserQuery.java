package com.inmaytide.orbit.uaa.domain.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.inmaytide.orbit.commons.domain.dto.params.Pageable;
import com.inmaytide.orbit.commons.utils.ApplicationContextHolder;
import com.inmaytide.orbit.uaa.configuration.ApplicationProperties;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author inmaytide
 * @since 2023/4/7
 */
@Schema(title = "用户查询请求参数")
public class UserQuery extends Pageable<User> {

    @Override
    public LambdaQueryWrapper<User> toWrapper() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        // 超级管理员在用户列表中隐藏
        wrapper.ne(User::getUsername, ApplicationContextHolder.getInstance().getBean(ApplicationProperties.class).getSuperAdministratorUsername());
        return wrapper;
    }
}
