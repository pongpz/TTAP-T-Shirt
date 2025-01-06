package com.project.ttaptshirt.service;

import com.project.ttaptshirt.entity.KhachHangVoucher;

import java.util.List;

public interface KhachHangVoucherService extends CommonService<KhachHangVoucher> {
    List<KhachHangVoucher> getMaGiamGia(Long khachHangId);
}
