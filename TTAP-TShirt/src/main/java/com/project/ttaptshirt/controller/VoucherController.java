package com.project.ttaptshirt.controller;

import com.project.ttaptshirt.entity.MaGiamGia;
import com.project.ttaptshirt.repository.VoucherRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class VoucherController {

    @Autowired
    VoucherRepo vr;
    @GetMapping("/admin/voucher/hien-thi")
    public String hienThi(Model model){
        model.addAttribute("list",vr.findAll());
        return "admin/voucher/voucher";
    }

    @PostMapping("/admin/voucher/add")
    public String add(MaGiamGia voucher){

        vr.save(voucher);
        return "redirect:/admin/voucher/hien-thi";
    }

    @GetMapping("/admin/voucher/detail/{idVC}")
    public String detaail(@PathVariable("idVC") Long id, Model model){
        MaGiamGia voucher = vr.getReferenceById(id);
        model.addAttribute("list",vr.findAll());
        model.addAttribute("listDetail",vr.getReferenceById(id));
        return "/admin/voucher/voucher-detail";
    }

    @GetMapping("/admin/voucher/delete/{idVC}")
    public String delete(@PathVariable("idVC") Long id){
        MaGiamGia voucher = vr.getReferenceById(id);

        if (voucher.getTrangThai()){
            voucher.setTrangThai(false);
            voucher.setNgaySua(LocalDateTime.now());
        }else {
            voucher.setTrangThai(true);
            voucher.setNgaySua(LocalDateTime.now());
        }
        vr.save(voucher);
        return "redirect:/admin/voucher/hien-thi";
    }
}
