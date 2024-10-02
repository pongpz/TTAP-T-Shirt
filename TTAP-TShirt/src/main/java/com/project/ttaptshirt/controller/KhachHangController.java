package com.project.ttaptshirt.controller;


import com.project.ttaptshirt.entity.DiaChi;
import com.project.ttaptshirt.entity.KhachHang;
import com.project.ttaptshirt.service.KhachHangService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/TTAP/KhachHang/customer")
public class KhachHangController {

    @Autowired
    private KhachHangService serKhachHang;

    @GetMapping("/home")
    public String home(Model mol){
        List<KhachHang> KhachHangList = serKhachHang.findAll();
        mol.addAttribute("nvLst",KhachHangList);
        return"/KhachHang/index";
    }

//    @GetMapping("/find")
//    public String findKhachHang(@RequestParam(value = "name", required = false) String name, Model mol){
//        List<KhachHang> KhachHangs;
//        if (name == null || name.isEmpty()) {
//            KhachHangs = serKhachHang.findAll(); // Assuming you have a method to retrieve all KhachHangs
//        } else {
//            KhachHangs = serKhachHang.findByKhachHang(name);
//        }
//        mol.addAttribute("empoLst",KhachHangs);
//        return "/KhachHang/nhanvien/index";
//    }

    @GetMapping("/find")
    public String findCos(@RequestParam(value = "name", required = false) String name, Model mol){
        List<KhachHang> KhachHangs;
        KhachHangs = serKhachHang.findByKhachHang(name);// neu bị trùng tên là vẫn hiện lên table
        mol.addAttribute("cusLst",KhachHangs);
        return "/KhachHang/khachhang/index";
    }


    @GetMapping("/new")
    public String add(Model mol){
        mol.addAttribute("KhachHang", new KhachHang());
        return "/KhachHang/dangky";
    }

    @PostMapping("/save")
    public String createKhachHang(@Valid @ModelAttribute KhachHang KhachHang, BindingResult result,Model mol){
        KhachHang.setNgayTao(LocalDate.now());
        KhachHang.setNgaySua(LocalDate.now());
        serKhachHang.save(KhachHang);
        return "redirect:home";
    }

    @GetMapping("/delete/{id}")
    public String deleteKhachHang(@PathVariable("id") Long id,Model mol){
        KhachHang KhachHang = serKhachHang.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID ko ton tai:" + id));
        serKhachHang.deleteById(id);
        return "redirect:/TTAP/KhachHang/employee";
    }

    @GetMapping("/detail/{id}")
    public String showDetail(@PathVariable("id") Long id,Model mol){
        KhachHang KhachHang = serKhachHang.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID ko ton tai:"+ id));
        mol.addAttribute("KhachHang",KhachHang);
        mol.addAttribute("diaChi",KhachHang.getDc() != null ? KhachHang.getDc() : new DiaChi());
        return "/KhachHang/nhanvien/update";
    }

    @PostMapping("/update")
    public String updateKhachHang(@Valid KhachHang KhachHang,BindingResult result, Model mol){
        if (result.hasErrors()){
            KhachHang.setId(KhachHang.getId());
            return "/KhachHang/update";
        }
        KhachHang existingKhachHang = serKhachHang.findById(KhachHang.getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid KhachHang Id:" + KhachHang.getId()));
        KhachHang.setNgayTao(existingKhachHang.getNgayTao());
        KhachHang.setNgaySua(LocalDate.now());
        serKhachHang.save(KhachHang);
        return "redirect:/TTAP/KhachHang/employee";
    }

    @PostMapping("/{id}/DiaChi")
    public String getupdateDiachi(@PathVariable("id") Long id, @ModelAttribute DiaChi diaChi,Model mol){
        serKhachHang.updateDiachi(id,diaChi);
        return "redirect:/TTAP/KhachHang/detail/"+ id;
    }

    @PostMapping("/register")
    public String registerKhachHang(@Valid @ModelAttribute KhachHang KhachHang,BindingResult result,Model mol) {
        KhachHang.setNgayTao(LocalDate.now());
        KhachHang.setNgaySua(LocalDate.now());
        serKhachHang.createKhachHang(KhachHang);
        mol.addAttribute("message", "Chi tiết tài khoản đã đc gửi.");
        return "redirect:/TTAP/KhachHang/home";
    }

//    @PostMapping(value = "create")
//    public ResponseEntity<String> create(
//            @Valid @ModelAttribute KhachHang KhachHang, BindingResult result, Model mol
//    ) {
//        if (result.hasErrors()) {
//            mol.addAttribute("cv", serCv.findAll());
//            return ResponseEntity.badRequest().body("Validation errors occurred");
//        }
//        KhachHang.setNgayTao(LocalDate.now());
//        KhachHang.setNgaySua(LocalDate.now());
//        serKhachHang.registerKhachHang(KhachHang);
//        return ResponseEntity.ok().body();
//    }

    @PostMapping("/create")
    public String registerKhachHang(
            @Valid @ModelAttribute("KhachHang") KhachHang KhachHang, BindingResult result,
            Model model, RedirectAttributes redirectAttributes
    ) {
        KhachHang.setNgayTao(LocalDate.now());
        KhachHang.setNgaySua(LocalDate.now());
        serKhachHang.registerKhachHang(KhachHang);

        return "redirect:/TTAP/KhachHang/home";
    }

//    @PostMapping("/createNv")
//    public String registerNv(
//            @Valid @ModelAttribute("KhachHang") KhachHang KhachHang, BindingResult result,
//            Model model, RedirectAttributes redirectAttributes
//    ) {
//        if (result.hasErrors()) {
//            model.addAttribute("cvList",serCv.findAll());
//            return "KhachHang/register"; // Trả về lại form nếu có lỗi
//        }
//
//        KhachHang.setNgayTao(LocalDate.now());
//        KhachHang.setNgaySua(LocalDate.now());
//        serKhachHang.registerKhachHang(KhachHang);
//
//        return "redirect:/TTAP/KhachHang/employee";
//    }
//
//    @PostMapping("/createKh")
//    public String registerKh(
//            @Valid @ModelAttribute("KhachHang") KhachHang KhachHang, BindingResult result,
//            Model model, RedirectAttributes redirectAttributes
//    ) {
//        if (result.hasErrors()) {
//            model.addAttribute("cvList",serCv.findAll());
//            return "KhachHang/register"; // Trả về lại form nếu có lỗi
//        }
//
//        KhachHang.setNgayTao(LocalDate.now());
//        KhachHang.setNgaySua(LocalDate.now());
//        serKhachHang.registerKhachHang(KhachHang);
//
//        return "redirect:/TTAP/KhachHang/customer";
//    }

}
