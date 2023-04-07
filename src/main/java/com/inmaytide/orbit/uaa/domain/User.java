package com.inmaytide.orbit.uaa.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.inmaytide.orbit.commons.consts.Is;
import com.inmaytide.orbit.commons.consts.Languages;
import com.inmaytide.orbit.commons.consts.UserState;
import com.inmaytide.orbit.commons.domain.pattern.TombstoneEntity;
import com.inmaytide.orbit.commons.domain.validation.groups.Update;
import com.inmaytide.orbit.uaa.domain.association.AssociationUserAndOrganization;
import com.inmaytide.orbit.uaa.domain.association.AssociationUserAndPosition;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static io.swagger.annotations.ApiModelProperty.AccessMode.READ_ONLY;

/**
 * @author inmaytide
 * @since 2023/4/3
 */
@ApiModel("用户信息")
public class User extends TombstoneEntity {

    @ApiModelProperty("唯一标识")
    @NotNull(groups = {Update.class})
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty(value = "所属租户", accessMode = READ_ONLY, notes = "系统根据条件自动指定")
    private Long tenantId;

    @ApiModelProperty(value = "是否是租户管理员", notes = "只有超级管理员或租住管理员可以修改该字段")
    private Is isTenantAdministrator;

    @TableField(exist = false)
    @ApiModelProperty(value = "租户名称", accessMode = READ_ONLY)
    private Long tenantName;

    @ApiModelProperty(value = "用户所属组织", notes = "同一个用户允许属于多个组织")
    private List<AssociationUserAndOrganization> organizations;

    @ApiModelProperty(value = "用户岗位", notes = "同一个用户允许兼任多个岗位")
    private List<AssociationUserAndPosition> positions;

    @ApiModelProperty(value = "职级", notes = "取数据字典-职级")
    private String rank;

    @TableField(exist = false)
    @ApiModelProperty("职级描述")
    private String rankName;

    @ApiModelProperty(value = "权重", notes = "影响用户排序(数值倒序), 可为空(为空时排序在不为空的用户后面, 多个为空按用户创建时间正序排列)")
    private Integer weights;

    @NotBlank
    @ApiModelProperty(value = "姓名", required = true)
    private String name;

    @ApiModelProperty(value = "性别", notes = "取数据字典-职级")
    private String gender;

    @ApiModelProperty("性别中文描述")
    @TableField(exist = false)
    private String genderName;

    @ApiModelProperty("生日")
    private LocalDate birthday;

    @ApiModelProperty("身份证号码")
    private String identificationNumber;

    @NotBlank
    @ApiModelProperty(value = "登录用户名", required = true)
    private String username;

    @ApiModelProperty(value = "登录密码", notes = "新建时默认密码根据配置指定")
    private String password;

    @ApiModelProperty("密码有效期至")
    private Instant passwordExpireAt;

    @ApiModelProperty("手机号码")
    private String telephoneNumber;

    @Email
    @ApiModelProperty("电子邮箱")
    private String email;

    @ApiModelProperty("用户系统语言")
    private Languages lang;

    @ApiModelProperty("用户头像图片地址")
    private String avatar;

    @ApiModelProperty("用户电子签名图片地址")
    private String signature;

    @ApiModelProperty("个人照片")
    private String photo;

    @ApiModelProperty("用户状态")
    private UserState state;

    @ApiModelProperty(value = "任务代理人", notes = "用户外出/休假时代替处理相关任务的人员")
    private Long proxy;

    @TableField(exist = false)
    @ApiModelProperty("任务代理人姓名")
    private String proxyName;

    @ApiModelProperty(value = "人事状态", notes = "取数据字典-人事状态")
    private String personnelStatus;

    @ApiModelProperty("人事状态描述")
    @TableField(exist = false)
    private String personnelStatusName;

    @ApiModelProperty("入职时间")
    private LocalDate joinDate;

    @ApiModelProperty("转正日期")
    private LocalDate employmentConfirmationDate;

    @ApiModelProperty("离职日期")
    private LocalDate resignationDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Long getTenantName() {
        return tenantName;
    }

    public void setTenantName(Long tenantName) {
        this.tenantName = tenantName;
    }

    public List<AssociationUserAndOrganization> getOrganizations() {
        return organizations;
    }

    public void setOrganizations(List<AssociationUserAndOrganization> organizations) {
        this.organizations = organizations;
    }

    public List<AssociationUserAndPosition> getPositions() {
        return positions;
    }

    public void setPositions(List<AssociationUserAndPosition> positions) {
        this.positions = positions;
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
