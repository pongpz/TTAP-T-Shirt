package com.project.ttaptshirt.repository;

import com.project.ttaptshirt.entity.SanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SanPhamRepository extends JpaRepository<SanPham,Long> {
    List<SanPham> findByTenContaining(String ten);
    boolean existsByMa(String ma);

}
