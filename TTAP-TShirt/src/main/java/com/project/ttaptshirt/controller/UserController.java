package com.project.ttaptshirt.controller;


import com.project.ttaptshirt.entity.User;
import com.project.ttaptshirt.service.ChucVuService;
import com.project.ttaptshirt.service.UserService;
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
@RequestMapping("/TTAP/User/")
public class UserController {

    @Autowired
    private UserService serUser;

    @Autowired
    private ChucVuService serCv;



    @GetMapping("home")
    public String home(Model mol){
        List<User> userList = serUser.findAll();
        mol.addAttribute("nvLst",userList);
        return"/user/index";
    }

    @GetMapping("new")
    public String add(Model mol){
        mol.addAttribute("user", new User());
        mol.addAttribute("cv",serCv.findAll());
        return "/user/dangky";
    }

    @PostMapping("save")
    public String createUser(@Valid @ModelAttribute User user, BindingResult result,Model mol){
        if (result.hasErrors()){
            mol.addAttribute("cv",serCv.findAll());
            return "/user/dangky";
        }
        user.setNgayTao(LocalDate.now());
        user.setNgaySua(LocalDate.now());
        serUser.save(user);
        return "redirect:home";
    }

    @GetMapping("delete/{id}")
    public String deleteUser(@PathVariable("id") Long id,Model mol){
        User user = serUser.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID ko ton tai:" + id));
        serUser.deleteById(id);
        return "redirect:/TTAP/User/home";
    }

    @GetMapping("detail/{id}")
    public String showDetail(@PathVariable("id") Long id,Model mol){
        User user = serUser.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID ko ton tai:"+ id));
        mol.addAttribute("user",user);
        mol.addAttribute("cv",serCv.findAll());
        return "/user/update";
    }

    @PostMapping("update")
    public String updateUser(@Valid User user,BindingResult result, Model mol){
        if (result.hasErrors()){
            user.setId(user.getId());
            return "/user/update";
        }
        User existingUser = serUser.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + user.getId()));
        user.setNgayTao(existingUser.getNgayTao());
        user.setNgaySua(LocalDate.now());
        serUser.save(user);
        return "redirect:/TTAP/User/home";
    }



}
