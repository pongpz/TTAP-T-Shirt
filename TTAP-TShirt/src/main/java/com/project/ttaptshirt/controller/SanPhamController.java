package com.project.ttaptshirt.controller;


import com.project.ttaptshirt.entity.SanPham;
import com.project.ttaptshirt.repository.SanPhamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/san-pham")
public class SanPhamController {

    @Autowired
    SanPhamRepository sanPhamRepository;
    @GetMapping
    public String index(Model model){
        List<SanPham> listSP = sanPhamRepository.findAll();
        model.addAttribute("listSP",listSP);
        return "/admin/sanpham/san-pham";
    }

    @GetMapping("/add")
    public String add(Model model){
        model.addAttribute("sanpham", new SanPham());
        return "/admin/sanpham/san-pham-new";
    }

    @PostMapping("/add")
    public String add(SanPham sanPham){
        sanPhamRepository.save(sanPham);
        return "redirect:/admin/san-pham";
    }

}
