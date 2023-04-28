package com.inmaytide.orbit.uaa.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inmaytide.orbit.uaa.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author inmaytide
 * @since 2023/4/6
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
