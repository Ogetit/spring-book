package com.github.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {
    /**
     * 主页
     *
     * @return
     */
    @RequestMapping("/")
    public String index() {
        return "/index";
    }
}
