package com.project.ttaptshirt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/ttap-tshirt")
public class HomeController {
    @GetMapping("")
    public String openBanHangPage(){
        return "index";
    }
}
