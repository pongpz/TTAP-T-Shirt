package com.project.ttaptshirt.service.impl;

import com.project.ttaptshirt.entity.HoaDon;
import com.project.ttaptshirt.entity.HoaDonChiTiet;
import com.project.ttaptshirt.repository.HoaDonChiTietRepository;
import com.project.ttaptshirt.repository.HoaDonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ThongKeService {

    @Autowired
    private HoaDonChiTietRepository hoaDonChiTietRepository;

    public Double getTotalRevenue() {
        return hoaDonChiTietRepository.getTotalRevenue();
    }

    public List<Object[]> getBestSellingProducts() {
        return hoaDonChiTietRepository.getBestSellingProducts();
    }

    public Integer getTotalQuantitySold() {
        return hoaDonChiTietRepository.getTotalQuantitySold();
    }

    public List<Object[]> getMonthlyRevenue() {
        return hoaDonChiTietRepository.getMonthlyRevenue();
    }

    @Autowired
    private HoaDonRepository hoaDonRepository;


    public Double tongTienHomNay() {
        LocalDate today = LocalDate.now();

        // Lấy tất cả hóa đơn có ngày thanh toán là hôm nay
        List<HoaDon> hoaDons = hoaDonRepository.findHoaDonsByNgayThanhToan(today);

        double totalIncome = 0;

        // Duyệt qua từng hóa đơn để tính tổng thu nhập
        for (HoaDon hoaDon : hoaDons) {
            // Lấy tất cả các chi tiết hóa đơn của mỗi hóa đơn
            List<HoaDonChiTiet> hoaDonChiTiets = hoaDonChiTietRepository.findByHoaDon(hoaDon);

            // Tính tổng thu nhập từ các chi tiết hóa đơn
            for (HoaDonChiTiet chiTiet : hoaDonChiTiets) {
                Float donGia = chiTiet.getDonGia();
                Integer soLuong = chiTiet.getSoLuong();
                if (donGia != null && soLuong != null) {
                    totalIncome += donGia * soLuong;  // Cộng tổng thu nhập
                }
            }
        }

        return totalIncome;
    }

    public Double tinhThuNhapTheoThang(int month, int year) {
        // Lấy tất cả hóa đơn có ngày thanh toán trong tháng và năm đã chọn
        List<HoaDon> hoaDons = hoaDonRepository.findHoaDonsByMonthAndYear(month, year);

        double totalIncome = 0;

        // Duyệt qua từng hóa đơn để tính tổng thu nhập
        for (HoaDon hoaDon : hoaDons) {
            // Lấy tất cả các chi tiết hóa đơn của mỗi hóa đơn
            List<HoaDonChiTiet> hoaDonChiTiets = hoaDonChiTietRepository.findByHoaDon(hoaDon);

            // Tính tổng thu nhập từ các chi tiết hóa đơn
            for (HoaDonChiTiet chiTiet : hoaDonChiTiets) {
                Float donGia = chiTiet.getDonGia();
                Integer soLuong = chiTiet.getSoLuong();
                if (donGia != null && soLuong != null) {
                    totalIncome += donGia * soLuong;  // Cộng tổng thu nhập
                }
            }
        }

        return totalIncome;
    }

    public Double tinhThuNhapTheoNam(int year) {
        List<HoaDon> hoaDons = hoaDonRepository.findHoaDonsByYear(year);

        double totalIncome = 0.0;

        for (HoaDon hoaDon : hoaDons) {
            List<HoaDonChiTiet> hoaDonChiTiets = hoaDonChiTietRepository.findByHoaDon(hoaDon);

            for (HoaDonChiTiet chiTiet : hoaDonChiTiets) {
                Float donGia = chiTiet.getDonGia();
                Integer soLuong = chiTiet.getSoLuong();
                if (donGia != null && soLuong != null) {
                    totalIncome += donGia * soLuong;  // Cộng tổng thu nhập
                }
            }
        }

        return totalIncome;
    }

    public Map<LocalDate, Long> thongKeSoHoaDonTheoNgay() {
        List<Object[]> results = hoaDonRepository.countHoaDonTheoNgay();
        LocalDate today = LocalDate.now();
        return results.stream()
                .filter(result -> ((LocalDate) result[0]).equals(today))  // Lọc chỉ lấy hóa đơn của ngày hôm nay
                .collect(Collectors.toMap(
                        result -> (LocalDate) result[0], // ngày
                        result -> (Long) result[1]      // số lượng
                ));
    }



    public Map<String, Double> thongKeDoanhThuTheoLoaiDon() {
        Map<String, Double> doanhThu = new HashMap<>();
        doanhThu.put("Online", hoaDonRepository.tinhTongDoanhThuTheoLoaiDon(0));
        doanhThu.put("Offline", hoaDonRepository.tinhTongDoanhThuTheoLoaiDon(1));
        return doanhThu;
    }


    public Map<LocalDate, Double> thongKeDoanhThuTheoNgay(int day, int month, int year) {
        LocalDate selectedDate = LocalDate.of(year, month, day);

        List<HoaDon> hoaDons = hoaDonRepository.findAll();
        Map<LocalDate, Double> doanhThuTheoNgay = new HashMap<>();

        for (HoaDon hoaDon : hoaDons) {
            if (hoaDon.getNgayThanhToan() != null && hoaDon.getNgayThanhToan().isEqual(selectedDate)) {
                // Lấy chi tiết hóa đơn
                List<HoaDonChiTiet> hoaDonChiTiets = hoaDonChiTietRepository.findByHoaDon(hoaDon);

                // Tính tổng doanh thu của hóa đơn
                double doanhThu = hoaDonChiTiets.stream()
                        .filter(chiTiet -> chiTiet.getDonGia() != null && chiTiet.getSoLuong() != null) // Kiểm tra null
                        .mapToDouble(chiTiet -> chiTiet.getDonGia() * chiTiet.getSoLuong())
                        .sum();

                doanhThuTheoNgay.put(selectedDate, doanhThu);
            }
        }

        return doanhThuTheoNgay;
    }
    public Map<Integer, Double> thongKeDoanhThuTheoThang(List<HoaDon> hoaDons) {
        Map<Integer, Double> doanhThuTheoThang = new HashMap<>();
        for (HoaDon hoaDon : hoaDons) {
            if (hoaDon.getNgayThanhToan() != null) {
                int monthValue = hoaDon.getNgayThanhToan().getMonthValue();
                List<HoaDonChiTiet> hoaDonChiTiets = hoaDonChiTietRepository.findByHoaDon(hoaDon);
                double doanhThu = hoaDonChiTiets.stream()
                        .filter(chiTiet -> chiTiet.getDonGia() != null && chiTiet.getSoLuong() != null)
                        .mapToDouble(chiTiet -> chiTiet.getDonGia() * chiTiet.getSoLuong())
                        .sum();
                doanhThuTheoThang.put(monthValue, doanhThuTheoThang.getOrDefault(monthValue, 0.0) + doanhThu);
            }
        }
        return doanhThuTheoThang;
    }


}
