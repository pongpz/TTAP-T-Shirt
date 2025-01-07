package com.project.ttaptshirt.service.impl;

import com.project.ttaptshirt.entity.KhachHangVoucher;
import com.project.ttaptshirt.repository.KhachHangVoucherRepository;
import com.project.ttaptshirt.service.KhachHangVoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KhachHangVoucherServicelmpl implements KhachHangVoucherService {
    @Autowired
    private KhachHangVoucherRepository khachHangVoucherRepository;
    @Override
    public List<KhachHangVoucher> getMaGiamGia(Long khachHangId) {
        return khachHangVoucherRepository.findByKhachHang_Id(khachHangId);
    }

    @Override
    public void save(KhachHangVoucher khachHangVoucher) {

    }

    @Override
    public KhachHangVoucher findById(Long id) {
        return null;
    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public List<KhachHangVoucher> findAll() {
        return List.of();
    }
}
