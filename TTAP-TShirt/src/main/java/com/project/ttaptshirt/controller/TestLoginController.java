package com.project.ttaptshirt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/login")
public class TestLoginController {

    @GetMapping("")
    public String login(){
        return "/user/login";
    }

}
