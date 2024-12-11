package com.project.ttaptshirt.service.impl;

import com.project.ttaptshirt.entity.SanPham;
import com.project.ttaptshirt.repository.SanPhamRepository;
import com.project.ttaptshirt.service.HinhAnhService;
import com.project.ttaptshirt.service.SanPhamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@Service
public class SanPhamServiceImpl implements SanPhamService {
    @Autowired
    SanPhamRepository sanPhamRepository;



    @Override
    public void save(SanPham sanPham) {
        sanPhamRepository.save(sanPham);
    }

    @Override
    public SanPham findById(Long id) {
        return sanPhamRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteById(Long id) {
        sanPhamRepository.deleteById(id);
    }

    @Override
    public List<SanPham> findAll() {
        return sanPhamRepository.findAll();
    }

}
