package com.project.ttaptshirt.repository;

import com.project.ttaptshirt.entity.HoaDonChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Repository
public interface HoaDonChiTietRepository extends JpaRepository<HoaDonChiTiet,Long> {
    @Query("select h from HoaDonChiTiet h where h.hoaDon.id = :id ")
    List<HoaDonChiTiet> getHoaDonChiTietByHoaDonId(@Param("id") Long id);

    @Transactional
    @Modifying
    @Query(value = "update hoa_don_chi_tiet set so_luong = ?1 where id = ?2",nativeQuery = true)
    void updateSoLuongSpHdct(Integer soLuong,Long idHdct);
}
