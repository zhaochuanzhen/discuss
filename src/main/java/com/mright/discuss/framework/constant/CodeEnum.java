package com.mright.discuss.framework.constant;

import com.mright.discuss.platform.entity.dto.HttpResult;

/**
 * @author: mright
 * @date: Created in 2021/2/17 9:28 下午
 * @desc:
 */
public enum CodeEnum {
    /**
     * 入参校验异常
     */
    RES_PARAM_ERROR(1001, "入参校验失败"),
    RES_OK(1, "正常");

    private Integer code;
    private String msg;

    public Integer getCode() {
        return code;
    }

    CodeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
