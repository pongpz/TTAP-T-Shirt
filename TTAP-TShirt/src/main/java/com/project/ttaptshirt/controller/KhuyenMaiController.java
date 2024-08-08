package com.project.ttaptshirt.controller;

import com.project.ttaptshirt.entity.KhuyenMai;
import com.project.ttaptshirt.repository.KhuyenMaiRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Date;
import java.time.LocalDate;

@Controller
@RequestMapping("/admin/khuyen-mai")
public class KhuyenMaiController {

    @Autowired
    KhuyenMaiRepo kmrp;

    @GetMapping("/hien-thi")
    public String hienThi(Model model){
        model.addAttribute("listKM",kmrp.findAll());
        return "admin/khuyenmai/khuyen-mai";
    }

    @PostMapping("/add")
    public String them(KhuyenMai km){
        km.setNgayTao(Date.valueOf(LocalDate.now()));
        km.setNgaySua(Date.valueOf(LocalDate.now()));
        kmrp.save(km);
        return "redirect:/admin/khuyen-mai/hien-thi";
    }

    @PostMapping("/update/{idKM}")
    public String sua(KhuyenMai km, @PathVariable("idKM") Long id){
        km.setNgaySua(Date.valueOf(LocalDate.now()));
        km.setNgayTao(kmrp.getReferenceById(id).getNgayTao());
        km.setId(id);
        kmrp.save(km);
        return "redirect:/admin/khuyen-mai/hien-thi";
    }

    @GetMapping("/detail/{idKM}")
    public String detail(Model model, @PathVariable("idKM") Long id){
        model.addAttribute("listKM",kmrp.findAll());
        model.addAttribute("listDetail",kmrp.getReferenceById(id));
        return "admin/khuyenmai/khuyen-mai-detail";
    }

    @GetMapping("/form-add")
    public String formAdd(){
        return "admin/khuyenmai/formAddKM";
    }


    @GetMapping("/delete/{idKM}")
    public String delete(@PathVariable("idKM") Long id){
        KhuyenMai km = kmrp.getReferenceById(id);

        if (km.getTrangThai().equals("Hoạt động")){
            km.setTrangThai("Ngưng hoạt động");
            km.setNgaySua(Date.valueOf(LocalDate.now()));
        }else {
            km.setTrangThai("Hoạt động");
            km.setNgaySua(Date.valueOf(LocalDate.now()));
        }
        kmrp.save(km);
        return "redirect:/admin/khuyen-mai/hien-thi";
    }
}
