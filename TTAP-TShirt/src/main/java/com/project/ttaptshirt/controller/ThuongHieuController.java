
package com.project.ttaptshirt.controller;


import com.project.ttaptshirt.entity.ThuongHieu;
import com.project.ttaptshirt.repository.ThuongHieuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ThuongHieuController {
    @Autowired
    ThuongHieuRepository thi;

    @GetMapping("/admin/thuong-hieu/view")
    public String viewThuongHieu(Model model){
        model.addAttribute("listThuongHieu",thi.findAll());
        return "admin/thuoctinhsanpham/thuong-hieu";
    }

    @PostMapping("/admin/thuong-hieu/add")
    public String addThuongHieu(ThuongHieu th){
        thi.save(th);
        return "redirect:/admin/thuong-hieu/view";
    }

    @PostMapping("/admin/thuong-hieu/update/{id}")
    public String updateThuongHieu(ThuongHieu th){
        thi.save(th);
        return "redirect:/admin/thuong-hieu/view";
    }

    @GetMapping("/admin/thuong-hieu/delete")
    public String deleteThuongHieu(@RequestParam("id") Long id){
        thi.delete(thi.getReferenceById(id));
        return "redirect:/admin/thuong-hieu/view";
    }

    @GetMapping("/admin/thuong-hieu/update/{id}")
    public String detailThuongHieu(@PathVariable("id") Long id, Model model){
        model.addAttribute("detailThuongHieu",thi.getReferenceById(id));
        model.addAttribute("listThuongHieu",thi.findAll());
        return "admin/thuoctinhsanpham/update-thuong-hieu";
    }
}
