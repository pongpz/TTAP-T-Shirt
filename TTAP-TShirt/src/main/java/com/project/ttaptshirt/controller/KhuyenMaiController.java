package com.project.ttaptshirt.controller;

import com.project.ttaptshirt.entity.KhuyenMai;
import com.project.ttaptshirt.repository.KhuyenMaiRepo;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.sql.Date;
import java.time.LocalDate;

@Controller
@RequestMapping("/admin/khuyen-mai")
public class KhuyenMaiController {

    @Autowired
    KhuyenMaiRepo kmrp;

    @GetMapping("/hien-thi")
    public String hienThi(Model model){
        for(KhuyenMai km : kmrp.findAll()){
            if(km.getNgayKetThuc().compareTo(Date.valueOf(LocalDate.now())) < 0){
                km.setTrangThai("Ngưng hoạt động");
            }
        }
        model.addAttribute("listKM",kmrp.findAll());
        return "admin/khuyenmai/khuyen-mai";
    }

    @PostMapping("/add")
    public String them(@Valid KhuyenMai km, Errors errors, Model model){
        String errorMessage;
        if(errors.hasErrors()){
            if (km.getTen().trim().isEmpty() || km.getNgayBatDau() == null || km.getNgayKetThuc() == null || km.getGiaTriGiam() == null){
                errorMessage = "Vui lòng điền đủ trường";
                model.addAttribute("errorMessage",errorMessage);
                return "admin/khuyenmai/formAddKM";
            }
        }
        if(km.getNgayBatDau().compareTo(km.getNgayKetThuc()) > 0 || km.getNgayKetThuc().compareTo(Date.valueOf(LocalDate.now())) < 0){
            errorMessage = "Ngày bắt đầu hoặc ngày kết thúc không hợp lệ";
            model.addAttribute("errorMessage", errorMessage);
            return "admin/khuyenmai/formAddKM";
        }else if(km.getGiaTriGiam() >= 100 && km.getHinhThuc() == 2 || km.getGiaTriGiam().isNaN() || km.getGiaTriGiam() <0){
                errorMessage = "Giá trị giảm không hợp lệ";
                model.addAttribute("errorMessage",errorMessage);
                return "admin/khuyenmai/formAddKM";
            }else {
                km.setNgayTao(Date.valueOf(LocalDate.now()));
                km.setNgaySua(Date.valueOf(LocalDate.now()));
                kmrp.save(km);
                return "redirect:/admin/khuyen-mai/hien-thi";
            }
    }

    @PostMapping("/update/{idKM}")
    public String sua(@Valid KhuyenMai km, @PathVariable("idKM") Long id, Errors errors, Model model){
        String errorMessage;
        if(errors.hasErrors()){
            if (km.getTen().trim().isEmpty() || km.getNgayBatDau() == null || km.getNgayKetThuc() == null || km.getGiaTriGiam() == null) {
                errorMessage = "Vui lòng điền đủ trường";
                model.addAttribute("errorMessage", errorMessage);
                return "admin/khuyenmai/khuyen-mai-detail/"+id;
            }
        }
        if(km.getNgayBatDau().compareTo(km.getNgayKetThuc()) > 0 || km.getNgayKetThuc().compareTo(Date.valueOf(LocalDate.now())) < 0){
            errorMessage = "Ngày bắt đầu hoặc ngày kết thúc không hợp lệ";
            model.addAttribute("errorMessage", errorMessage);
            return "admin/khuyenmai/khuyen-mai-detail/"+id;
        }else if(km.getGiaTriGiam() >= 100 && km.getHinhThuc() == 2 || km.getGiaTriGiam().isNaN() || km.getGiaTriGiam() <0){
            errorMessage = "Giá trị giảm không hợp lệ";
            model.addAttribute("errorMessage",errorMessage);
            return "admin/khuyenmai/khuyen-mai-detail/"+id;
        }else {
            km.setNgaySua(Date.valueOf(LocalDate.now()));
            km.setNgayTao(kmrp.getReferenceById(id).getNgayTao());
            km.setId(id);
            kmrp.save(km);
            return "redirect:/admin/khuyen-mai/hien-thi";
        }
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
