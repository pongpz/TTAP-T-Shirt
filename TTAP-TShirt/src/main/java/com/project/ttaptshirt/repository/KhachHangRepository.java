package com.project.ttaptshirt.repository;

import com.project.ttaptshirt.entity.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KhachHangRepository extends JpaRepository<KhachHang, Long> {
    @Query(value = "select * from khach_hang where ?1 = '' or so_dien_thoai like %?1% order by id desc", nativeQuery = true)
    List<KhachHang> searchKhachHangBySoDienThoai(String soDienThoai);

    @Query(value = "select * from khach_hang order by id desc", nativeQuery = true)
    List<KhachHang> findAllOrderByNgayTao();


}
