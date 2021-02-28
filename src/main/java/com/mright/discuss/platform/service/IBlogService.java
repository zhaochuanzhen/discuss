package com.mright.discuss.platform.service;

import com.mright.discuss.platform.entity.po.Blog;

import java.util.List;

/**
 * @author: mright
 * @date: Created in 2021/2/23 11:42 下午
 * @desc:
 */
public interface IBlogService {
    List<Blog> list(Integer page);

    Blog detail(Integer id);

    int add(Blog blog);
}
