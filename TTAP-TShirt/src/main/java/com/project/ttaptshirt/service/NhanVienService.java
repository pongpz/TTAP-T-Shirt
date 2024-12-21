package com.project.ttaptshirt.service;

import com.project.ttaptshirt.entity.NhanVien;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NhanVienService extends CommonService<NhanVien> {
    Page<NhanVien> findAll(Pageable pageable);
}
