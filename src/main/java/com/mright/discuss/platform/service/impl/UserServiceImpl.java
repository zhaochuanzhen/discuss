package com.mright.discuss.platform.service.impl;

import com.mright.discuss.platform.dao.IUserDao;
import com.mright.discuss.platform.entity.po.User;
import com.mright.discuss.platform.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

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

    @Override
    public User findByUsername(String username) {
        User user = new User();
        user.setId(1);
        user.setUsername(username);
        String password = new BCryptPasswordEncoder().encode("123456");
        user.setPassword(password);
        return user;
    }

    @Override
    public Set<String> findPermissions(String username) {
        Set<String> permissions = new HashSet<>();
        permissions.add("sys:user:view");
        permissions.add("sys:user:add");
        permissions.add("sys:user:edit");
        permissions.add("sys:user:delete");
        return permissions;
    }
}
