package com.inmaytide.orbit.uaa.domain.dto;

import com.inmaytide.orbit.commons.domain.dto.params.Pageable;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author inmaytide
 * @since 2023/6/1
 */
@Schema(title = "租户列表查询接口参数")
public class TenantQuery extends Pageable {
}
