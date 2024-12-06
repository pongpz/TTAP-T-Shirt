package com.project.ttaptshirt.controller.adminController;

import com.project.ttaptshirt.entity.KieuDang;
import com.project.ttaptshirt.repository.KieuDangRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/kieu-dang")
public class KieuDangController {

    @Autowired
    KieuDangRepository kieuDangRepository;

    @GetMapping("/view")
    public String viewKieuDang(Model model){
        model.addAttribute("listKieuDang",kieuDangRepository.findAll());
        return "admin/thuoctinhsanpham/kieu-dang";
    }

    @PostMapping("/add")
    public String addKieuDang(KieuDang kieuDang, RedirectAttributes redirectAttributes){
        kieuDangRepository.save(kieuDang);
        redirectAttributes.addFlashAttribute("createSuccess", true);
        return "redirect:/admin/kieu-dang/view";
    }

    @PostMapping("/update/{id}")
    public String updateKieuDang(KieuDang kieuDang){
        kieuDangRepository.save(kieuDang);
        return "redirect:/admin/kieu-dang/view";
    }

    @GetMapping("/delete")
    public String deleteKieuDang(@PathVariable("id") Long id, RedirectAttributes redirectAttributes){
        kieuDangRepository.delete(kieuDangRepository.getReferenceById(id));
        redirectAttributes.addFlashAttribute("updateSuccess", true);
        return "redirect:/admin/kieu-dang/view";
    }

    @GetMapping("/update/{id}")
    public String detailKieuDang(@PathVariable("id") Long id, Model model){
        KieuDang kieuDang = kieuDangRepository.findById(id).orElse(null);
        model.addAttribute("detailKieuDang",kieuDang);
        model.addAttribute("listKieuDang",kieuDangRepository.findAll());
        return "admin/thuoctinhsanpham/update-kieu-dang";
    }
}
