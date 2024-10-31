package com.project.ttaptshirt.controller.adminController;

import com.project.ttaptshirt.entity.KichCo;
import com.project.ttaptshirt.service.KichCoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/kich-co")
public class KichCoController {

    @Autowired
    KichCoService kichCoService;

    @GetMapping("")
    public String openSizePage(Model model){
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
    public String createNewKichCo(KichCo kichCo){
        kichCoService.save(kichCo);
        return "redirect:/admin/kich-co";
    }

    @PostMapping("update-kich-co/{id}")
    public String updateMauSac(KichCo kichCo, @PathVariable("id") Long id){
        kichCo.setId(id);
        kichCoService.save(kichCo);
        return "redirect:/admin/kich-co";
    }

}
