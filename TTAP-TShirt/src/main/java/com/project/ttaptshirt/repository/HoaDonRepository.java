package com.project.ttaptshirt.repository;

import com.project.ttaptshirt.entity.HoaDon;
import com.project.ttaptshirt.entity.KhachHang;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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

    @Query("select hd from HoaDon hd order by hd.id desc")
    List<HoaDon> getAllHD(Pageable pageable);

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
    void hdChuanBiHang(Long idHd);
    @Transactional
    @Modifying
    @Query(value = "update hoa_don set trang_thai = 8 where id=?1" ,nativeQuery = true)
    void hdChoGiaoHang(Long idHd);

    @Transactional
    @Modifying
    @Query(value = "update hoa_don set trang_thai = 9 where id=?1" ,nativeQuery = true)
    void xacNhanDangGiaoHang(Long idHd);

    @Transactional
    @Modifying
    @Query(value = "update hoa_don set trang_thai = 10 where id=?1" ,nativeQuery = true)
    void hdDaGiaoHang(Long idHd);

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


    @Query("SELECT h FROM HoaDon h WHERE h.ngayThanhToan = :today and h.trangThai=1")
    List<HoaDon> findHoaDonsByNgayThanhToan(@Param("today") LocalDate today);

    @Query("SELECT h FROM HoaDon h WHERE YEAR(h.ngayThanhToan) = :year AND MONTH(h.ngayThanhToan) = :month and h.trangThai=1")
    List<HoaDon> findHoaDonsByMonthAndYear(@Param("month") int month, @Param("year") int year);


    @Query("SELECT h FROM HoaDon h WHERE FUNCTION('YEAR', h.ngayThanhToan) = :year and h.trangThai=1")
    List<HoaDon> findHoaDonsByYear(@Param("year") int year);


    @Query("SELECT h.ngayThanhToan, COUNT(h.id) FROM HoaDon h " +
            "WHERE h.ngayThanhToan IS NOT NULL and h.trangThai=1" +
            "AND h.ngayThanhToan = :filterDate " + // Thêm điều kiện lọc theo ngày
            "GROUP BY h.ngayThanhToan")
    List<Object[]> countHoaDonTheoNgay(@Param("filterDate") LocalDate filterDate);

    @Query("SELECT SUM(h.tongTien) FROM HoaDon h " +
            "WHERE h.loaiDon = :loaiDon AND h.ngayThanhToan = :ngayThanhToan " +
            "AND h.trangThai = 1")
    Double tinhTongDoanhThuTheoLoaiDon(@Param("loaiDon") Integer loaiDon,
                                       @Param("ngayThanhToan") LocalDate ngayThanhToan);

    // Truy vấn danh sách hóa đơn theo mã khách hàng
    @Query("SELECT h FROM HoaDon h WHERE h.khachHang.id = :khachHangId")
    List<HoaDon> findAllByKhachHangId(Long khachHangId);
}
