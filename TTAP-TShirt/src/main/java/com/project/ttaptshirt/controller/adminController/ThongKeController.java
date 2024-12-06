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
    public String getThongKe(Model model,
                             @RequestParam(name = "month", defaultValue = "#{T(java.time.LocalDate).now().monthValue}") int month,
                             @RequestParam(name = "year", defaultValue = "#{T(java.time.LocalDate).now().year}") int year) {

        // Tổng thu nhập hôm nay
        double tongTienHomNay = thongKeService.tongTienHomNay();
        model.addAttribute("tongTienHomNay", tongTienHomNay);

        // Thu nhập theo tháng
        double tienTheoThang = thongKeService.tinhThuNhapTheoThang(month, year);
        model.addAttribute("tienTheoThang", tienTheoThang);

        // Thu nhập theo năm
        double tienTheoNam = thongKeService.tinhThuNhapTheoNam(year);
        model.addAttribute("tienTheoNam", tienTheoNam);

        // Thêm NumberUtils vào model
        NumberUtils numberUtils = new NumberUtils();
        model.addAttribute("numberUtils", numberUtils);

        // Lấy tất cả hóa đơn
        List<HoaDon> hoaDons = hoaDonRepository.findAll();

        // Tạo Map để nhóm doanh thu theo tháng
        Map<Integer, Double> doanhThuTheoThang = new HashMap<>();

        // Lặp qua từng hóa đơn
        for (HoaDon hoaDon : hoaDons) {
            // Kiểm tra ngày thanh toán
            if (hoaDon.getNgayThanhToan() != null) {
                int monthValue = hoaDon.getNgayThanhToan().getMonthValue(); // Lấy tháng (1-12)

                // Lấy chi tiết hóa đơn
                List<HoaDonChiTiet> hoaDonChiTiets = hoaDonChiTietRepository.findByHoaDon(hoaDon);

                // Tính tổng doanh thu của hóa đơn
                double doanhThu = hoaDonChiTiets.stream()
                        .filter(chiTiet -> chiTiet.getDonGia() != null && chiTiet.getSoLuong() != null) // Kiểm tra null
                        .mapToDouble(chiTiet -> chiTiet.getDonGia() * chiTiet.getSoLuong())
                        .sum();

                // Cộng doanh thu vào Map
                doanhThuTheoThang.put(monthValue, doanhThuTheoThang.getOrDefault(monthValue, 0.0) + doanhThu);
            }
        }

        // Tách keys và values từ Map và sắp xếp theo tháng
        List<Integer> months = new ArrayList<>(doanhThuTheoThang.keySet());
        List<Double> revenues = new ArrayList<>(doanhThuTheoThang.values());

        // Sắp xếp theo thứ tự tháng
        months.sort(Comparator.naturalOrder()); // Sắp xếp tháng từ 1 đến 12

        // Sắp xếp revenues theo thứ tự tháng
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



        // Trả về view
        return "/admin/thongke/thong-ke";
    }




}