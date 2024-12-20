package com.project.ttaptshirt.repository;


import com.project.ttaptshirt.entity.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KhachHangReopository extends JpaRepository<TaiKhoan, Long> {
}
