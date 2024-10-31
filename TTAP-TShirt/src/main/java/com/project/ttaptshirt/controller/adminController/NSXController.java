package com.project.ttaptshirt.controller.adminController;

import com.project.ttaptshirt.entity.NSX;
import com.project.ttaptshirt.repository.NSXRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/nsx")
public class NSXController {
    @Autowired
    NSXRepository nsxi;

    @GetMapping("/view")
    public String viewChatLieu(Model model) {
        model.addAttribute("listNSX", nsxi.findAll());
        return "admin/thuoctinhsanpham/nha-san-xuat";
    }

    @PostMapping("/add")
    public String addChatLieu(NSX nsx) {
        nsxi.save(nsx);
        return "redirect:/admin/nsx/view";
    }

    @PostMapping("/update/{id}")
    public String updateChatLieu(@PathVariable("id") Long id, NSX nsx) {
        nsx.setId(id);
        nsxi.save(nsx);
        return "redirect:/admin/nsx/view";
    }

    @GetMapping("/delete/{id}")
    public String deleteChatLieu(@PathVariable("id") Long id) {
        nsxi.delete(nsxi.getReferenceById(id));
        return "redirect:/admin/nsx/view";
    }

    @GetMapping("/update/{id}")
    public String detailChatLieu(@PathVariable("id") Long id, Model model) {
        NSX nsx = nsxi.findById(id).orElse(null);
        model.addAttribute("detailNSX", nsx);
        model.addAttribute("listNSX", nsxi.findAll());
        return "admin/thuoctinhsanpham/update-nha-san-xuat";
    }
}
