package com.github.app.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 文件描述 首页、登出等相关
 *
 * @author ouyangjie
 * @Title: IndexCtrl
 * @ProjectName spring-book
 * @date 2019/11/14 4:07 PM
 */
@Controller
@RequestMapping("/")
public class IndexCtrl {
    /**
     * 初始化
     *
     * @return index页面
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String init(HttpServletRequest request) {
        return "index";
    }
}
