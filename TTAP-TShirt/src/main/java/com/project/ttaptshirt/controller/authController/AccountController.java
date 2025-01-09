package com.project.ttaptshirt.controller.authController;


import com.project.ttaptshirt.entity.KhachHang;
import com.project.ttaptshirt.entity.NhanVien;
import com.project.ttaptshirt.entity.Role;
import com.project.ttaptshirt.entity.TaiKhoan;
import com.project.ttaptshirt.service.KhachHangService;
import com.project.ttaptshirt.repository.UserRepo;
import com.project.ttaptshirt.service.UserService;
import com.project.ttaptshirt.service.impl.KhachHangServiceImpl;
import com.project.ttaptshirt.service.impl.NhanVienServicelmpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
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
    KhachHangServiceImpl khachHangService;
    @Autowired
    UserRepo userRepo;
    @Autowired
    private KhachHangServiceImpl khachHangServiceImpl;
    @Autowired
    private NhanVienServicelmpl nhanVienServicelmpl;


    @GetMapping("/login")
    public String showLoginForm() {
        return "user/login";
    }


    @GetMapping("/register")
    public String showSignUpForm(Model model){
        TaiKhoan user = new TaiKhoan();
        model.addAttribute("user",user);
        return "user/register";
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }
    @PostMapping("/register")
    public String createNewUser(KhachHang khachHang ,TaiKhoan user, @RequestParam("password") String passwordString,
                                RedirectAttributes redirectAttributes, Model model) {

        if(userRepo.findUserByUsername(user.getUsername()) != null){
            model.addAttribute("usernameIsInvalid","Tài khoản đã tồn tại");
            model.addAttribute("user",user);
            return "user/register";
        }

//        else if (!user.getSoDienthoai().matches("\\d+")){
//            model.addAttribute("ErrorPhoneNumber","Số điện thoại không hợp lệ");
//            model.addAttribute("user",user);
//            return "user/register";
//        }
        else {
            String password = new BCryptPasswordEncoder().encode(passwordString);
            user.setPassword(password);
            user.setEnable(true);
            user.setNgayTao(LocalDate.now());
            Role role = new Role();
            role.setId(Long.parseLong("2"));
            user.setRole(role);
            khachHang.setTaiKhoan(user);
            userService.save(user);
            khachHangService.save(khachHang);
            redirectAttributes.addFlashAttribute("isRegisterSuccess", true);
            return "redirect:/login";
        }

    }

    @PostMapping("/registerNv")
    public String createNewUserNv(NhanVien nhanVien, TaiKhoan user, @RequestParam("password") String passwordString,
                                  RedirectAttributes redirectAttributes, Model model) {

        if(userRepo.findUserByUsername(user.getUsername()) != null){
            model.addAttribute("usernameIsInvalid","Tài khoản đã tồn tại");
            model.addAttribute("user",user);
            return "user/register";
        }

//        else if (!user.getSoDienthoai().matches("\\d+")){
//            model.addAttribute("ErrorPhoneNumber","Số điện thoại không hợp lệ");
//            model.addAttribute("user",user);
//            return "user/register";
//        }
        else {
            String password = new BCryptPasswordEncoder().encode(passwordString);
            user.setPassword(password);
            user.setEnable(true);
            user.setNgayTao(LocalDate.now());
            Role role = new Role();
            role.setId(Long.parseLong("1"));
            user.setRole(role);
            nhanVien.setTaiKhoan(user);
            userService.save(user);
            nhanVienServicelmpl.save(nhanVien);
            redirectAttributes.addFlashAttribute("isRegisterSuccess", true);
            return "redirect:/login";
        }

    }

}
