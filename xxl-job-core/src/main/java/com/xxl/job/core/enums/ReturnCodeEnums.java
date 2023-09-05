package com.xxl.job.core.enums;

import java.util.Arrays;

/**
 * @author 空青
 * @date 2022-01-18
 */
public enum ReturnCodeEnums {

    SUCCESS(200, "成功"),
    FAIL(500, "失败"),
    WAIT(0, "未开始"),
    RUNNING(100, "执行中");

    private Integer code;
    private String text;

    ReturnCodeEnums(Integer code, String text) {
        this.code = code;
        this.text = text;
    }

    public Integer getCode() {
        return code;
    }

    public String getText() {
        return text;
    }

    public static String ofText(Integer code){
        return Arrays.stream(values()).filter(item -> item.getCode().equals(code)).findFirst().map(ReturnCodeEnums::getText).orElse("");
    }
}
