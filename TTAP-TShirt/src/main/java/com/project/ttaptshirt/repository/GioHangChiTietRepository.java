package com.project.ttaptshirt.repository;

import com.project.ttaptshirt.entity.GioHang;
import com.project.ttaptshirt.entity.GioHangChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GioHangChiTietRepository extends JpaRepository<GioHangChiTiet, Long> {
    List<GioHangChiTiet> findByGioHang(GioHang gioHang);
}
