package com.project.ttaptshirt.service.impl;

import com.project.ttaptshirt.entity.KhachHang;
import com.project.ttaptshirt.repository.KhachHangRepository;
import com.project.ttaptshirt.service.KhachHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class KhachHangServiceImpl implements KhachHangService {

    @Autowired
    KhachHangRepository khachHangRepository;
    @Override
    public void save(KhachHang khachHang) {
        khachHangRepository.save(khachHang);
    }

    @Override
    public KhachHang findById(Long id) {
        return khachHangRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteById(Long id) {
        khachHangRepository.deleteById(id);
    }

    @Override
    public List<KhachHang> findAll() {
        return khachHangRepository.findAll();
    }

    @Override
    public List<KhachHang> searchCustomerByPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return khachHangRepository.findAll();
        }
        return khachHangRepository.searchKhachHangBySoDienThoai(phoneNumber);
    }
}
