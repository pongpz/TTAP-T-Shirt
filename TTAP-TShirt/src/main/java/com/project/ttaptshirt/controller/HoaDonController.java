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

    @GetMapping("/admin/hoa-don/tim-kiem")
    public String timKiem(@RequestParam("keyword") String keyword,
                          @RequestParam("trangThai") Integer trangThai,
                          @RequestParam("loaiDon") Integer loaiDon,
                          @RequestParam(value="ngayThanhToan", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayThanhToan,
                          Model model){
        List<HoaDon> ls = new ArrayList<>();
        if(keyword.trim().isEmpty() && trangThai == 0 && loaiDon == 0 && ngayThanhToan == null){
            ls = hr.findAll();
        }else if(!keyword.trim().isEmpty() && trangThai == 0 && loaiDon == 0 && ngayThanhToan == null){
            ls = hr.searchKeyword(keyword);
        }else if(keyword.trim().isEmpty() && trangThai != 0 && loaiDon == 0 && ngayThanhToan == null){
            ls = hr.searchTrangThai(trangThai);
        }else if(keyword.trim().isEmpty() && trangThai == 0 && loaiDon != 0 && ngayThanhToan == null){
            ls = hr.searchLoaiDon(loaiDon);
        }else if(keyword.trim().isEmpty() && trangThai == 0 && loaiDon == 0 && ngayThanhToan != null){
            ls = hr.searchNgayThanhToan(ngayThanhToan);
        }else if(!keyword.trim().isEmpty() && trangThai != 0 && loaiDon == 0 && ngayThanhToan == null){
            ls = hr.searchKeywordVaTrangThai(keyword,trangThai);
        }else if(!keyword.trim().isEmpty() && trangThai == 0 && loaiDon != 0 && ngayThanhToan == null){
            ls = hr.searchKeywordVaLoaiDon(keyword, loaiDon);
        }else if(!keyword.trim().isEmpty() && trangThai == 0 && loaiDon == 0 && ngayThanhToan != null){
            ls = hr.searchKeywordVaNgayThanhToan(keyword, ngayThanhToan);
        }else if(!keyword.trim().isEmpty() && trangThai != 0 && loaiDon != 0 && ngayThanhToan == null){
            ls = hr.searchKeywordTrangThaiLoaiDon(keyword, trangThai,loaiDon);
        }else if(!keyword.trim().isEmpty() && trangThai == 0 && loaiDon != 0 && ngayThanhToan != null){
            ls = hr.searchKeywordLoaiHDNgayThanhToan(keyword, loaiDon, ngayThanhToan);
        }else if(!keyword.trim().isEmpty() && trangThai != 0 && loaiDon == 0 && ngayThanhToan != null){
            ls = hr.searchKeywordTrangThaiNgayThanhToan(keyword, loaiDon, ngayThanhToan);
        }else if(keyword.trim().isEmpty() && trangThai != 0 && loaiDon != 0 && ngayThanhToan != null){
            ls = hr.searchLoaiDonTrangThaiNgayThanhToan(loaiDon,trangThai,ngayThanhToan);
        }else if(keyword.trim().isEmpty() && trangThai == 0 && loaiDon != 0 && ngayThanhToan != null){
            ls = hr.searchLoaiDonNgayThanhToan(loaiDon,ngayThanhToan);
        }else {
            System.out.println("Có lỗi sảy ra");
        }
        model.addAttribute("listHD",ls);
        return"admin/hoadon/hoa-don";
    }

}
