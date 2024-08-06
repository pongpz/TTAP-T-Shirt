package com.project.ttaptshirt.service.impl;

import com.project.ttaptshirt.entity.ChiTietSanPham;
import com.project.ttaptshirt.repository.ChiTietSanPhamRepository;
import com.project.ttaptshirt.service.ChiTietSanPhamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChiTietSanPhamServiceImpl implements ChiTietSanPhamService {
    @Autowired
    ChiTietSanPhamRepository chiTietSanPhamRepository;

    @Override
    public void save(ChiTietSanPham chiTietSanPham) {
        chiTietSanPhamRepository.save(chiTietSanPham);
    }

    @Override
    public ChiTietSanPham findById(Long id) {
        return chiTietSanPhamRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteById(Long id) {
        chiTietSanPhamRepository.deleteById(id);
    }

    @Override
    public List<ChiTietSanPham> findAll() {
        return chiTietSanPhamRepository.findAll();
    }

    @Override
    public void updateSoLuongCtsp(Integer soLuong, Long id) {
        chiTietSanPhamRepository.updateSoLuongCTSP(soLuong,id);
    }

}
