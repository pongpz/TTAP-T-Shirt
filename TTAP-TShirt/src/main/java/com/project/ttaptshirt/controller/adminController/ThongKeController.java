package com.project.ttaptshirt.controller.adminController;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.time.LocalDate;
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

    @Autowired
    public ThongKeController(ThongKeService thongKeService, HoaDonRepository hoaDonRepository, HoaDonChiTietRepository hoaDonChiTietRepository) {
        this.thongKeService = thongKeService;
        this.hoaDonRepository = hoaDonRepository;
        this.hoaDonChiTietRepository = hoaDonChiTietRepository;
    }

    @GetMapping
    public String getThongKe(Model model,
                             @RequestParam(name = "month", defaultValue = "#{T(java.time.LocalDate).now().monthValue}") Integer month,
                             @RequestParam(name = "year", defaultValue = "#{T(java.time.LocalDate).now().year}") Integer year,
                             @RequestParam(name = "day", required = false) Integer day) {

        // Tổng thu nhập hôm nay
        double tongTienHomNay = thongKeService.tongTienHomNay(day, month, year);
        model.addAttribute("tongTienHomNay", tongTienHomNay);

        // Thu nhập theo tháng
        double tienTheoThang = thongKeService.tinhThuNhapTheoThang(month, year);
        model.addAttribute("tienTheoThang", tienTheoThang);

        // Thu nhập theo năm
        double tienTheoNam = thongKeService.tinhThuNhapTheoNam(year);
        model.addAttribute("tienTheoNam", tienTheoNam);

        // Thêm NumberUtils vào model
        model.addAttribute("numberUtils", new NumberUtils());

        // Nếu ngày được chọn, tính thống kê cho ngày đó
        if (day != null) {
            // Lọc doanh thu theo ngày
            Map<LocalDate, Double> doanhThuTheoNgay = thongKeService.thongKeDoanhThuTheoNgay(day, month, year);
            model.addAttribute("doanhThuTheoNgay", doanhThuTheoNgay);
        }

        // Lấy tất cả hóa đơn và nhóm doanh thu theo tháng
        List<HoaDon> hoaDons = hoaDonRepository.findAll();
        Map<Integer, Double> doanhThuTheoThang = thongKeService.thongKeDoanhThuTheoThang(hoaDons);

        // Tách keys và values từ Map và sắp xếp theo tháng
        List<Integer> months = new ArrayList<>(doanhThuTheoThang.keySet());
        months.sort(Comparator.naturalOrder());

        List<Double> sortedRevenues = new ArrayList<>();
        for (int monthValue : months) {
            sortedRevenues.add(doanhThuTheoThang.get(monthValue));
        }

        // Chuyển đổi sang JSON
        ObjectMapper mapper = new ObjectMapper();
        try {
            model.addAttribute("months", mapper.writeValueAsString(months));
            model.addAttribute("revenues", mapper.writeValueAsString(sortedRevenues));
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("months", "[]");
            model.addAttribute("revenues", "[]");
        }

        LocalDate filterDate = (day != null) ? LocalDate.of(year, month, day) : LocalDate.now();

        // Thống kê số hóa đơn theo ngày
        Map<LocalDate, Long> tongHoaDonTheoNgay = thongKeService.thongKeSoHoaDonTheoNgay(filterDate);
        model.addAttribute("tongHoaDonTheoNgay", tongHoaDonTheoNgay);

        LocalDate ngayThanhToan = (day != null) ? LocalDate.of(year, month, day) : LocalDate.now();

        // Doanh thu theo loại đơn
        Map<String, Double> doanhThuTheoLoaiDon = thongKeService.thongKeDoanhThuTheoLoaiDon(ngayThanhToan);
        model.addAttribute("doanhThuTheoLoaiDon", doanhThuTheoLoaiDon);
        model.addAttribute("doanhThuOnline", doanhThuTheoLoaiDon.get("Online"));
        model.addAttribute("doanhThuOffline", doanhThuTheoLoaiDon.get("Offline"));

        LocalDate today = LocalDate.now();
        model.addAttribute("day", today.getDayOfMonth());
        model.addAttribute("month", today.getMonthValue());
        model.addAttribute("year", today.getYear());
        // Trả về view
        return "admin/thongke/thong-ke";
    }
}