package com.inmaytide.orbit.uaa.domain.account;

import com.baomidou.mybatisplus.annotation.TableField;
import com.inmaytide.orbit.commons.constants.Is;
import com.inmaytide.orbit.commons.constants.Languages;
import com.inmaytide.orbit.commons.constants.UserState;
import com.inmaytide.orbit.commons.domain.pattern.TombstoneEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.wildfly.common.annotation.NotNull;

import java.time.Instant;
import java.time.LocalDate;

/**
 * @author inmaytide
 * @since 2024/1/19
 */
@Schema(title = "用户信息")
public class User extends TombstoneEntity {

    @NotNull
    @Schema(title = "所属租户唯一标识", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long tenant;

    @Schema(title = "用户是否为所属租户的管理员", requiredMode = Schema.RequiredMode.REQUIRED, defaultValue = "N")
    private Is isTenantAdministrator;

    @TableField("`rank`")
    @Schema(title = "用户职级", description = "对应数据字典编码")
    private String rank;

    @Schema(title = "用户排序", minimum = "0")
    private Integer sequence;

    @NotBlank
    @Schema(title = "用户姓名")
    private String name;

    @Schema(title = "用户性别", description = "对应数据字典编码")
    private String gender;

    @Schema(title = "出生日期")
    private LocalDate birthday;

    @Schema(title = "身份证号码")
    private String identificationNumber;

    @NotBlank
    @Schema(title = "用户登录名")
    private String loginName;

    @Schema(title = "用户登录密码", description = "加密后")
    private String password;

    @Schema(title = "密码过期时间", description = "用户根据相关配置在密码过期时是否强制修改密码")
    private Instant passwordExpireAt;

    @Schema(title = "手机号码")
    private String telephoneNumber;

    @Schema(title = "电子邮箱地址")
    private String email;

    @Schema(title = "系统默认语言", description = "预留字段-后期可扩展多语言支持, 目前只支持简体中文")
    private Languages lang;

    @Schema(title = "用户头像存储地址")
    private String avatar;

    @Schema(title = "用户电子签名图片存储地址")
    private String signature;

    @Schema(title = "用户个人照片存储地址")
    private String photo;

    @Schema(title = "用户状态")
    private UserState state;

    @Schema(title = "用户状态变更时间")
    private Instant stateTime;

    @Schema(title = "用户系统功能代理人", description = "当用户状态为不在岗时，系统相关功能待办自动转发给代理人")
    private Long proxy;

    @Schema(title = "用户人事状态")
    private String personnelStatus;

    @Schema(title = "员工编号")
    private String employeeId;

    @Schema(title = "入职日期")
    private LocalDate joinDate;

    @Schema(title = "转正日期")
    private LocalDate employmentConfirmationDate;

    @Schema(title = "离职日期")
    private LocalDate resignationDate;

    public Long getTenant() {
        return tenant;
    }

    public void setTenant(Long tenant) {
        this.tenant = tenant;
    }

    public Is getIsTenantAdministrator() {
        return isTenantAdministrator;
    }

    public void setIsTenantAdministrator(Is isTenantAdministrator) {
        this.isTenantAdministrator = isTenantAdministrator;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public String getIdentificationNumber() {
        return identificationNumber;
    }

    public void setIdentificationNumber(String identificationNumber) {
        this.identificationNumber = identificationNumber;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Instant getPasswordExpireAt() {
        return passwordExpireAt;
    }

    public void setPasswordExpireAt(Instant passwordExpireAt) {
        this.passwordExpireAt = passwordExpireAt;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Languages getLang() {
        return lang;
    }

    public void setLang(Languages lang) {
        this.lang = lang;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public UserState getState() {
        return state;
    }

    public void setState(UserState state) {
        this.state = state;
    }

    public Instant getStateTime() {
        return stateTime;
    }

    public void setStateTime(Instant stateTime) {
        this.stateTime = stateTime;
    }

    public Long getProxy() {
        return proxy;
    }

    public void setProxy(Long proxy) {
        this.proxy = proxy;
    }

    public String getPersonnelStatus() {
        return personnelStatus;
    }

    public void setPersonnelStatus(String personnelStatus) {
        this.personnelStatus = personnelStatus;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDate joinDate) {
        this.joinDate = joinDate;
    }

    public LocalDate getEmploymentConfirmationDate() {
        return employmentConfirmationDate;
    }

    public void setEmploymentConfirmationDate(LocalDate employmentConfirmationDate) {
        this.employmentConfirmationDate = employmentConfirmationDate;
    }

    public LocalDate getResignationDate() {
        return resignationDate;
    }

    public void setResignationDate(LocalDate resignationDate) {
        this.resignationDate = resignationDate;
    }
}
