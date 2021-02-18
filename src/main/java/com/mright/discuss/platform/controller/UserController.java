package com.mright.discuss.platform.controller;

import com.mright.discuss.framework.constant.CodeEnum;
import com.mright.discuss.framework.constant.CommonResponse;
import com.mright.discuss.framework.security.JwtAuthenticatioToken;
import com.mright.discuss.framework.utils.SecurityUtils;
import com.mright.discuss.platform.entity.dto.HttpResult;
import com.mright.discuss.platform.entity.dto.LoginBean;
import com.mright.discuss.platform.entity.po.User;
import com.mright.discuss.platform.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

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

    @Autowired
    private AuthenticationManager authenticationManager;

    @PreAuthorize("hasAuthority('sys:user:view')")
    @GetMapping("/queryById")
    public CommonResponse<User> queryById(Integer id) {
        if (id == null) {
            return new CommonResponse<>(CodeEnum.RES_PARAM_ERROR.getCode(), "ID不能为空");
        }
        User user = iUserService.queryById(id);
        return new CommonResponse<>(CodeEnum.RES_OK.getCode(), user);
    }

    /**
     * 登录接口
     */
    @PostMapping(value = "/login")
    public HttpResult login(@RequestBody LoginBean loginBean, HttpServletRequest request) throws IOException {
        String username = loginBean.getUsername();
        String password = loginBean.getPassword();

        // 系统登录认证
        JwtAuthenticatioToken token = SecurityUtils.login(request, username, password, authenticationManager);

        return HttpResult.ok(token);
    }

    @PreAuthorize("hasAuthority('sys:user:view')")
    @GetMapping(value="/findAll")
    public HttpResult findAll() {
        return HttpResult.ok("the findAll service is called success.");
    }

    @PreAuthorize("hasAuthority('sys:user:edit')")
    @GetMapping(value="/edit")
    public HttpResult edit() {
        return HttpResult.ok("the edit service is called success.");
    }

    @PreAuthorize("hasAuthority('sys:user:delete')")
    @GetMapping(value="/delete")
    public HttpResult delete() {
        return HttpResult.ok("the delete service is called success.");
    }
}
