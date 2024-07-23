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

    public Long getTenant() {
        return tenant;
    }

    public void setTenant(Long tenant) {
        this.tenant = tenant;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public TenantSubscriptionStatus getStatus() {
        return status;
    }

    public void setStatus(TenantSubscriptionStatus status) {
        this.status = status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
