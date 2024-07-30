package com.project.ttaptshirt.repository;

import com.project.ttaptshirt.entity.HoaDonChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface HoaDonChiTietRepository extends JpaRepository<HoaDonChiTiet,Long> {
    @Query("select h from HoaDonChiTiet h where h.hoaDon.id = :id ")
    List<HoaDonChiTiet> getHoaDonChiTietByHoaDonId(@Param("id") Long id);
}
