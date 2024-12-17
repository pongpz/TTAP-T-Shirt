package com.project.ttaptshirt.repository;

import com.project.ttaptshirt.entity.GioHang;
import com.project.ttaptshirt.entity.GioHangChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GioHangChiTietRepository extends JpaRepository<GioHangChiTiet, Long> {
    List<GioHangChiTiet> findByGioHang(GioHang gioHang);
    void deleteByGioHang_Id(Long gioHangId);

    @Modifying
    @Query("UPDATE GioHangChiTiet ghct " +
            "SET ghct.gia = :newGiaBan " +
            "WHERE ghct.chiTietSanPham.id = :idChiTietSanPham")
    void updateGiaInDatHangChiTiet(@Param("idChiTietSanPham") Long idChiTietSanPham,
                                   @Param("newGiaBan") Double newGiaBan);

}
