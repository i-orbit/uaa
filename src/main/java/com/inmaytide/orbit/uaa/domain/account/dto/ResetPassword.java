package com.inmaytide.orbit.uaa.domain.account.dto;

import com.inmaytide.orbit.commons.utils.CommonUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

/**
 * @author inmaytide
 * @since 2024/2/26
 */
@Schema(title = "重置用户密码的接口请求参数")
public class ResetPassword {

    @NotBlank
    private String userIds;

    public List<Long> getUserIds() {
        return CommonUtils.splitToLongByCommas(userIds);
    }

    public void setUserIds(String userIds) {
        this.userIds = userIds;
    }
}
