package com.project.ttaptshirt.repository;

import com.project.ttaptshirt.entity.NhanVien;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NhanVienRepository extends JpaRepository<NhanVien, Long> {
    @Query("select nv from NhanVien nv where nv.taiKhoan.role.id = 1 or nv.taiKhoan.role.id = 3")
    Page<NhanVien> findAll(Pageable pageable);
}
