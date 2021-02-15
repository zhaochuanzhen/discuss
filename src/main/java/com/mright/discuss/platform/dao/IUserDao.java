package com.mright.discuss.platform.dao;

import com.mright.discuss.platform.entity.po.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author: mright
 * @date: Created in 2021/2/15 8:54 下午
 * @desc:
 */
@Mapper
public interface IUserDao {

    /**
     * 根据ID查找用户详情
     *
     * @param id id
     * @return 用户详情
     */
    User queryById(@Param("id") Integer id);
}
