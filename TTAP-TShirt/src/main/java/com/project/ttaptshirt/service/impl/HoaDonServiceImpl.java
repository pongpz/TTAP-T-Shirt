package com.project.ttaptshirt.service.impl;

import com.project.ttaptshirt.dto.CartDTO;
import com.project.ttaptshirt.dto.CartItemDTO;
import com.project.ttaptshirt.entity.ChiTietSanPham;
import com.project.ttaptshirt.entity.HoaDon;
import com.project.ttaptshirt.entity.HoaDonChiTiet;
import com.project.ttaptshirt.repository.ChiTietSanPhamRepository;
import com.project.ttaptshirt.repository.HoaDonChiTietRepository;
import com.project.ttaptshirt.repository.HoaDonRepository;
import com.project.ttaptshirt.service.HoaDonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class HoaDonServiceImpl implements HoaDonService {

    private final HoaDonRepository hoadonRepository;

    private final HoaDonChiTietRepository hoadonChiTietRepository;



    @Autowired
    HoaDonRepository hoaDonRepository;

    @Autowired
    ChiTietSanPhamRepository chiTietSanPhamRepository;

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

    public void updateTongTien(Long idHd, Double tongTien) {
        hoaDonRepository.updateTongTienHd(idHd,tongTien);
    }

    public HoaDon createHoaDon(CartDTO cart, String fullName, String phoneNumber, String address) {
        if (cart == null|| cart.getItems() == null || cart.getItems().isEmpty()){
            throw new IllegalArgumentException("Giỏ hàng trống, không thể tạo hóa đơn.");
        }
        HoaDon hoaDon = new HoaDon();
        hoaDon.setMa("HD" + (int) (Math.random() * 1000000));
        hoaDon.setTenNguoiNhan(fullName);
        hoaDon.setSdtNguoiNhan(phoneNumber);
        hoaDon.setDiaChiGiaoHang(address);
        hoaDon.setNgayTao(LocalDate.now());
        hoaDon.setTrangThai(3);
        hoaDon.setLoaiDon(1);
        hoaDon.setTongTien(cart.getTotalAmount().floatValue());

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
}
