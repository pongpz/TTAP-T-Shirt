package com.project.ttaptshirt.controller;


import com.project.ttaptshirt.entity.DiaChi;
import com.project.ttaptshirt.service.DiaChiService;
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

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/TTAP/User/DiaChi/")
public class DiaChiController {

    @Autowired
    private DiaChiService serDc;

    @GetMapping("home")
    public String home(Model mol){
        List<DiaChi> dcList = serDc.findAll();
        mol.addAttribute("dcLst",dcList);
        return"/user/diachi/index";
    }

    @GetMapping("new")
    public String add(Model mol){
        mol.addAttribute("diachi", new DiaChi());
        return "/user/diachi/dangky";
    }

    @PostMapping("save")
    public String createUser(@Valid @ModelAttribute DiaChi dc, BindingResult result, Model mol) {
        if (result.hasErrors()) {
            return "/user/diachi/dangky";
        }
        serDc.save(dc);
        return "redirect:home";
    }

    @GetMapping("delete/{id}")
    public String deleteUser(@PathVariable("id") Long id, Model mol){
        DiaChi dc = serDc.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID ko ton tai:" + id));
        serDc.deleteById(id);
        return "redirect:/TTAP/User/DiaChi/home";
    }

    @GetMapping("detail/{id}")
    public String showDetail(@PathVariable("id") Long id,Model mol){
        DiaChi dc = serDc.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID ko ton tai:"+ id));
        mol.addAttribute("diachi",dc);
        return "/user/diachi/update";
    }

    @PostMapping("update")
    public String updateUser(@Valid DiaChi dc,BindingResult result, Model mol){
        if (result.hasErrors()){
            dc.setId(dc.getId());
            return "/user/diachi/update";
        }
        DiaChi existDc = serDc.findById(dc.getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + dc.getId()));
        serDc.save(dc);
        return "redirect:/TTAP/User/DiaChi/home";
    }

}
