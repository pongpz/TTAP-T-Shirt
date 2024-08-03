package com.project.ttaptshirt.repository;

import com.project.ttaptshirt.entity.SanPham;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SanPhamRepository extends JpaRepository<SanPham,Long> {
    List<SanPham> findByTenContaining(String ten);
}
