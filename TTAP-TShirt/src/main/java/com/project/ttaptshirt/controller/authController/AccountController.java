package com.project.ttaptshirt.controller.authController;


import com.project.ttaptshirt.entity.KhachHang;
import com.project.ttaptshirt.entity.User;
import com.project.ttaptshirt.service.KhachHangService;
import com.project.ttaptshirt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

@Controller
public class AccountController {

    @Autowired
    UserService userService;

    @Autowired
    KhachHangService khachHangService;



    @GetMapping("/login")
    public String showLoginForm() {
        return "/user/login";
    }


    @GetMapping("/register")
    public String showSignUpForm(){
        return "/user/register";
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }
    @PostMapping("/register")
    public String createNewUser(User user, @RequestParam("password") String passwordString,
                                RedirectAttributes redirectAttributes) {

        try {
            String password = new BCryptPasswordEncoder().encode(passwordString);
            user.setPassword(password);
            user.setEnable(true);
            user.setNgayTao(LocalDate.now());
            userService.save(user);
            System.out.println( user.toString());
            KhachHang khachHang = new KhachHang();
            khachHang.setHoTen(user.getHoTen());
            khachHang.setSoDienThoai(user.getSoDienthoai());
            khachHang.setNgayTao(LocalDate.now());
            User user1 = userService.findUserByUsername(user.getUsername());
            khachHang.setUser(user1);
            khachHangService.save(khachHang);
            userService.insertDefaultUserRole(user1.getId());
            System.out.println("thanh cong roi");
            redirectAttributes.addFlashAttribute("isRegisterSuccess", true);
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("isRegisterSuccess", false);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            System.out.println(e);
            System.out.println("deo on roi");
            return "redirect:/register";
        }
    }

}
