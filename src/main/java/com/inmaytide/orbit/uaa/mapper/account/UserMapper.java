package com.inmaytide.orbit.uaa.mapper.account;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inmaytide.orbit.uaa.domain.account.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author inmaytide
 * @since 2024/1/19
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("select ifnull(max(sequence), 0) + 1 from user")
    Integer findNewSequence();

}
