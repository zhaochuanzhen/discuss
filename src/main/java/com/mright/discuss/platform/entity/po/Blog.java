package com.mright.discuss.platform.entity.po;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: mright
 * @date: Created in 2021/2/23 11:39 下午
 * @desc:
 */
@Data
public class Blog implements Serializable {
    private static final long serialVersionUID = -9214361593137033578L;

    private Integer id;
    private Integer userId;
    private String title;
    private String img;
    private String introduction;
    private String content;
    private Date createTime;
    private Date updateTime;
    private Integer isDelete;
}
