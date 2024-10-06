package com.project.ttaptshirt.repository;


import com.project.ttaptshirt.entity.KhachHang;
import com.project.ttaptshirt.entity.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KhachHangReopository extends JpaRepository<KhachHang, Long> {
//    User findByUsername(String userName);
    Optional<KhachHang> findByEmail(String email);

    List<KhachHang> findByHoTenContainingIgnoreCase(String name);
}
