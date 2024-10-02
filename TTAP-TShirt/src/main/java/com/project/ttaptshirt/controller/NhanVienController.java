package com.project.ttaptshirt.controller;


import com.project.ttaptshirt.entity.DiaChi;
import com.project.ttaptshirt.entity.NhanVien;
import com.project.ttaptshirt.service.NhanVienService;
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
@RequestMapping("/TTAP/NhanVien")
public class NhanVienController {

    @Autowired
    private NhanVienService serNhanVien;

    @GetMapping("/home")
    public String home(Model mol){
        List<NhanVien> NhanVienList = serNhanVien.findAll();
        mol.addAttribute("empoLst",NhanVienList);
        return"user/nhanvien/index";
    }

    @GetMapping("/find")
    public String findNhanVien(@RequestParam(value = "name", required = false) String name, Model mol){
        List<NhanVien> NhanViens;
        if (name == null || name.isEmpty()) {
            NhanViens = serNhanVien.findAll(); // Assuming you have a method to retrieve all NhanViens
        } else {
            NhanViens = serNhanVien.findByNhanVien(name);
        }
        mol.addAttribute("empoLst",NhanViens);
        return "/user/nhanvien/index";
    }
//
//    @GetMapping("/find")
//    public String findCos(@RequestParam(value = "name", required = false) String name, Model mol){
//        List<NhanVien> NhanViens;
//        NhanViens = serNhanVien.findByNhanVien(name);// neu bị trùng tên là vẫn hiện lên table
//        mol.addAttribute("cusLst",NhanViens);
//        return "/NhanVien/NhanVien/index";
//    }


    @GetMapping("/new")
    public String add(Model mol){
        mol.addAttribute("nhanVien", new NhanVien());
        return "user/nhanvien/dangky";
    }

    @PostMapping("/save")
    public String createNhanVien(@Valid @ModelAttribute NhanVien NhanVien, BindingResult result,Model mol){
        NhanVien.setNgayTao(LocalDate.now());
        NhanVien.setNgaySua(LocalDate.now());
        serNhanVien.save(NhanVien);
        return "redirect:home";
    }

    @GetMapping("/delete/{id}")
    public String deleteNhanVien(@PathVariable("id") Long id,Model mol){
        NhanVien NhanVien = serNhanVien.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID ko ton tai:" + id));
        serNhanVien.deleteById(id);
        return "redirect:/TTAP/NhanVien/employee";
    }

    @GetMapping("/detail/{id}")
    public String showDetail(@PathVariable("id") Long id,Model mol){
        NhanVien nhanVien = serNhanVien.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID ko ton tai:"+ id));
        mol.addAttribute("nhanVien",nhanVien);
        mol.addAttribute("diaChi",nhanVien.getDc() != null ? nhanVien.getDc() : new DiaChi());
        return "/user/nhanvien/update";
    }

    @PostMapping("/update")
    public String updateNhanVien(@Valid NhanVien NhanVien,BindingResult result, Model mol){
        if (result.hasErrors()){
            NhanVien.setId(NhanVien.getId());
            return "/NhanVien/update";
        }
        NhanVien existingNhanVien = serNhanVien.findById(NhanVien.getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid NhanVien Id:" + NhanVien.getId()));
        NhanVien.setNgayTao(existingNhanVien.getNgayTao());
        NhanVien.setNgaySua(LocalDate.now());
        serNhanVien.save(NhanVien);
        return "redirect:/TTAP/NhanVien/home";
    }

    @PostMapping("/{id}/DiaChi")
    public String getupdateDiachi(@PathVariable("id") Long id, @ModelAttribute DiaChi diaChi,Model mol){
        serNhanVien.updateDiachi(id,diaChi);
        return "redirect:/TTAP/NhanVien/detail/"+ id;
    }

    @PostMapping("/register")
    public String registerNhanVien(@Valid @ModelAttribute NhanVien NhanVien,BindingResult result,Model mol) {
        NhanVien.setNgayTao(LocalDate.now());
        NhanVien.setNgaySua(LocalDate.now());
        serNhanVien.createNhanVien(NhanVien);
        mol.addAttribute("message", "Chi tiết tài khoản đã đc gửi.");
        return "redirect:/TTAP/NhanVien/home";
    }

//    @PostMapping(value = "create")
//    public ResponseEntity<String> create(
//            @Valid @ModelAttribute NhanVien NhanVien, BindingResult result, Model mol
//    ) {
//        if (result.hasErrors()) {
//            mol.addAttribute("cv", serCv.findAll());
//            return ResponseEntity.badRequest().body("Validation errors occurred");
//        }
//        NhanVien.setNgayTao(LocalDate.now());
//        NhanVien.setNgaySua(LocalDate.now());
//        serNhanVien.registerNhanVien(NhanVien);
//        return ResponseEntity.ok().body();
//    }

    @PostMapping("/create")
    public String registerNhanVien(
            @Valid @ModelAttribute("NhanVien") NhanVien NhanVien, BindingResult result,
            Model model, RedirectAttributes redirectAttributes
    ) {
        NhanVien.setNgayTao(LocalDate.now());
        NhanVien.setNgaySua(LocalDate.now());
        serNhanVien.registerNhanVien(NhanVien);

        return "redirect:/TTAP/NhanVien/home";
    }

//    @PostMapping("/createNv")
//    public String registerNv(
//            @Valid @ModelAttribute("NhanVien") NhanVien NhanVien, BindingResult result,
//            Model model, RedirectAttributes redirectAttributes
//    ) {
//        if (result.hasErrors()) {
//            model.addAttribute("cvList",serCv.findAll());
//            return "NhanVien/register"; // Trả về lại form nếu có lỗi
//        }
//
//        NhanVien.setNgayTao(LocalDate.now());
//        NhanVien.setNgaySua(LocalDate.now());
//        serNhanVien.registerNhanVien(NhanVien);
//
//        return "redirect:/TTAP/NhanVien/employee";
//    }
//
//    @PostMapping("/createKh")
//    public String registerKh(
//            @Valid @ModelAttribute("NhanVien") NhanVien NhanVien, BindingResult result,
//            Model model, RedirectAttributes redirectAttributes
//    ) {
//        if (result.hasErrors()) {
//            model.addAttribute("cvList",serCv.findAll());
//            return "NhanVien/register"; // Trả về lại form nếu có lỗi
//        }
//
//        NhanVien.setNgayTao(LocalDate.now());
//        NhanVien.setNgaySua(LocalDate.now());
//        serNhanVien.registerNhanVien(NhanVien);
//
//        return "redirect:/TTAP/NhanVien/customer";
//    }

}
