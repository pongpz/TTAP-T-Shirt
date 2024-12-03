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
                totalIncome += chiTiet.getDonGia() * chiTiet.getSoLuong();  // Cộng tổng thu nhập
            }
        }

        return totalIncome;
    }

    public Double tinhThuNhapTheoThang(int month, int year) {
        // Lấy ngày hiện tại
        LocalDate today = LocalDate.now();  // Lấy ngày hiện tại

        // Nếu bạn chỉ cần lấy tháng và năm hiện tại
        int currentMonth = today.getMonthValue();  // Lấy tháng hiện tại (1-12)
        int currentYear = today.getYear();        // Lấy năm hiện tại

        // Nếu tháng và năm truyền vào không khớp với tháng và năm hiện tại, bạn có thể xử lý
        if (month != currentMonth || year != currentYear) {
            throw new IllegalArgumentException("Tháng và năm không khớp với tháng và năm hiện tại.");
        }

        // Lấy tất cả hóa đơn có ngày thanh toán trong tháng và năm đã chọn
        List<HoaDon> hoaDons = hoaDonRepository.findHoaDonsByMonthAndYear(month, year);

        double totalIncome = 0;

        // Duyệt qua từng hóa đơn để tính tổng thu nhập
        for (HoaDon hoaDon : hoaDons) {
            // Lấy tất cả các chi tiết hóa đơn của mỗi hóa đơn
            List<HoaDonChiTiet> hoaDonChiTiets = hoaDonChiTietRepository.findByHoaDon(hoaDon);

            // Tính tổng thu nhập từ các chi tiết hóa đơn
            for (HoaDonChiTiet chiTiet : hoaDonChiTiets) {
                totalIncome += chiTiet.getDonGia() * chiTiet.getSoLuong();  // Cộng tổng thu nhập
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
                totalIncome += chiTiet.getDonGia() * chiTiet.getSoLuong();
            }
        }

        return totalIncome;
    }


    public List<Double> tinhThuNhapTheoNam1(int year) {
        List<Double> thuNhapThang = new ArrayList<>(Collections.nCopies(12, 0.0));  // Khởi tạo mảng thu nhập cho 12 tháng

        List<HoaDon> hoaDons = hoaDonRepository.findHoaDonsByYear(year);  // Tìm các hóa đơn trong năm

        for (HoaDon hoaDon : hoaDons) {
            List<HoaDonChiTiet> hoaDonChiTiets = hoaDonChiTietRepository.findByHoaDon(hoaDon);

            // Duyệt qua từng chi tiết hóa đơn
            for (HoaDonChiTiet chiTiet : hoaDonChiTiets) {
                int month = hoaDon.getNgayThanhToan().getMonthValue();  // Lấy tháng từ ngày thanh toán

                // Cộng thu nhập vào đúng tháng
                thuNhapThang.set(month - 1, thuNhapThang.get(month - 1) + chiTiet.getDonGia() * chiTiet.getSoLuong());
            }
        }

        return thuNhapThang;  // Trả về danh sách thu nhập cho từng tháng
    }

    public List<Object[]> thongKeDoanhThuTheoThangVaNam() {
        return hoaDonRepository.thongKeDoanhThuTheoThangVaNam();
    }


}
