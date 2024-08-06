package com.project.ttaptshirt.controller;

import com.project.ttaptshirt.entity.HinhAnh;
import com.project.ttaptshirt.service.HinhAnhService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/image")
public class apicController {

    @Autowired
    private HinhAnhService serImage;

    @GetMapping("/api")
    private List<HinhAnh> api(){
        return serImage.findAll();
    }

}
