package com.project.ttaptshirt.controller.customerController;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PreventNonApiRequest {
    @GetMapping("")
    public String home1() {
        return "redirect:/TTAP/trang-chu";
    }

    @GetMapping("/TTAP")
    public String home2() {
        return "redirect:/TTAP/trang-chu";
    }
}
