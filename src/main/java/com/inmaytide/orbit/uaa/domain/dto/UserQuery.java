package com.inmaytide.orbit.uaa.domain.dto;

import com.inmaytide.orbit.commons.domain.params.Pageable;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author inmaytide
 * @since 2023/4/7
 */
@Schema(title = "用户查询请求参数")
public class UserQuery extends Pageable {
}
