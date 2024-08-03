package com.project.ttaptshirt.repository;

import com.project.ttaptshirt.entity.ChiTietSanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ChiTietSanPhamRepository extends JpaRepository<ChiTietSanPham,Long> {
    @Transactional
    @Modifying
    @Query(value = "update chi_tiet_san_pham set so_luong = ?1 where id = ?2",nativeQuery = true)
    void  updateSoLuongCTSP(Integer soLuong,Long idCtsp);

    List<ChiTietSanPham> findBySanPhamId(Long id);

}
