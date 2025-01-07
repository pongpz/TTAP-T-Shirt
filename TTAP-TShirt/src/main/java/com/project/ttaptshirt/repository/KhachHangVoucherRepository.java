package com.project.ttaptshirt.repository;

import com.project.ttaptshirt.entity.KhachHang;
import com.project.ttaptshirt.entity.KhachHangVoucher;
import com.project.ttaptshirt.entity.MaGiamGia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface KhachHangVoucherRepository extends JpaRepository<KhachHangVoucher, Long> {
    List<KhachHangVoucher> findByKhachHang_Id(Long khachHangId);
    KhachHangVoucher findByKhachHangAndMaGiamGia(KhachHang khachHang, MaGiamGia voucher);
}
