package com.mright.discuss.platform.entity.po;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: mright
 * @date: Created in 2021/2/15 8:50 下午
 * @desc: 用户表PO
 */
@Data
public class User implements Serializable {
    private static final long serialVersionUID = 5924650104404355589L;

    /**
     * 用户表主键
     */
    private Integer id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 头像
     */
    private String headImg;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除。0：未删除；1：已删除
     */
    private Integer isDelete;
}
