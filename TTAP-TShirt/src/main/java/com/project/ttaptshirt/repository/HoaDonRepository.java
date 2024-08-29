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

    @Query("select hd from HoaDon hd where (hd.ma = :keyword or hd.khachHang.hoTen = :keyword or hd.khachHang.soDienthoai = :keyword or :keyword is null) and (hd.trangThai = :trangThai or :trangThai is null) and (hd.loaiDon = :loaiDon or :loaiDon is null) and (hd.ngayThanhToan = :ngayThanhToan or :ngayThanhToan is null)")
    List<HoaDon> search(String keyword, Integer trangThai, Integer loaiDon, Date ngayThanhToan);

    @Query(value = "select * from hoa_don where trang_thai = 0",nativeQuery = true)
    List<HoaDon> getListHDChuaThanhToan();

    @Query(value = "select * from hoa_don where trang_thai = 1",nativeQuery = true)
    List<HoaDon> getListHDDaThanhToan();

    @Modifying
    @Query(value = "update hoa_don set  trang_thai = ?1 where id = ?2",nativeQuery = true)
    void updateHoaDonStatus(int status,Long id);
}
