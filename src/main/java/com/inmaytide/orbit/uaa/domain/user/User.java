package com.inmaytide.orbit.uaa.domain.user;

import com.baomidou.mybatisplus.annotation.TableField;
import com.inmaytide.orbit.commons.consts.Is;
import com.inmaytide.orbit.commons.consts.Languages;
import com.inmaytide.orbit.commons.consts.UserState;
import com.inmaytide.orbit.commons.domain.pattern.TombstoneEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.apache.commons.lang3.StringUtils;

import java.io.Serial;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

/**
 * @author inmaytide
 * @since 2023/4/3
 */
@Schema(title = "用户信息")
public class User extends TombstoneEntity {

    @Serial
    private static final long serialVersionUID = -8283756438301534620L;

    @Schema(title = "所属租户", accessMode = READ_ONLY, description = "系统根据条件自动指定")
    private Long tenantId;

    @Schema(title = "是否是租户管理员", description = "只有超级管理员或租住管理员可以修改该字段")
    private Is isTenantAdministrator;

    @TableField(exist = false)
    @Schema(title = "租户名称", accessMode = READ_ONLY)
    private String tenantName;

    @TableField(exist = false)
    @Schema(title = "用户所属组织", description = "同一个用户允许属于多个组织")
    private List<UserAssociation> organizations;

    @TableField(exist = false)
    @Schema(title = "用户岗位", description = "同一个用户允许兼任多个岗位")
    private List<UserAssociation> positions;

    @TableField(exist = false)
    @Schema(title = "用户角色", description = "同一个用户用于多个角色")
    private List<UserAssociation> roles;

    @Schema(title = "职级", description = "取数据字典-职级")
    @TableField("`rank`")
    private String rank;

    @TableField(exist = false)
    @Schema(title = "职级描述")
    private String rankName;

    @Schema(title = "权重", description = "影响用户排序(数值倒序), 可为空(为空时排序在不为空的用户后面, 多个为空按用户创建时间正序排列)")
    private Integer weights;

    @NotBlank(message = "用户姓名不能为空")
    @Schema(title = "姓名", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(title = "性别", description = "取数据字典-性别")
    private String gender;

    @Schema(title = "性别中文描述")
    @TableField(exist = false)
    private String genderName;

    @Schema(title = "生日")
    private LocalDate birthday;

    @Schema(title = "身份证号码")
    private String identificationNumber;

    @NotBlank(message = "用户登录名不能为空")
    @Schema(title = "登录用户名", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @Schema(title = "登录密码", description = "新建时默认密码根据配置指定")
    private String password;

    @Schema(title = "密码有效期至")
    private Instant passwordExpireAt;

    @Schema(title = "手机号码")
    private String telephoneNumber;

    @Email(message = "邮箱地址格式错误")
    @Schema(title = "电子邮箱")
    private String email;

    @Schema(title = "用户系统语言")
    private Languages lang;

    @Schema(title = "用户头像图片地址")
    private String avatar;

    @Schema(title = "用户电子签名图片地址")
    private String signature;

    @Schema(title = "个人照片")
    private String photo;

    @Schema(title = "用户状态")
    private UserState state;

    @Schema(title = "状态修改时间")
    private Instant stateTime;

    @Schema(title = "任务代理人", description = "用户外出/休假时代替处理相关任务的人员")
    private Long proxy;

    @TableField(exist = false)
    @Schema(title = "任务代理人姓名")
    private String proxyName;

    @Schema(title = "人事状态", description = "取数据字典-人事状态")
    private String personnelStatus;

    @Schema(title = "人事状态描述")
    @TableField(exist = false)
    private String personnelStatusName;

    @Schema(title = "员工编号")
    private String employeeId;

    @Schema(title = "入职时间")
    private LocalDate joinDate;

    @Schema(title = "转正日期")
    private LocalDate employmentConfirmationDate;

    @Schema(title = "离职日期")
    private LocalDate resignationDate;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Is getIsTenantAdministrator() {
        return isTenantAdministrator;
    }

    public void setIsTenantAdministrator(Is isTenantAdministrator) {
        this.isTenantAdministrator = isTenantAdministrator;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public List<UserAssociation> getOrganizations() {
        if (organizations == null) {
            organizations = Collections.emptyList();
        }
        return organizations;
    }

    public void setOrganizations(List<UserAssociation> organizations) {
        this.organizations = organizations;
    }

    public List<UserAssociation> getPositions() {
        if (positions == null) {
            positions = Collections.emptyList();
        }
        return positions;
    }

    public void setPositions(List<UserAssociation> positions) {
        this.positions = positions;
    }

    public List<UserAssociation> getRoles() {
        if (roles == null) {
            roles = Collections.emptyList();
        }
        return roles;
    }

    public void setRoles(List<UserAssociation> roles) {
        this.roles = roles;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getRankName() {
        return rankName;
    }

    public void setRankName(String rankName) {
        this.rankName = rankName;
    }

    public Integer getWeights() {
        return weights;
    }

    public void setWeights(Integer weights) {
        this.weights = weights;
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

    public String getGenderName() {
        return genderName;
    }

    public void setGenderName(String genderName) {
        this.genderName = genderName;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getLangName() {
        if (lang != null) {
            return lang.getDescription();
        }
        return StringUtils.EMPTY;
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

    public String getStateName() {
        if (state != null) {
            return state.getDescription();
        }
        return StringUtils.EMPTY;
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

    public String getProxyName() {
        return proxyName;
    }

    public void setProxyName(String proxyName) {
        this.proxyName = proxyName;
    }

    public String getPersonnelStatus() {
        return personnelStatus;
    }

    public void setPersonnelStatus(String personnelStatus) {
        this.personnelStatus = personnelStatus;
    }

    public String getPersonnelStatusName() {
        return personnelStatusName;
    }

    public void setPersonnelStatusName(String personnelStatusName) {
        this.personnelStatusName = personnelStatusName;
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
