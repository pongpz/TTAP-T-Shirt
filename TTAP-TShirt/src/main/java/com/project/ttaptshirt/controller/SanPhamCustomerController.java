package com.project.ttaptshirt.controller;

import com.project.ttaptshirt.repository.ChiTietSanPhamRepository;
import com.project.ttaptshirt.repository.SanPhamRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.DecimalFormat;

@Controller("sanPhamCustomerController")
@RequestMapping("/TTAP")
public class SanPhamCustomerController {

    @Autowired
    SanPhamRepository sanPhamRepository;

    @Autowired
    ChiTietSanPhamRepository chiTietSanPhamRepository;

    @GetMapping("/san-pham")
    public String sanPhamCustomer(HttpServletRequest request , Model model) {
        model.addAttribute("requestURI", request.getRequestURI());

        model.addAttribute("listctsp", chiTietSanPhamRepository.findAll());


        return "user/home/sanpham";

    }

    public String formatCurrency(double amount) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(amount);
    }
}
