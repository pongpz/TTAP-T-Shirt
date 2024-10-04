package com.project.ttaptshirt.repository;

import com.project.ttaptshirt.entity.DatHang;
import com.project.ttaptshirt.entity.DatHangChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DatHangChiTietRepository extends JpaRepository<DatHangChiTiet, Long> {
    List<DatHangChiTiet> findByDatHang(DatHang datHang);
}
