package com.project.ttaptshirt.repository;

import com.project.ttaptshirt.entity.HoaDon;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface HoaDonRepository extends JpaRepository<HoaDon, Long> {
    @Query("select hd from HoaDon hd where (:ma is null or hd.ma like %:ma% ) and (:tenkh is null or hd.khachHang.hoTen like %:tenkh% ) and (:tennv is null or hd.nhanVien.hoTen like %:tennv%) and (:sdt is null or hd.khachHang.soDienthoai like %:sdt%) and (:trangThai is null or hd.trangThai =:trangThai) and (:ngayThanhToan is null or hd.ngayThanhToan = :ngayThanhToan)")
    List<HoaDon> search(String ma,String tenkh, String tennv, String sdt, Boolean trangThai, LocalDate ngayThanhToan,Pageable pageable);

    @Query(value = "select * from hoa_don where trang_thai = 0",nativeQuery = true)
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
}
