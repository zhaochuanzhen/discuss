package com.mright.discuss.framework.constant;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: mright
 * @date: Created in 2021/2/15 9:05 下午
 * @desc:
 */
@Data
public class CommonResponse<T> implements Serializable {
    private static final long serialVersionUID = 2342369542565463553L;

    private Integer code;
    private String msg;
    private T data;

    public CommonResponse(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public CommonResponse(Integer code, T data) {
        this.code = code;
        this.data = data;
    }

    public CommonResponse(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
