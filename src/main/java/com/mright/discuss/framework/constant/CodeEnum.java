package com.mright.discuss.framework.constant;

/**
 * @author: mright
 * @date: Created in 2021/2/15 9:09 下午
 * @desc: 编码枚举
 */
public enum CodeEnum {

    /**
     * 响应正常：OK
     */
    RES_OK(1, "OK"),

    /**
     * 入参校验失败
     */
    RES_PARAM_ERROR(1001, "入参检验失败");

    private Integer code;
    private String msg;

    CodeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
