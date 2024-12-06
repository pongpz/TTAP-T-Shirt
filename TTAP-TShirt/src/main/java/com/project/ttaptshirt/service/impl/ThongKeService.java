package com.project.ttaptshirt.service.impl;

import com.project.ttaptshirt.entity.HoaDon;
import com.project.ttaptshirt.entity.HoaDonChiTiet;
import com.project.ttaptshirt.repository.HoaDonChiTietRepository;
import com.project.ttaptshirt.repository.HoaDonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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


}
