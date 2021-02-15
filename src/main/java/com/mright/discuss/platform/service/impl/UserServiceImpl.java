package com.mright.discuss.platform.service.impl;

import com.mright.discuss.platform.dao.IUserDao;
import com.mright.discuss.platform.entity.po.User;
import com.mright.discuss.platform.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: mright
 * @date: Created in 2021/2/15 9:07 下午
 * @desc:
 */
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private IUserDao iUserDao;

    @Override
    public User queryById(Integer id) {
        return iUserDao.queryById(id);
    }
}
