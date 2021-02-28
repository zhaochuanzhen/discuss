package com.mright.discuss.platform.controller;

import com.mright.discuss.framework.constant.CodeEnum;
import com.mright.discuss.framework.constant.CommonResponse;
import com.mright.discuss.platform.entity.po.Blog;
import com.mright.discuss.platform.service.IBlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: mright
 * @date: Created in 2021/2/23 11:39 下午
 * @desc:
 */
@RestController
@RequestMapping("/blog")
public class BlogController {

    @Autowired
    private IBlogService iBlogService;

    @GetMapping("/list")
    public CommonResponse<List<Blog>> list(Integer page) {
        List<Blog> blogs = iBlogService.list(page);
        return new CommonResponse<>(CodeEnum.RES_OK.getCode(), blogs);
    }

    @GetMapping("/detail")
    public CommonResponse<Blog> detail(Integer id) {
        Blog blog = iBlogService.detail(id);
        return new CommonResponse<>(CodeEnum.RES_OK.getCode(), blog);
    }

    @PostMapping("/add")
    public CommonResponse<Boolean> add(@RequestBody Blog blog) {
        blog.setUserId(1);
        blog.setImg("https://pic2.zhimg.com/50/v2-2132494e8d3a3f31ba16aad875d9c8f3_400x224.jpg");
        blog.setIntroduction("常青： 这是我本人亲身实践快速成为某领域的高手的经验，精华提纯版，全文共5000多字，它是一套完整的知识体系，答应我看完它，看完这一篇，保证你就知道怎么去建立自己的知识体系了。 建议先收藏，后阅读，以供随…");
        int count = iBlogService.add(blog);
        return new CommonResponse<>(CodeEnum.RES_OK.getCode(), count > 0);
    }
}
