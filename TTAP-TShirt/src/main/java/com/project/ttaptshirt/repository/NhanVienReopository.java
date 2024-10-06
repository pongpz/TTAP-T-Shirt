package com.project.ttaptshirt.repository;


import com.project.ttaptshirt.entity.NhanVien;
import com.project.ttaptshirt.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NhanVienReopository extends JpaRepository<NhanVien, Long> {
//    User findByUsername(String userName);
    Optional<NhanVien> findByEmail(String email);

    List<NhanVien> findByCv_Ten(String cv);

    List<NhanVien> findByHoTenContainingIgnoreCase(String name);
}
