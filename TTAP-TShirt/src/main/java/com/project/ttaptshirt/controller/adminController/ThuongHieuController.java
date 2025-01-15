
package com.project.ttaptshirt.controller.adminController;


import com.project.ttaptshirt.entity.TaiKhoan;
import com.project.ttaptshirt.entity.ThuongHieu;
import com.project.ttaptshirt.repository.ThuongHieuRepository;
import com.project.ttaptshirt.security.CustomUserDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/thuong-hieu")
public class ThuongHieuController {
    @Autowired
    ThuongHieuRepository thi;

    @GetMapping("/view")
    public String viewThuongHieu(Model model, Authentication authentication){
        if (authentication != null) {
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            TaiKhoan user = customUserDetail.getUser();
            model.addAttribute("userLogged", user);
        }
        model.addAttribute("listThuongHieu",thi.findAll());
        return "admin/thuoctinhsanpham/thuong-hieu";
    }

    @PostMapping("/add")
    public String addThuongHieu(ThuongHieu th, RedirectAttributes redirectAttributes){
        thi.save(th);
        redirectAttributes.addFlashAttribute("createSuccess", true);
        return "redirect:/admin/thuong-hieu/view";
    }

    @PostMapping("/update/{id}")
    public String updateThuongHieu(ThuongHieu th, RedirectAttributes redirectAttributes){
        thi.save(th);
        redirectAttributes.addFlashAttribute("updateSuccess", true);
        return "redirect:/admin/thuong-hieu/view";
    }

    @GetMapping("/delete")
    public String deleteThuongHieu(@RequestParam("id") Long id){
        thi.delete(thi.getReferenceById(id));
        return "redirect:/admin/thuong-hieu/view";
    }

    @GetMapping("/update/{id}")
    public String detailThuongHieu(@PathVariable("id") Long id, Model model){
        ThuongHieu thuongHieu = thi.findById(id).orElse(null);
        model.addAttribute("detailThuongHieu",thuongHieu);
        model.addAttribute("listThuongHieu",thi.findAll());
        return "admin/thuoctinhsanpham/update-thuong-hieu";
    }
}
