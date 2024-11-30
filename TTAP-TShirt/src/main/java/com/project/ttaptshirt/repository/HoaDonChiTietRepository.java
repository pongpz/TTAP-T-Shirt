package com.project.ttaptshirt.repository;

import com.project.ttaptshirt.entity.HoaDon;
import com.project.ttaptshirt.entity.HoaDonChiTiet;
import org.springframework.data.domain.Pageable;
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

    @Query(value = "select * from hoa_don_chi_tiet where id_hoa_don = ?1",nativeQuery = true)
    List<HoaDonChiTiet> getHoaDonChiTietByIdHd(Long idHd);



    @Query("SELECT SUM(hdct.donGia * hdct.soLuong) FROM HoaDonChiTiet hdct WHERE hdct.hoaDon.trangThai = 1")
    Double getTotalRevenue();

    @Query("SELECT hdct.chiTietSanPham.sanPham.ten, SUM(hdct.soLuong) AS totalSold " +
            "FROM HoaDonChiTiet hdct " +
            "WHERE hdct.hoaDon.trangThai = 1 " +
            "GROUP BY hdct.chiTietSanPham.sanPham.ten " +
            "ORDER BY totalSold DESC")
    List<Object[]> getBestSellingProducts();

    @Query("SELECT SUM(hdct.soLuong) FROM HoaDonChiTiet hdct WHERE hdct.hoaDon.trangThai = 1")
    Integer getTotalQuantitySold();

    @Query(value = "SELECT MONTH(ngay_thanh_toan) AS month, SUM(tong_tien) AS revenue " +
            "FROM hoa_don " +
            "WHERE trang_thai = 1 " +
            "GROUP BY MONTH(ngay_thanh_toan) " +
            "ORDER BY month ASC", nativeQuery = true)
    List<Object[]> getMonthlyRevenue();


    List<HoaDonChiTiet> findByHoaDon(HoaDon hoaDon);


}


