package com.project.ttaptshirt.controller;

import com.project.ttaptshirt.entity.HoaDon;
import com.project.ttaptshirt.repository.HoaDonChiTietRepository;
import com.project.ttaptshirt.repository.HoaDonRepository;
import org.aspectj.apache.bcel.classfile.InnerClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class HoaDonController {

    @Autowired
    HoaDonRepository hr;

    @GetMapping("/admin/hoa-don/hien-thi")
    public String hienThi(Model model){
        model.addAttribute("listHD",hr.findAll());
        return"admin/hoadon/hoa-don";
    }

//    @GetMapping("/admin/hoa-don/tim-kiem")
//    public String timKiem(@RequestParam(required = false, value="keyword") String keyword,
//                          @RequestParam(required = false, value="trangThai") Integer trangThai,
//                          @RequestParam(required = false, value="loaiDon") Integer loaiDon,
//                          @RequestParam(required = false, value="ngayThanhToan") @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayThanhToan,
//                          Model model){
//        List<HoaDon> ls = new ArrayList<>();
//        if(keyword.trim().isEmpty() && trangThai == null && loaiDon == null && ngayThanhToan == null){
//            model.addAttribute("listHD",hr.findAll());
//        }else{
//            model.addAttribute("listHD",hr.search(keyword,trangThai,loaiDon,ngayThanhToan));
//        }
//        return"admin/hoadon/hoa-don";
//    }

    @GetMapping("/admin/hoa-don/tim-kiem")
    public String timKiem(@RequestParam(required = false, value="keyword") String keyword,
                          @RequestParam(required = false, value="trangThai") Integer trangThai,
                          @RequestParam(required = false, value="loaiDon") Integer loaiDon,
                          @RequestParam(value="ngayThanhToan", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayThanhToan,
                          Model model){
        model.addAttribute("listHD",hr.search(keyword, trangThai, loaiDon, ngayThanhToan));
        return"admin/hoadon/hoa-don";
    }


}
