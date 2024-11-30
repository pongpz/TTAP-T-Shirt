package com.project.ttaptshirt.repository;

import com.project.ttaptshirt.entity.HoaDon;
import com.project.ttaptshirt.entity.KhachHang;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HoaDonRepository extends JpaRepository<HoaDon, Long> {
    @Query("select hd from HoaDon hd where (:ma is null or hd.ma like %:ma%) and (hd.khachHang is null or hd.khachHang.hoTen like %:keyword% or hd.khachHang.soDienThoai like %:keyword%) and (:trangThai is null or hd.trangThai =:trangThai) and (:ngayThanhToan is null or hd.ngayThanhToan =:ngayThanhToan) and (:loaiDon is null or hd.loaiDon =:loaiDon) order by hd.id desc")
    List<HoaDon> search(String ma,String keyword, Integer trangThai, LocalDate ngayThanhToan,Integer loaiDon, Pageable pageable);

    @Query("select hd from HoaDon hd where (:keyword is null or hd.ma like %:keyword% ) and (:trangThai is null or hd.trangThai =:trangThai) and (:ngayThanhToan is null or hd.ngayThanhToan =:ngayThanhToan) and (:loaiDon is null or hd.loaiDon =:loaiDon) order by hd.id desc")
    List<HoaDon> search2(String keyword, Integer trangThai, LocalDate ngayThanhToan,Integer loaiDon,Pageable pageable);

    @Query(value = "select * from hoa_don where trang_thai = 0 and loai_don = 1 order by ngay_tao DESC",nativeQuery = true)
    List<HoaDon> getListHDChuaThanhToan();

    @Query(value = "select * from hoa_don where trang_thai = 1",nativeQuery = true)
    List<HoaDon> getListHDDaThanhToan();

    @Modifying
    @Query(value = "update hoa_don set  trang_thai = ?1 where id = ?2",nativeQuery = true)
    void updateHoaDonStatus(int status,Long id);

    @Query("select hd from HoaDon hd where hd.loaiDon = 1 order by hd.id desc")
    List<HoaDon> getAllHDTaiQuay(Pageable pageable);

    @Query("select hd from HoaDon hd where hd.loaiDon = 0 order by hd.id desc")
    List<HoaDon> getAllHDOnline(Pageable pageable);

    @Query("select hd from HoaDon hd where hd.ma =:ma")
    List<HoaDon> getHDByMa(String ma);

    @Transactional
    @Modifying
    @Query(value = "update hoa_don set tong_tien = ?2 where id=?1" ,nativeQuery = true)
    void updateTongTienHd(Long idHd,Double tongTien);

    @Transactional
    @Modifying
    @Query(value = "update hoa_don set trang_thai = 6 where id=?1" ,nativeQuery = true)
    void xacNhanHoaDon(Long idHd);

    @Transactional
    @Modifying
    @Query(value = "update hoa_don set trang_thai = 7 where id=?1" ,nativeQuery = true)
    void hdChoGiaoHang(Long idHd);

    @Transactional
    @Modifying
    @Query(value = "update hoa_don set trang_thai = 8 where id=?1" ,nativeQuery = true)
    void xacNhanDangGiaoHang(Long idHd);
    @Transactional
    @Modifying
    @Query(value = "update hoa_don set trang_thai = 1 where id=?1" ,nativeQuery = true)
    void hoanThanhHoaDon(Long idHd);

    @Transactional
    @Modifying
    @Query(value = "update hoa_don set trang_thai = 2 where id=?1" ,nativeQuery = true)
    void huyHoaDonOnline(Long idHd);


    List<HoaDon> findByTrangThaiAndNgayTaoBefore(int trangThai, LocalDateTime ngayTao);

    @Query("select hd from HoaDon hd where hd.khachHang =:khachHang order by hd.id desc ")
    List<HoaDon> findByKhachHang(KhachHang khachHang);

}
