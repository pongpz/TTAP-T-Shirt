package com.project.ttaptshirt.service;

import com.project.ttaptshirt.entity.KhachHang;
import com.project.ttaptshirt.entity.MaGiamGia;
import com.project.ttaptshirt.entity.NhanVien;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface KhachHangService extends CommonService<KhachHang> {
    List<KhachHang> searchCustomerByPhoneNumber(String phoneNumber);

    List<KhachHang> findAllOrderByNgayTao();

    Page<KhachHang> findAll(Pageable pageable);

    List<KhachHang> findAllById(List<Long> id);

    List<MaGiamGia> getList(Long idKhachHang);
}
