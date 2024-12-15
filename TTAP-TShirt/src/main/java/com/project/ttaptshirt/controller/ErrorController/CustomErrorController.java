package com.project.ttaptshirt.controller.ErrorController;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/handlePageError")
public class CustomErrorController {

    // Handle the general error page
    @GetMapping("/forbiddenPage")
    public String forbiddenPage(Model model) {
        return "/handlePageError/forbiddenPage"; // The forbiddenPage.html template
    }
}
