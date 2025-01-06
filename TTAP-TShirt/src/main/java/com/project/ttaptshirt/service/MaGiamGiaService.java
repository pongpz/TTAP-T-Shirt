package com.project.ttaptshirt.service;

import com.project.ttaptshirt.entity.MaGiamGia;

import java.util.List;

public interface MaGiamGiaService extends CommonService<MaGiamGia> {
    List<MaGiamGia> getMaGiamGia(Long idKh);
    MaGiamGia validateAndGetVoucher(Long voucherId, Long customerId);
    void themVoucherChoKhachHang(Long voucherId, Long customerId, Integer quantity);
}
