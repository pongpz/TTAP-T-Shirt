package com.project.ttaptshirt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/TTAP/")
public class IndexController {
    @GetMapping("home")
    public String login(){
        return "/index";
    }
}