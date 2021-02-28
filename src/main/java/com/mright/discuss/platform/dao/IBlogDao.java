package com.mright.discuss.platform.dao;

import com.mright.discuss.platform.entity.po.Blog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author: mright
 * @date: Created in 2021/2/23 11:43 下午
 * @desc:
 */
@Mapper
public interface IBlogDao {

    List<Blog> list(@Param("start") int start, @Param("size") int size);

    Blog detail(@Param("id") Integer id);

    int add(Blog blog);
}
