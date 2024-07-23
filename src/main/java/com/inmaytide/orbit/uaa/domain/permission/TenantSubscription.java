package com.inmaytide.orbit.uaa.domain.permission;

import com.inmaytide.orbit.commons.domain.pattern.TombstoneEntity;
import com.inmaytide.orbit.uaa.consts.TenantSubscriptionStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * @author inmaytide
 * @since 2024/7/23
 */
@Schema(title = "租户订阅记录")
public class TenantSubscription extends TombstoneEntity {

    @Schema(title = "租户唯一标识")
    private Long tenant;

    private Instant startDate;

    private Instant endDate;

    private TenantSubscriptionStatus status;

    private BigDecimal amount;

    private String paymentMethod;



}
