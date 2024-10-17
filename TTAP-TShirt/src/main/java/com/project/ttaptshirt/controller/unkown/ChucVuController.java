//package com.project.ttaptshirt.controller;
//
//
//import com.project.ttaptshirt.entity.Role;
//import com.project.ttaptshirt.service.ChucVuService;
//import jakarta.validation.Valid;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import java.util.List;
//
//@Controller
//@RequestMapping("/TTAP/Admin/ChucVu/")
//public class ChucVuController {
//
//    @Autowired
//    private ChucVuService serCv;
//
//    @GetMapping("home")
//    public String home(Model mol){
//        List<Role> cvList = serCv.findAll();
//        mol.addAttribute("cvLst",cvList);
//        return"/admin/chucvu/index";
//    }
//
//    @GetMapping("new")
//    public String add(Model mol){
//        mol.addAttribute("chucvu", new Role());
//        return "/admin/chucvu/dangky";
//    }
//
//    @PostMapping("save")
//    public String createUser(@Valid @ModelAttribute Role cv, BindingResult result, Model mol) {
//        if (result.hasErrors()) {
//            return "/admin/chucvu/dangky";
//        }
//        serCv.save(cv);
//        return "redirect:/TTAP/User/new";
//    }
//
//    @GetMapping("delete/{id}")
//    public String deleteUser(@PathVariable("id") Long id, Model mol){
//        Role cv = serCv.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("ID ko ton tai:" + id));
//        serCv.deleteById(id);
//        return "redirect:/TTAP/Admin/ChucVu/home";
//    }
//
//    @GetMapping("detail/{id}")
//    public String showDetail(@PathVariable("id") Long id,Model mol){
//        Role cv = serCv.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("ID ko ton tai:"+ id));
//        mol.addAttribute("chucvu",cv);
//        return "/admin/chucvu/update";
//    }
//
//    @PostMapping("update")
//    public String updateUser(@Valid Role cv, BindingResult result, Model mol){
//        if (result.hasErrors()){
//          cv.setId(cv.getId());
//            return "/admin/chucvu/update";
//        }
//        serCv.save(cv);
//        return "redirect:/TTAP/Admin/ChucVu/home";
//    }
//
//}
