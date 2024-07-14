package com.project.ttaptshirt.controller;

import com.project.ttaptshirt.entity.HoaDon;
import com.project.ttaptshirt.service.HoaDonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/admin/ban-hang")
public class BanHangController {

    @Autowired
    HoaDonService hoaDonService;
    @GetMapping("")
    public String openBanHangPage(Model model){
        List<HoaDon> listHoaDon = hoaDonService.findAll();
        model.addAttribute("listHoaDon",listHoaDon);
        return "admin/banhangtaiquay/banhang";
    }

    @PostMapping("/newHoaDon")
    public String newHoaDon(){
        HoaDon hoaDon = new HoaDon();
        hoaDon.setMa("HD"+(int)(Math.random() * 1000000));
        hoaDon.setNgayTao(new java.sql.Date(new Date().getTime()));
        hoaDon.setLoaiDon(0);
        hoaDon.setTrangThai(0);
        hoaDonService.save(hoaDon);
        return "redirect:/admin/ban-hang";

    }
}
