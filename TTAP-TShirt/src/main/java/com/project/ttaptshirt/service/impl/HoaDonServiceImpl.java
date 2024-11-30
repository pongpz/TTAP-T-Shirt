package com.project.ttaptshirt.service.impl;

import com.project.ttaptshirt.dto.CartDTO;
import com.project.ttaptshirt.dto.CartItemDTO;
import com.project.ttaptshirt.entity.*;
import com.project.ttaptshirt.repository.ChiTietSanPhamRepository;
import com.project.ttaptshirt.repository.HoaDonChiTietRepository;
import com.project.ttaptshirt.repository.HoaDonRepository;
import com.project.ttaptshirt.security.CustomUserDetail;
import com.project.ttaptshirt.service.HoaDonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class HoaDonServiceImpl implements HoaDonService {

    private final HoaDonRepository hoadonRepository;

    private final HoaDonChiTietRepository hoadonChiTietRepository;



    @Autowired
    HoaDonRepository hoaDonRepository;

    @Autowired
    ChiTietSanPhamRepository chiTietSanPhamRepository;


    //huy hoa don cho
    @Transactional
    public void huyHoaDonCho(){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiredTime = now.minusDays(1);

        List<HoaDon> hoadons = hoaDonRepository.findByTrangThaiAndNgayTaoBefore(0,expiredTime);

        hoadons.forEach(hoadon -> hoadon.setTrangThai(5));
        hoaDonRepository.saveAll(hoadons);

    }

    public HoaDonServiceImpl(HoaDonRepository hoadonRepository, HoaDonChiTietRepository hoadonChiTietRepository) {
        this.hoadonRepository = hoadonRepository;
        this.hoadonChiTietRepository = hoadonChiTietRepository;
    }

    @Override
    public void save(HoaDon hoaDon) {
        hoaDonRepository.save(hoaDon);
    }

    @Override
    public HoaDon findById(Long id) {
        return hoaDonRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteById(Long id) {
        hoaDonRepository.deleteById(id);
    }

    @Override
    public List<HoaDon> findAll() {
        return hoaDonRepository.findAll();
    }

    @Override
    public void updateTrangThaiHD(int trangThai, Long id) {
         hoaDonRepository.updateHoaDonStatus(trangThai,id);
    }

    @Override
    public List<HoaDon> getListHDChuaThanhToan() {
        return hoaDonRepository.getListHDChuaThanhToan();
    }

    @Override
    public List<HoaDon> getListHDDaThanhToan() {
        return hoaDonRepository.getListHDDaThanhToan();
    }

    @Override
    public List<HoaDon> getListDonHang(KhachHang khachHang) {
        return hoaDonRepository.findByKhachHang(khachHang);
    }

    @Override
    public HoaDon getDonHang(Long id) {
        return hoaDonRepository.findById(id).orElse(null);
    }

    @Override
    public void updateTongTien(Long idHd, Double tongTien) {
        hoaDonRepository.updateTongTienHd(idHd,tongTien);
    }

    @Override
    public HoaDon createHoaDon(CartDTO cart, String fullName, String phoneNumber, String address) {
        if (cart == null|| cart.getItems() == null || cart.getItems().isEmpty()){
            throw new IllegalArgumentException("Giỏ hàng trống, không thể tạo hóa đơn.");
        }
        HoaDon hoaDon = new HoaDon();
        hoaDon.setMa("HD" + (int) (Math.random() * 1000000));
        hoaDon.setTenNguoiNhan(fullName);
        hoaDon.setSdtNguoiNhan(phoneNumber);
        hoaDon.setDiaChiGiaoHang(address);
        hoaDon.setNgayTao(LocalDateTime.now());
        hoaDon.setTrangThai(3);
        hoaDon.setLoaiDon(0);
        hoaDon.setTongTien(cart.getTotalAmount().doubleValue());

        HoaDon saveHd = hoaDonRepository.save(hoaDon);

        for(CartItemDTO item : cart.getItems()) {
            HoaDonChiTiet chiTiet = new HoaDonChiTiet();
            chiTiet.setHoaDon(saveHd);
            ChiTietSanPham chiTietSanPham = chiTietSanPhamRepository.findById(item.getIdItem())
                            .orElseThrow(() -> new RuntimeException("Chi tiết sản phẩm không tồn tại: " + item.getIdItem()));
            chiTiet.setChiTietSanPham(chiTietSanPham);
            chiTiet.setSoLuong(item.getQuantity());
            chiTiet.setDonGia(item.getPrice().floatValue());

            hoadonChiTietRepository.save(chiTiet);
        }
        return saveHd;

    }


    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetail) {
            return ((CustomUserDetail) principal).getUser();
        }
        throw new IllegalStateException("Người dùng chưa đăng nhập.");
    }

    @Override
    public HoaDon createHoaDon2(GioHang cart, List<Long> selectedProductIds, String fullName, String phoneNumber, String address) {
        if (cart == null|| cart.getItems() == null || cart.getItems().isEmpty()){
            throw new IllegalArgumentException("Giỏ hàng trống, không thể tạo hóa đơn.");
        }

        // Lọc các sản phẩm được chọn từ giỏ hàng
        List<GioHangChiTiet> selectedItems = cart.getItems().stream()
                .filter(item -> selectedProductIds.contains(item.getChiTietSanPham().getId()))
                .toList();

        if (selectedItems.isEmpty()) {
            throw new IllegalArgumentException("Không có sản phẩm nào được chọn để tạo hóa đơn.");
        }

        double tongTien = selectedItems.stream().mapToDouble(item -> item.getGia().doubleValue() * item.getSoLuong()).sum();

        HoaDon hoaDon = new HoaDon();
        hoaDon.setMa("HD" + (int) (Math.random() * 1000000));
        User user = getCurrentUser();
        KhachHang khachHang = user.getKhachHang();
        hoaDon.setKhachHang(khachHang);
        hoaDon.setTenNguoiNhan(fullName);
        hoaDon.setSdtNguoiNhan(phoneNumber);
        hoaDon.setDiaChiGiaoHang(address);
        hoaDon.setNgayTao(LocalDateTime.now());
        hoaDon.setTrangThai(3);
        hoaDon.setLoaiDon(2);
        hoaDon.setTongTien(tongTien);

        HoaDon saveHd = hoaDonRepository.save(hoaDon);

        for(GioHangChiTiet item : cart.getItems()) {
            HoaDonChiTiet chiTiet = new HoaDonChiTiet();
            chiTiet.setHoaDon(saveHd);
            ChiTietSanPham chiTietSanPham = chiTietSanPhamRepository.findById(item.getId())
                    .orElseThrow(() -> new RuntimeException("Chi tiết sản phẩm không tồn tại: " + item.getId()));
            chiTiet.setChiTietSanPham(chiTietSanPham);
            chiTiet.setSoLuong(item.getSoLuong());
            chiTiet.setDonGia(item.getGia().floatValue());

            hoadonChiTietRepository.save(chiTiet);
        }
        return saveHd;

    }

    @Override
    public void xacNhanHoaDon(Long idHoaDon) {
        hoaDonRepository.xacNhanHoaDon(idHoaDon);
    }

    @Override
    public void hdChoGiaoHang(Long idHoaDon) {
        hoaDonRepository.hdChoGiaoHang(idHoaDon);
    }

    @Override
    public void xacNhanDangGiaoHang(Long idHoaDon) {
        hoaDonRepository.xacNhanDangGiaoHang(idHoaDon);
    }

    @Override
    public void hoanThanhHoaDon(Long idHoaDon) {
        hoaDonRepository.hoanThanhHoaDon(idHoaDon);
    }

    @Override
    public void huyHoaDonOnline(Long idHoaDon) {
        hoaDonRepository.huyHoaDonOnline(idHoaDon);
    }

}
