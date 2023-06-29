package com.inmaytide.orbit.uaa.mapper.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inmaytide.orbit.uaa.domain.user.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author inmaytide
 * @since 2023/4/6
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
