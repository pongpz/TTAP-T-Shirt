package com.project.ttaptshirt.controller.adminController;

import com.project.ttaptshirt.entity.MauSac;
import com.project.ttaptshirt.entity.TaiKhoan;
import com.project.ttaptshirt.security.CustomUserDetail;
import com.project.ttaptshirt.service.MauSacService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/mau-sac")
public class MauSacController {

    @Autowired
    MauSacService mauSacService;

    @GetMapping("")
    public String openColorPage(Model model, Authentication authentication) {
        if (authentication != null) {
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            TaiKhoan user = customUserDetail.getUser();
            model.addAttribute("userLogged", user);
        }
        model.addAttribute("listMauSac", mauSacService.findAll());
        model.addAttribute("mauSac", new MauSac());
        return "admin/thuoctinhsanpham/mau-sac";
    }

    @PostMapping("/save-mau-sac")
    public String createNewMauSac(@ModelAttribute MauSac mauSac, RedirectAttributes redirectAttributes) {
        mauSacService.save(mauSac);
        redirectAttributes.addFlashAttribute("createSuccess", true);
        return "redirect:/admin/mau-sac";
    }

    @PostMapping("/update-mau-sac/{id}")
    public String updateMauSac(@ModelAttribute MauSac mauSac, @PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        mauSac.setId(id);
        mauSacService.save(mauSac);
        redirectAttributes.addFlashAttribute("updateSuccess", true);

        return "redirect:/admin/mau-sac";
    }

    @GetMapping("/delete-mau-sac/{id}")
    public String deleteMauSac(@PathVariable("id") Long id) {
        mauSacService.deleteById(id);
        return "redirect:/admin/mau-sac";
    }

    @GetMapping("/update-mau-sac/{id}")
    public String openPageUpdate(@PathVariable("id") Long id, Model model) {
        MauSac mauSac = mauSacService.findById(id);
        model.addAttribute("mauSac", mauSac);
        return "admin/thuoctinhsanpham/update-mau-sac";
    }
}
