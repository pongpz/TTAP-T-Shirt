
package com.project.ttaptshirt.controller.adminController;

import com.project.ttaptshirt.entity.NSX;
import com.project.ttaptshirt.repository.NSXRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/nsx")
public class NSXController {
    @Autowired
    NSXRepository nsxi;

    @GetMapping("/view")
    public String viewChatLieu(Model model){
        model.addAttribute("listNSX",nsxi.findAll());
        return "admin/thuoctinhsanpham/nha-san-xuat";
    }

    @PostMapping("/add")
    public String addChatLieu(NSX nsx){
        nsxi.save(nsx);
        return "redirect:/admin/nsx/view";
    }

    @PostMapping("/admin/nsx/update/{id}")
    public String updateChatLieu(NSX nsx){
        nsxi.save(nsx);
        return "redirect:/admin/nsx/view";
    }

    @GetMapping("/admin/nsx/delete")
    public String deleteChatLieu(@PathVariable("id") Long id){
        nsxi.delete(nsxi.getReferenceById(id));
        return "redirect:/admin/nsx/view";
    }

    @GetMapping("/admin/nsx/update/{id}")
    public String detailChatLieu(@PathVariable("id") Long id, Model model){
        model.addAttribute("detailNSX",nsxi.getReferenceById(id));
        model.addAttribute("listNSX",nsxi.findAll());
        return "admin/thuoctinhsanpham/update-nha-san-xuat";
    }
}
