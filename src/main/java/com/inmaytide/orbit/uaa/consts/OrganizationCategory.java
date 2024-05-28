package com.inmaytide.orbit.uaa.consts;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * 组织分类
 *
 * @author inmaytide
 * @since 2024/5/27
 */
public enum OrganizationCategory {

    GROUP("GROUP", "集团"),

    COMPANY("COMPANY", "公司"),

    DEPARTMENT("DEPARTMENT", "部门"),

    TEAM("TEAM", "班组");

    @EnumValue
    private final String value;

    private final String name;

    OrganizationCategory(String value, String name) {
        this.value = value;
        this.name = name;
    }

    @JsonCreator
    public static OrganizationCategory withValue(String value) {
        return Stream.of(values()).filter(e -> Objects.equals(value, e.getValue()))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
