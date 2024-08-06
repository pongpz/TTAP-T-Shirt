package com.project.ttaptshirt.controller;

import com.project.ttaptshirt.repository.HoaDonChiTietRepository;
import com.project.ttaptshirt.repository.HoaDonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class HoaDonChiTietController {

    @Autowired
    HoaDonChiTietRepository hdctr;

    @Autowired
    HoaDonRepository hdr;

    @GetMapping("/admin/hoa-don-chi-tiet/hien-thi/{idHD}")
    public String hienThi(@PathVariable("idHD") Long id, Model model){
        model.addAttribute("listHDCT",hdctr.getHoaDonChiTietByHoaDonId(id));
        model.addAttribute("listHD", hdr.findAll());
        return "admin/hoadon/hoa-don";
    }
}
