package com.project.ttaptshirt.service.impl;

import com.project.ttaptshirt.entity.KhachHang;
import com.project.ttaptshirt.entity.MaGiamGia;
import com.project.ttaptshirt.repository.KhachHangRepository;
import com.project.ttaptshirt.repository.MaGiamGiaRepo;
import com.project.ttaptshirt.service.KhachHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class KhachHangServiceImpl implements KhachHangService {

    @Autowired
    KhachHangRepository khachHangRepository;
    @Autowired
    MaGiamGiaRepo maGiamGiaRepo;
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

    @Override
    public List<KhachHang> findAllOrderByNgayTao() {
        return khachHangRepository.findAllOrderByNgayTao();
    }

    @Override
    public Page<KhachHang> findAll(Pageable pageable) {
        return khachHangRepository.findAll(pageable);
    }

    @Override
    public List<KhachHang> findAllById(List<Long> id) {
        return khachHangRepository.findAllById(id);
    }

    @Override
    public List<MaGiamGia> getList(Long idKhachHang) {
        Optional<KhachHang> khachHangOptional = khachHangRepository.findById(idKhachHang);
        if (khachHangOptional.isPresent()) {
            // Lấy danh sách mã giảm giá
            return null;
        } else {
            throw new RuntimeException("Không tìm thấy khách hàng với ID: " + idKhachHang);
        }
    }


}
