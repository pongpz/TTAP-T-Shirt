//package com.project.ttaptshirt.controller;
//
//import com.project.ttaptshirt.entity.HinhAnh;
//import com.project.ttaptshirt.entity.User;
//import com.project.ttaptshirt.service.HinhAnhService;
//import com.project.ttaptshirt.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api")
//public class apicController {
//
//    @Autowired
//    private HinhAnhService serImage;
//
//    @Autowired
//    private UserService service;
//
//    @GetMapping("/image")
//    private List<HinhAnh> api(){
//        return serImage.findAll();
//    }
//
//    @GetMapping("/user")
//    private List<User> apiUser(){
//        return service.findAll();
//    }
//
//}
