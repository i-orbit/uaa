package com.inmaytide.orbit.uaa.mapper.account;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inmaytide.orbit.uaa.domain.account.Organization;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author inmaytide
 * @since 2024/5/28
 */
@Mapper
public interface OrganizationMapper extends BaseMapper<Organization> {

    @Select("select ifnull(max(sequence), 0) + 1 from organization")
    Integer findNextSequence();

}
