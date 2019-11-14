package com.github.app.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.app.entity.user.User;
import com.github.app.pojo.vo.AjaxResult;
import com.github.app.service.user.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 文件描述 用户相关控制器
 *
 * @author ouyangjie
 * @Title: UserCtrl
 * @ProjectName spring-book
 * @date 2019/11/14 4:07 PM
 */
@Controller
@RequestMapping("/user")
@Api(value = "用户相关", tags = {"用户"})
public class UserCtrl {
    @Autowired
    private UserService userService;

    /**
     * 用户信息清单
     */
    @ApiOperation("用户信息清单")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult<List<User>> list(@RequestBody User query) {
        List<User> list = userService.fetch(query);
        return AjaxResult.successResult(list);
    }
}
