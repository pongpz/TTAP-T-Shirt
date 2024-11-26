package com.project.ttaptshirt.controller.adminController;

import com.project.ttaptshirt.dto.NumberUtils;
import com.project.ttaptshirt.repository.HoaDonChiTietRepository;
import com.project.ttaptshirt.repository.HoaDonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HoaDonChiTietController {

    @Autowired
    HoaDonChiTietRepository hdctr;

    @Autowired
    HoaDonRepository hdr;

    @GetMapping("/admin/hoa-don-chi-tiet/hien-thi")
    public String hienThi(@RequestParam Long id, Model model, @RequestParam(defaultValue = "0") Integer page){
        Pageable pageab = PageRequest.of(page, 5);
        NumberUtils numberUtils = new NumberUtils();
        model.addAttribute("numberUtils",numberUtils);
        model.addAttribute("listHDCT",hdctr.getHoaDonChiTietByHoaDonId(id));
        model.addAttribute("listHD", hdr.getAllHD(pageab));
        model.addAttribute("page",page);
        return "admin/hoadon/hoa-don";
    }
}
