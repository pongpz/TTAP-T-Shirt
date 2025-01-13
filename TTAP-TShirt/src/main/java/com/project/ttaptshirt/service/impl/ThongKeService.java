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


    public Double tongTienHomNay(Integer day, Integer month, Integer year) {
        // Nếu tham số ngày, tháng, năm không được truyền vào, mặc định là ngày hôm nay
        LocalDate today = (day != null && month != null && year != null) ?
                LocalDate.of(year, month, day) : LocalDate.now();

        // Lấy tất cả hóa đơn có ngày thanh toán trong khoảng ngày tháng năm đã cho
        List<HoaDon> hoaDons = hoaDonRepository.findHoaDonsByNgayThanhToan(today);

        double totalIncome = 0;

        // Duyệt qua từng hóa đơn để tính tổng thu nhập
        for (HoaDon hoaDon : hoaDons) {

            if (hoaDon.getSoTienGiamGia() == null){
                totalIncome += hoaDon.getTongTien();
            }else {
                totalIncome += hoaDon.getTongTien() - hoaDon.getSoTienGiamGia();
            }
//            // Lấy tất cả các chi tiết hóa đơn của mỗi hóa đơn
//            List<HoaDonChiTiet> hoaDonChiTiets = hoaDonChiTietRepository.findByHoaDon(hoaDon);
//
//            // Tính tổng thu nhập từ các chi tiết hóa đơn
//            for (HoaDonChiTiet chiTiet : hoaDonChiTiets) {
//                Float donGia = chiTiet.getDonGia();
//                Integer soLuong = chiTiet.getSoLuong();
//                if (donGia != null && soLuong != null) {
//                    totalIncome += donGia * soLuong;  // Cộng tổng thu nhập
//                }
//            }
        }

        return totalIncome;
    }




    public Double tinhThuNhapTheoThang(int month, int year) {
        // Lấy tất cả hóa đơn có ngày thanh toán trong tháng và năm đã chọn
        List<HoaDon> hoaDons = hoaDonRepository.findHoaDonsByMonthAndYear(month, year);

        double totalIncome = 0;

        // Duyệt qua từng hóa đơn để tính tổng thu nhập
        for (HoaDon hoaDon : hoaDons) {
            if (hoaDon.getSoTienGiamGia() == null){
                totalIncome += hoaDon.getTongTien();
            }else {
                totalIncome += hoaDon.getTongTien() - hoaDon.getSoTienGiamGia();
            }
//            // Lấy tất cả các chi tiết hóa đơn của mỗi hóa đơn
//            List<HoaDonChiTiet> hoaDonChiTiets = hoaDonChiTietRepository.findByHoaDon(hoaDon);
//
//            // Tính tổng thu nhập từ các chi tiết hóa đơn
//            for (HoaDonChiTiet chiTiet : hoaDonChiTiets) {
//                Float donGia = chiTiet.getDonGia();
//                Integer soLuong = chiTiet.getSoLuong();
//                if (donGia != null && soLuong != null) {
//                    totalIncome += donGia * soLuong;  // Cộng tổng thu nhập
//                }
//            }
        }

        return totalIncome;
    }

    public Double tinhThuNhapTheoNam(int year) {
        List<HoaDon> hoaDons = hoaDonRepository.findHoaDonsByYear(year);

        double totalIncome = 0.0;

        for (HoaDon hoaDon : hoaDons) {

            if (hoaDon.getSoTienGiamGia() == null){
                totalIncome += hoaDon.getTongTien();
            }else {
                totalIncome += hoaDon.getTongTien() - hoaDon.getSoTienGiamGia();
            }

//
//            List<HoaDonChiTiet> hoaDonChiTiets = hoaDonChiTietRepository.findByHoaDon(hoaDon);
//
//            for (HoaDonChiTiet chiTiet : hoaDonChiTiets) {
//                Float donGia = chiTiet.getDonGia();
//                Integer soLuong = chiTiet.getSoLuong();
//                if (donGia != null && soLuong != null) {
//                    totalIncome += donGia * soLuong;  // Cộng tổng thu nhập
//                }
//            }
        }

        return totalIncome;
    }

    public Map<LocalDate, Long> thongKeSoHoaDonTheoNgay(LocalDate filterDate) {
        List<Object[]> results = hoaDonRepository.countHoaDonTheoNgay(filterDate);
        return results.stream()
                .filter(result -> ((LocalDate) result[0]).equals(filterDate))  // Lọc theo ngày được truyền vào
                .collect(Collectors.toMap(
                        result -> (LocalDate) result[0], // ngày
                        result -> (Long) result[1]        // số lượng
                ));
    }

    public Map<String, Double> thongKeDoanhThuTheoLoaiDon(LocalDate ngayThanhToan) {
        Map<String, Double> doanhThu = new HashMap<>();
        Double doanhThuOnline = hoaDonRepository.tinhTongDoanhThuTheoLoaiDon(0, ngayThanhToan);
        Double doanhThuOffline = hoaDonRepository.tinhTongDoanhThuTheoLoaiDon(1, ngayThanhToan);

        doanhThu.put("Online", doanhThuOnline != null ? doanhThuOnline : 0.0);
        doanhThu.put("Offline", doanhThuOffline != null ? doanhThuOffline : 0.0);

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

                double doanhThu;
                if (hoaDon.getSoTienGiamGia() == null){
                    doanhThu = hoaDon.getTongTien();
                }else {
                    doanhThu = hoaDon.getTongTien()-hoaDon.getSoTienGiamGia();
                }

//                // Tính tổng doanh thu của hóa đơn
//                double doanhThu = hoaDonChiTiets.stream()
//                        .filter(chiTiet -> chiTiet.getDonGia() != null && chiTiet.getSoLuong() != null) // Kiểm tra null
//                        .mapToDouble(chiTiet -> chiTiet.getDonGia() * chiTiet.getSoLuong())
//                        .sum();

                doanhThuTheoNgay.put(selectedDate, doanhThu);
            }
        }

        return doanhThuTheoNgay;
    }
    public Map<Integer, Double> thongKeDoanhThuTheoThang(List<HoaDon> hoaDons) {
        Map<Integer, Double> doanhThuTheoThang = new HashMap<>();
        for (HoaDon hoaDon : hoaDons) {
            // Kiểm tra trạng thái hóa đơn
            if (hoaDon.getTrangThai() == 1 && hoaDon.getNgayThanhToan() != null) {
                int monthValue = hoaDon.getNgayThanhToan().getMonthValue();

                // Lấy danh sách chi tiết hóa đơn
                List<HoaDonChiTiet> hoaDonChiTiets = hoaDonChiTietRepository.findByHoaDon(hoaDon);

                double doanhThu;
                if (hoaDon.getSoTienGiamGia() == null){
                    doanhThu = hoaDon.getTongTien();
                }else {
                    doanhThu = hoaDon.getTongTien()-hoaDon.getSoTienGiamGia();
                }

//                // Tính doanh thu của từng hóa đơn
//                double doanhThu = hoaDonChiTiets.stream()
//                        .filter(chiTiet -> chiTiet.getDonGia() != null && chiTiet.getSoLuong() != null)
//                        .mapToDouble(chiTiet -> chiTiet.getDonGia() * chiTiet.getSoLuong())
//                        .sum();

                // Cập nhật tổng doanh thu theo tháng
                doanhThuTheoThang.put(monthValue, doanhThuTheoThang.getOrDefault(monthValue, 0.0) + doanhThu);
            }
        }
        return doanhThuTheoThang;
    }



}
