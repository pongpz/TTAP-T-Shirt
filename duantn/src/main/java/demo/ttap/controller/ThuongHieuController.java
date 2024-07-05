package demo.ttap.controller;

import demo.ttap.entity.ThuongHieu;
import demo.ttap.service.ThuongHieuInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ThuongHieuController {
    @Autowired
    ThuongHieuInterface thi;

    @GetMapping("/thuong-hieu/view")
    public String viewThuongHieu(Model model){
        model.addAttribute("listThuongHieu",thi.findAll());
        return "viewThuongHieu";
    }

    @PostMapping("/thuong-hieu/add")
    public String addThuongHieu(ThuongHieu th){
        thi.save(th);
        return "redirect:/thuong-hieu/view";
    }

    @PostMapping("/thuong-hieu/update")
    public String updateThuongHieu(ThuongHieu th){
        thi.save(th);
        return "redirect:/thuong-hieu/view";
    }

    @GetMapping("/thuong-hieu/delete")
    public String deleteThuongHieu(@RequestParam("id") Integer id){
        thi.delete(thi.getReferenceById(id));
        return "redirect:/thuong-hieu/view";
    }

    @GetMapping("thuong-hieu/detail")
    public String detailThuongHieu(@RequestParam("id") Integer id, Model model){
        model.addAttribute("detailThuongHieu",thi.getReferenceById(id));
        model.addAttribute("listThuongHieu",thi.findAll());
        return "viewThuongHieu";
    }
}
