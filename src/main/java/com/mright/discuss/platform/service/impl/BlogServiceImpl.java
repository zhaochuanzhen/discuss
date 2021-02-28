package com.mright.discuss.platform.service.impl;

import com.mright.discuss.platform.dao.IBlogDao;
import com.mright.discuss.platform.entity.po.Blog;
import com.mright.discuss.platform.service.IBlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: mright
 * @date: Created in 2021/2/23 11:42 下午
 * @desc:
 */
@Service
public class BlogServiceImpl implements IBlogService {

    @Autowired
    private IBlogDao iBlogDao;

    @Override
    public List<Blog> list(Integer page) {
        page = (page == null || page <= 0) ? 1 : page;
        return iBlogDao.list(page * 10 - 10, 10);
    }

    @Override
    public Blog detail(Integer id) {

        return iBlogDao.detail(id);
    }

    @Override
    public int add(Blog blog) {
        return iBlogDao.add(blog);
    }
}
