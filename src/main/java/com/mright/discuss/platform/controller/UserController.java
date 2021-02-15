package com.mright.discuss.platform.controller;

import com.mright.discuss.framework.constant.CodeEnum;
import com.mright.discuss.framework.constant.CommonResponse;
import com.mright.discuss.platform.entity.po.User;
import com.mright.discuss.platform.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: mright
 * @date: Created in 2021/2/15 9:04 下午
 * @desc:
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService iUserService;

    @GetMapping("/queryById")
    public CommonResponse<User> queryById(Integer id) {
        if (id == null) {
            return new CommonResponse<>(CodeEnum.RES_PARAM_ERROR.getCode(), "ID不能为空");
        }
        User user = iUserService.queryById(id);
        return new CommonResponse<>(CodeEnum.RES_OK.getCode(), user);
    }
}
