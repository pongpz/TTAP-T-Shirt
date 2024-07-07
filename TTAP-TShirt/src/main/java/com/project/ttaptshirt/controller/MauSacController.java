package com.project.ttaptshirt.controller;

import com.project.ttaptshirt.entity.MauSac;
import com.project.ttaptshirt.service.MauSacService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/mau-sac")
public class MauSacController {


    @Autowired
    MauSacService mauSacService;
    @GetMapping("/test")
    public String test(){
        return "/admin/test";
    }

    @GetMapping("")
    public String openColorPage(Model model){
        model.addAttribute("listMauSac",mauSacService.findAll());
        return "/admin/mau-sac";
    }

    @GetMapping("/delete-mau-sac/{id}")
    public String deleteMauSac(@PathVariable("id") Long id ){
            mauSacService.deleteById(id);
        return "redirect:/admin/mau-sac";
    }
    @PostMapping("/save-mau-sac")
    public String createNewMauSac(MauSac mauSac){
        mauSacService.save(mauSac);
        return "redirect:/admin/mau-sac";
    }

    @PostMapping("update-mau-sac/{id}")
    public String updateMauSac(MauSac mauSac, @PathVariable("id") Long id){
        mauSac.setId(id);
        mauSacService.save(mauSac);
        return "redirect:/admin/mau-sac";
    }

    @GetMapping("/update-mau-sac/{id}")
    public String openPageUpdate(@PathVariable("id") Long id,Model model ){
        MauSac mauSac = mauSacService.findById(id);
        model.addAttribute("mauSac",mauSac);
        return "/admin/mau-sac-update";
    }


}
