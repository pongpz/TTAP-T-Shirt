package com.project.ttaptshirt.controller.customerController;


import com.project.ttaptshirt.entity.DiaChi;
import com.project.ttaptshirt.entity.User;
import com.project.ttaptshirt.security.CustomUserDetail;
import com.project.ttaptshirt.service.DiaChiService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/TTAP/user")

public class DiaChiController {

    @Autowired
    private DiaChiService serDc;

    @GetMapping("home")
    public String home(Model mol){
        List<DiaChi> dcList = serDc.findAll();
        mol.addAttribute("dcLst",dcList);
        return"/user/diachi/index";
    }

    @GetMapping("new")
    public String add(Model mol ){
        mol.addAttribute("diachi", new DiaChi());
        return "/user/diachi/dangky";
    }

    @PostMapping("save")
    public String createUser(@Valid @ModelAttribute DiaChi dc, BindingResult result, Model mol) {
        if (result.hasErrors()) {
            return "/user/diachi/dangky";
        }
        serDc.save(dc);
        return "redirect:home";
    }


    @GetMapping("/addresses")
    public String getUserAddresses(Model model, Authentication authentication) {
        if (authentication != null) {
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            User user = customUserDetail.getUser();

            List<DiaChi> addresses = serDc.findAddressesByUser(user.getId());
            model.addAttribute("addresses", addresses);

            return "user/address-list"; // Trang hiển thị danh sách địa chỉ
        }
        return "redirect:/login";
    }

    @PostMapping("/address")
    public String createAddress(@ModelAttribute DiaChi address, Authentication authentication) {
        if (authentication != null) {
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            User user = customUserDetail.getUser();

            address.setUser(user); // Gắn địa chỉ với người dùng hiện tại
            serDc.save(address); // Lưu địa chỉ vào cơ sở dữ liệu

            return "redirect:/TTAP/cart/view"; // Sau khi lưu, chuyển hướng đến danh sách địa chỉ
        }
        return "redirect:/login"; // Nếu chưa đăng nhập, chuyển hướng đến trang login
    }

}
