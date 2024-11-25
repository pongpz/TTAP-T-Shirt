package com.project.ttaptshirt.service;

import com.project.ttaptshirt.entity.KhachHang;

import java.util.List;

public interface KhachHangService extends CommonService<KhachHang> {
    List<KhachHang> searchCustomerByPhoneNumber(String phoneNumber);

    List<KhachHang> findAllOrderByNgayTao();

}
