package com.project.ttaptshirt.controller;


import com.project.ttaptshirt.entity.ChucVu;
import com.project.ttaptshirt.service.ChucVuService;
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

import java.util.List;

@Controller
@RequestMapping("/TTAP/ChucVu/")
public class ChucVuController {

    @Autowired
    private ChucVuService serCv;

    @GetMapping("home")
    public String home(Model mol){
        List<ChucVu> cvList = serCv.findAll();
        mol.addAttribute("cvLst",cvList);
        return"/admin/chucvu/index";
    }

    @GetMapping("new")
    public String add(Model mol){
        mol.addAttribute("chucvu", new ChucVu());
        return "/admin/chucvu/dangky";
    }

    @PostMapping("save")
    public String createUser(@Valid @ModelAttribute ChucVu cv, BindingResult result, Model mol) {
        if (result.hasErrors()) {
            return "/admin/chucvu/dangky";
        }
        serCv.save(cv);
        return "redirect:home";
    }

    @GetMapping("delete/{id}")
    public String deleteUser(@PathVariable("id") Long id, Model mol){
        ChucVu cv = serCv.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID ko ton tai:" + id));
        serCv.deleteById(id);
        return "redirect:/TTAP/ChucVu/home";
    }

    @GetMapping("detail/{id}")
    public String showDetail(@PathVariable("id") Long id,Model mol){
        ChucVu cv = serCv.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID ko ton tai:"+ id));
        mol.addAttribute("chucvu",cv);
        return "/admin/chucvu/update";
    }

    @PostMapping("update")
    public String updateUser(@Valid ChucVu cv,BindingResult result, Model mol){
        if (result.hasErrors()){
          cv.setId(cv.getId());
            return "/admin/chucvu/update";
        }
        serCv.save(cv);
        return "redirect:/TTAP/ChucVu/home";
    }

}
