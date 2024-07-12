package com.project.ttaptshirt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
@RequestMapping("/admin/ban-hang")
public class BanHangController {
    @GetMapping("")
    public String openBanHangPage(){
        return "admin/banhangtaiquay/index";
    }
}
