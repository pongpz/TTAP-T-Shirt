package com.project.ttaptshirt.repository;

import com.project.ttaptshirt.entity.HoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Date;
import java.util.List;

@Repository
public interface HoaDonRepository extends JpaRepository<HoaDon, Long> {

    @Query("select hd from HoaDon hd where (hd.ma = :keyword or hd.khachHang.hoTen = :keyword or hd.khachHang.soDienthoai = :keyword) and hd.trangThai = :trangThai and hd.loaiDon = :loaiDon and hd.ngayThanhToan = :ngayThanhToan")
    List<HoaDon> search(String keyword, Integer trangThai, Integer loaiDon, Date ngayThanhToan);

    @Query("select hd from HoaDon hd where hd.ma = :keyword or hd.khachHang.hoTen = :keyword or hd.khachHang.soDienthoai = :keyword")
    List<HoaDon> searchKeyword(String keyword);

    @Query("select hd from HoaDon hd where hd.trangThai = :trangThai")
    List<HoaDon> searchTrangThai(Integer trangThai);

    @Query("select hd from HoaDon hd where hd.loaiDon = :loaiDon")
    List<HoaDon> searchLoaiDon(Integer loaiDon);

    @Query("select hd from HoaDon hd where hd.ngayThanhToan = :ngayThanhToan")
    List<HoaDon> searchNgayThanhToan(Date ngayThanhToan);

    @Query("select hd from HoaDon hd where (hd.ma = :keyword or hd.khachHang.hoTen = :keyword or hd.khachHang.soDienthoai = :keyword) and hd.trangThai = :trangThai")
    List<HoaDon> searchKeywordVaTrangThai(String keyword, Integer trangThai);

    @Query("select hd from HoaDon hd where (hd.ma = :keyword or hd.khachHang.hoTen = :keyword or hd.khachHang.soDienthoai = :keyword) and hd.loaiDon = :loaiDon")
    List<HoaDon> searchKeywordVaLoaiDon(String keyword, Integer loaiDon);

    @Query("select hd from HoaDon hd where (hd.ma = :keyword or hd.khachHang.hoTen = :keyword or hd.khachHang.soDienthoai = :keyword) and hd.ngayThanhToan = :ngayThanhToan")
    List<HoaDon> searchKeywordVaNgayThanhToan(String keyword, Date ngayThanhToan);

    @Query("select hd from HoaDon hd where (hd.ma = :keyword or hd.khachHang.hoTen = :keyword or hd.khachHang.soDienthoai = :keyword) and hd.trangThai = :trangThai and hd.ngayThanhToan = :ngayThanhToan")
    List<HoaDon> searchKeywordTrangThaiNgayThanhToan(String keyword, Integer trangThai, Date ngayThanhToan);

    @Query("select hd from HoaDon hd where (hd.ma = :keyword or hd.khachHang.hoTen = :keyword or hd.khachHang.soDienthoai = :keyword) and hd.loaiDon = :loaiDon and hd.ngayThanhToan = :ngayThanhToan")
    List<HoaDon> searchKeywordLoaiHDNgayThanhToan(String keyword, Integer loaiDon, Date ngayThanhToan);

    @Query("select hd from HoaDon hd where (hd.ma = :keyword or hd.khachHang.hoTen = :keyword or hd.khachHang.soDienthoai = :keyword) and hd.loaiDon = :loaiDon and hd.trangThai = :trangThai")
    List<HoaDon> searchKeywordTrangThaiLoaiDon(String keyword, Integer loaiDon, Integer trangThai);

    @Query("select hd from HoaDon hd where hd.loaiDon = :loaiDon and hd.trangThai = :trangThai and hd.ngayThanhToan = :ngayThanhToan")
    List<HoaDon> searchLoaiDonTrangThaiNgayThanhToan(Integer loaiDon, Integer trangThai, Date ngayThanhToan);

    @Query("select hd from HoaDon hd where hd.loaiDon = :loaiDon and hd.ngayThanhToan = :ngayThanhToan")
    List<HoaDon> searchLoaiDonNgayThanhToan(Integer loaiDon, Date ngayThanhToan);

    @Query(value = "select * from hoa_don where trang_thai = 0",nativeQuery = true)
    List<HoaDon> getListHDChuaThanhToan();

    @Modifying
    @Query(value = "update hoa_don set  trang_thai = ?1 where id = ?2",nativeQuery = true)
    void updateHoaDonStatus(int status,Long id);
}
