package com.project.ttaptshirt.controller.adminController;

import com.project.ttaptshirt.dto.NumberUtils;
import com.project.ttaptshirt.entity.HoaDon;
import com.project.ttaptshirt.entity.HoaDonChiTiet;
import com.project.ttaptshirt.repository.HoaDonChiTietRepository;
import com.project.ttaptshirt.repository.HoaDonRepository;
import com.project.ttaptshirt.service.impl.ThongKeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.format.TextStyle;
import java.util.*;

@Controller
@RequestMapping("/admin/thong-ke")
public class ThongKeController {

    @Autowired
    private ThongKeService thongKeService;
    @Autowired
    private HoaDonRepository hoaDonRepository;
    @Autowired
    private HoaDonChiTietRepository hoaDonChiTietRepository;

    @GetMapping
    public String getThongKe(Model model,@RequestParam(name = "month", defaultValue = "#{T(java.time.LocalDate).now().monthValue}") int month,
                             @RequestParam(name = "year", defaultValue = "#{T(java.time.LocalDate).now().year}") int year) {

        double tongTienHomNay;
        tongTienHomNay = thongKeService.tongTienHomNay();
        model.addAttribute("tongTienHomNay", tongTienHomNay);

        double tienTheoThang = thongKeService.tinhThuNhapTheoThang(month, year);
        model.addAttribute("tienTheoThang", tienTheoThang);

        Double tienTheoNam = thongKeService.tinhThuNhapTheoNam(year);
        model.addAttribute("tienTheoNam", tienTheoNam);

        NumberUtils numberUtils = new NumberUtils();

        model.addAttribute("numberUtils", numberUtils);


        return "/admin/thongke/thong-ke";
    }

    @GetMapping("/bao-cao-doanh-thu")
    public String getDoanhThu(Model model) {
        // Lấy tất cả hóa đơn
        List<HoaDon> hoaDons = hoaDonRepository.findAll();

        // Tạo Map để chứa doanh thu theo tháng
        Map<String, Double> doanhThuTheoThang = new HashMap<>();

        // Lặp qua các hóa đơn để tính doanh thu và nhóm theo tháng
        for (HoaDon hoaDon : hoaDons) {
            // Lấy tháng từ ngày thanh toán
            String thang = hoaDon.getNgayThanhToan() != null
                    ? hoaDon.getNgayThanhToan().getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault())
                    : "Chưa thanh toán";

            // Lấy chi tiết hóa đơn
            List<HoaDonChiTiet> hoaDonChiTiets = hoaDonChiTietRepository.findByHoaDon(hoaDon);

            // Tính doanh thu cho hóa đơn này
            double doanhThu = hoaDonChiTiets.stream()
                    .mapToDouble(chiTiet -> chiTiet.getDonGia() * chiTiet.getSoLuong()) // Tính doanh thu từng chi tiết hóa đơn
                    .sum();

            // Cộng doanh thu vào map theo tháng
            doanhThuTheoThang.put(thang, doanhThuTheoThang.getOrDefault(thang, 0.0) + doanhThu);
        }

        // Tách keys và values từ Map
        List<String> months = new ArrayList<>(doanhThuTheoThang.keySet());
        List<Double> revenues = new ArrayList<>(doanhThuTheoThang.values());

        // Truyền danh sách keys và values vào model
        model.addAttribute("months", months);
        model.addAttribute("revenues", revenues);

        return "admin/thongke/thong-ke"; // Tên view của bạn
    }


}