package com.project.ttaptshirt.controller.adminController;

import com.project.ttaptshirt.entity.HoaDon;
import com.project.ttaptshirt.repository.HoaDonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin/hoa-don")
public class HoaDonController {

    @Autowired
    HoaDonRepository hr;

    @GetMapping("/hien-thi")
    public String hienThi(Model model, @RequestParam(defaultValue = "0") Integer page) {
        Pageable pageab = PageRequest.of(page, 5);
        model.addAttribute("listHD", hr.getAllHD(pageab));
        model.addAttribute("page", page);
        if (hr.getAllHD(pageab).size() == 0) {
            model.addAttribute("nullhd", "Không có hóa đơn nào");
        }
        return "admin/hoadon/hoa-don";
    }

    @GetMapping("/tim-kiem")
    public String timKiem(@RequestParam(required = false, value = "ma") String ma,
                          @RequestParam(required = false, value = "tennv") String tennv,
                          @RequestParam(required = false, value = "tenkh") String tenkh,
                          @RequestParam(required = false, value = "sdt") String sdt,
                          @RequestParam(required = false, value = "trangThai") Integer trangThai,
                          @RequestParam(value = "ngayThanhToan", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate ngayThanhToan,
                          @RequestParam(defaultValue = "0") Integer page,
                          Model model) {
        Pageable pageab = PageRequest.of(page, 5);
//        List<HoaDon> lsSearch = hr.search("", "", "", "", null, null, pageab);
        List<HoaDon> lsSearch = hr.search2(ma,trangThai,ngayThanhToan,pageab);
        model.addAttribute("listHD", lsSearch);
        model.addAttribute("ma", ma);
        model.addAttribute("tennv", tennv);
        model.addAttribute("tenkh", tenkh);
        model.addAttribute("sdt", sdt);
        model.addAttribute("ngayThanhToan", ngayThanhToan);
        model.addAttribute("trangThai", trangThai);
        model.addAttribute("page", page);
        if (lsSearch.size() == 0) {
            model.addAttribute("nullhd", "Không có hóa đơn nào");
        }
        return "admin/hoadon/hoa-don-tim-kiem";
    }
}
