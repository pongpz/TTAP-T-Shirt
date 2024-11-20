package com.project.ttaptshirt.service;

import com.project.ttaptshirt.dto.CartDTO;
import com.project.ttaptshirt.entity.DatHang;
import com.project.ttaptshirt.entity.HoaDon;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface HoaDonService extends CommonService<HoaDon> {

    @Transactional
    public void updateTrangThaiHD(int trangThai, Long id);

    public List<HoaDon> getListHDChuaThanhToan();

    public List<HoaDon> getListHDDaThanhToan();


    public void updateTongTien(Long idHd, Double tongTien);

    public HoaDon createHoaDon(CartDTO cart, String fullName, String phoneNumber, String address);

    public HoaDon createHoaDon2(DatHang cart,List<Long> selectedProductIds, String fullName, String phoneNumber, String address);

    void xacNhanHoaDon(Long idHoaDon);

    void hoanThanhHoaDon(Long idHoaDon);

    void huyHoaDonOnline(Long idHoaDon);

}
