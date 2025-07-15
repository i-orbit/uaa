package com.inmaytide.orbit.uaa.consts;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

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
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        return Stream.of(values())
                .filter(e -> value.equals(e.getValue()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid organization category value: " + value));
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
