package com.project.ttaptshirt.service;

import com.project.ttaptshirt.dto.CartDTO;
import com.project.ttaptshirt.entity.GioHang;
import com.project.ttaptshirt.entity.HoaDon;
import com.project.ttaptshirt.entity.KhachHang;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface HoaDonService extends CommonService<HoaDon> {

    @Transactional
    public void updateTrangThaiHD(int trangThai, Long id);

    public List<HoaDon> getListHDChuaThanhToan();

    public List<HoaDon> getListHDDaThanhToan();

    public List<HoaDon> getListDonHang(KhachHang khachHang );

    public HoaDon getDonHang(Long id);

    public void updateTongTien(Long idHd, Double tongTien);

    public HoaDon createHoaDon(CartDTO cart, String fullName, String phoneNumber, String address);

    public HoaDon createHoaDon2(GioHang cart, List<Long> selectedProductIds, String fullName, String phoneNumber, String address);

    void xacNhanHoaDon(Long idHoaDon);

    void hoanThanhHoaDon(Long idHoaDon);

    void huyHoaDonOnline(Long idHoaDon);

}
