package com.inmaytide.orbit.uaa.domain.permission;

import com.inmaytide.orbit.Version;
import com.inmaytide.orbit.commons.constants.TenantStatus;
import com.inmaytide.orbit.commons.domain.pattern.TombstoneEntity;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serial;
import java.time.Instant;

/**
 * @author inmaytide
 * @since 2024/1/19
 */
@Schema(title = "租户信息")
public class Tenant extends TombstoneEntity {

    @Serial
    private static final long serialVersionUID = Version.SERIAL_VERSION_UID;

    @Schema(title = "租户全称")
    private String name;

    @Schema(title = "租户别称/简称")
    private String alias;

    @Schema(title = "租户描述/介绍")
    private String description;

    @Schema(title = "租户状态")
    private TenantStatus status;

    @Schema(title = "大LOGO图片存储地址")
    private String fullLogo;

    @Schema(title = "小LOGO图片存储地址")
    private String iconLogo;

    @Schema(title = "当前订阅到期时间")
    private Instant subscriptionExpireAt;

    @Schema(title = "主要联系人")
    private String primaryConcat;

    @Schema(title = "主要联系人电话")
    private String primaryConcatPhone;

    @Schema(title = "第二联系人")
    private String secondaryConcat;

    @Schema(title = "第二联系人电话")
    private String secondaryConcatPhone;

    @Schema(title = "账单地址")
    private String billingAddress;

    @Schema(title = "支付方法", description = "数据字典编码")
    private String paymentMethod;

    @Schema(title = "支付方法描述")
    private String paymentMethodName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TenantStatus getStatus() {
        return status;
    }

    public void setStatus(TenantStatus status) {
        this.status = status;
    }

    public String getFullLogo() {
        return fullLogo;
    }

    public void setFullLogo(String fullLogo) {
        this.fullLogo = fullLogo;
    }

    public String getIconLogo() {
        return iconLogo;
    }

    public void setIconLogo(String iconLogo) {
        this.iconLogo = iconLogo;
    }

    public Instant getSubscriptionExpireAt() {
        return subscriptionExpireAt;
    }

    public void setSubscriptionExpireAt(Instant subscriptionExpireAt) {
        this.subscriptionExpireAt = subscriptionExpireAt;
    }

    public String getPrimaryConcat() {
        return primaryConcat;
    }

    public void setPrimaryConcat(String primaryConcat) {
        this.primaryConcat = primaryConcat;
    }

    public String getPrimaryConcatPhone() {
        return primaryConcatPhone;
    }

    public void setPrimaryConcatPhone(String primaryConcatPhone) {
        this.primaryConcatPhone = primaryConcatPhone;
    }

    public String getSecondaryConcat() {
        return secondaryConcat;
    }

    public void setSecondaryConcat(String secondaryConcat) {
        this.secondaryConcat = secondaryConcat;
    }

    public String getSecondaryConcatPhone() {
        return secondaryConcatPhone;
    }

    public void setSecondaryConcatPhone(String secondaryConcatPhone) {
        this.secondaryConcatPhone = secondaryConcatPhone;
    }

    public String getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentMethodName() {
        return paymentMethodName;
    }

    public void setPaymentMethodName(String paymentMethodName) {
        this.paymentMethodName = paymentMethodName;
    }
}
