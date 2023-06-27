package com.inmaytide.orbit.uaa.domain.consts;

/**
 * @author inmaytide
 * @since 2023/6/26
 */
public enum AreaType {

    PROCESS(1, "按生产工艺划分");

    private final int value;

    private final String name;


    AreaType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
