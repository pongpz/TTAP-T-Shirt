package com.project.ttaptshirt.controller;

import com.project.ttaptshirt.repository.HoaDonChiTietRepository;
import com.project.ttaptshirt.repository.HoaDonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HoaDonChiTietController {

    @Autowired
    HoaDonChiTietRepository hdctr;

    @Autowired
    HoaDonRepository hdr;

    @GetMapping("/admin/hoa-don-chi-tiet/hien-thi")
    public String hienThi(@RequestParam Long id, Model model, @RequestParam(defaultValue = "0") Integer page){
        model.addAttribute("listHDCT",hdctr.getHoaDonChiTietByHoaDonId(id));
        model.addAttribute("listHD", hdr.findAll());
        model.addAttribute("page",page);
        return "admin/hoadon/hoa-don";
    }
}
