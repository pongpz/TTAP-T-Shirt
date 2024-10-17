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

    // GET request to open the size page
    @GetMapping("")
    public String openSizePage(Model model){
        model.addAttribute("listKichCo", kichCoService.findAll());
        return "admin/thuoctinhsanpham/kich-co";
    }

    // GET request to open the update page
    @GetMapping("/update-kich-co/{id}")
    public String openPageUpdate(@PathVariable("id") Long id, Model model ){
        KichCo kichCo = kichCoService.findById(id);
        model.addAttribute("kichCo", kichCo);
        return "admin/thuoctinhsanpham/update-kich-co";
    }

    // POST request to create a new size
    @PostMapping("/save-kich-co")
    public String createNewKichCo(KichCo kichCo){
        kichCoService.save(kichCo);
        return "redirect:/admin/kich-co";
    }

    // PUT request to update an existing size
    @PutMapping("/update-kich-co/{id}")
    public String updateKichCo(KichCo kichCo, @PathVariable("id") Long id){
        kichCo.setId(id);
        kichCoService.save(kichCo);
        return "redirect:/admin/kich-co";
    }

    // DELETE request to delete an existing size
    @DeleteMapping("/delete-kich-co/{id}")
    public String deleteKichCo(@PathVariable("id") Long id){
        kichCoService.deleteById(id);
        return "redirect:/admin/kich-co";
    }

    // Test method
    @GetMapping("/test")
    public String test(){
        return "admin/thuoctinhsanpham/kich-co";
    }

}
