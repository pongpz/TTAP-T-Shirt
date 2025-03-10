package com.project.ttaptshirt.controller.adminController;

import com.project.ttaptshirt.entity.KichCo;
import com.project.ttaptshirt.entity.TaiKhoan;
import com.project.ttaptshirt.security.CustomUserDetail;
import com.project.ttaptshirt.service.KichCoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/kich-co")
public class KichCoController {

    @Autowired
    KichCoService kichCoService;

    @GetMapping("")
    public String openSizePage(Model model, Authentication authentication){
        if (authentication != null) {
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            TaiKhoan user = customUserDetail.getUser();
            model.addAttribute("userLogged", user);
        }
        model.addAttribute("listKichCo", kichCoService.findAll());
        return "admin/thuoctinhsanpham/kich-co";
    }

    @GetMapping("/update-kich-co/{id}")
    public String openPageUpdate(@PathVariable("id") Long id, Model model ){
        KichCo kichCo = kichCoService.findById(id);
        model.addAttribute("kichCo", kichCo);
        return "admin/thuoctinhsanpham/update-kich-co";
    }

    @PostMapping("/save-kich-co")
    public String createNewKichCo(KichCo kichCo, RedirectAttributes redirectAttributes){
        kichCoService.save(kichCo);
        redirectAttributes.addFlashAttribute("createSuccess", true);
        return "redirect:/admin/kich-co";
    }

    @PostMapping("update-kich-co/{id}")
    public String updateMauSac(KichCo kichCo, @PathVariable("id") Long id, RedirectAttributes redirectAttributes){
        kichCo.setId(id);
        kichCoService.save(kichCo);
        redirectAttributes.addFlashAttribute("updateSuccess", true);
        return "redirect:/admin/kich-co";
    }

}
