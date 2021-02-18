package com.mright.discuss.platform.service;

import com.mright.discuss.platform.entity.po.User;

import java.util.Set;

/**
 * @author: mright
 * @date: Created in 2021/2/15 9:06 下午
 * @desc:
 */
public interface IUserService {

    /**
     * 根据ID查询用户详情
     *
     * @param id ID
     * @return 用户详情
     */
    User queryById(Integer id);

    User findByUsername(String username);

    Set<String> findPermissions(String username);
}
