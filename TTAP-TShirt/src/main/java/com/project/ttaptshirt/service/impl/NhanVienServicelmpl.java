package com.project.ttaptshirt.service.impl;

import com.project.ttaptshirt.entity.NhanVien;
import com.project.ttaptshirt.repository.NhanVienRepository;
import com.project.ttaptshirt.service.NhanVienService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NhanVienServicelmpl implements NhanVienService {

    @Autowired
    private NhanVienRepository nhanVienRepository;

    @Override
    public Page<NhanVien> findAll(Pageable pageable) {
        return nhanVienRepository.findAll(pageable);
    }


    @Override
    public void save(NhanVien nhanVien) {
        nhanVienRepository.save(nhanVien);
    }

    @Override
    public NhanVien findById(Long id) {
        return nhanVienRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteById(Long id) {
        nhanVienRepository.deleteById(id);
    }

    @Override
    public List<NhanVien> findAll() {
        return List.of();
    }


}
