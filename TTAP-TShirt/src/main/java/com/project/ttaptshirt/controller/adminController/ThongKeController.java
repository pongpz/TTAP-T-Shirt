package com.project.ttaptshirt.controller.adminController;

import com.project.ttaptshirt.dto.NumberUtils;
import com.project.ttaptshirt.service.impl.ThongKeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/admin/thong-ke")
public class ThongKeController {

    @Autowired
    private ThongKeService thongKeService;

    @GetMapping
    public String getThongKe(Model model,@RequestParam(name = "month", defaultValue = "#{T(java.time.LocalDate).now().monthValue}") int month,
                             @RequestParam(name = "year", defaultValue = "#{T(java.time.LocalDate).now().year}") int year) {

        double tongTienHomNay;
        tongTienHomNay = thongKeService.tongTienHomNay();
        model.addAttribute("tongTienHomNay", tongTienHomNay);

        double tienTheoThang = thongKeService.tinhThuNhapTheoThang(month,year);
        model.addAttribute("tienTheoThang", tienTheoThang);

        Double tienTheoNam = thongKeService.tinhThuNhapTheoNam(year);
        model.addAttribute("tienTheoNam", tienTheoNam);

        NumberUtils numberUtils = new NumberUtils();

        model.addAttribute("numberUtils", numberUtils);



        List<Double> thunhapthang1 = thongKeService.tinhThuNhapTheoNam1(year);
        List<String> thang = Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"); // Các tháng trong năm
        model.addAttribute("thuNhapThang1", thunhapthang1);
        model.addAttribute("thang", thang);  // Gửi tên các tháng
        model.addAttribute("year", year);  // Gửi năm

        return "admin/thongke/thong-ke";  // View trả về (thông qua Thymeleaf hoặc các template khác)
    }


}