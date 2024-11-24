package com.project.ttaptshirt.repository;

import com.project.ttaptshirt.entity.SanPham;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SanPhamRepository extends JpaRepository<SanPham,Long> {

    boolean existsByMa(String ma);
    Page<SanPham> findByTenContaining(String ten, Pageable pageable);

    Page<SanPham> findAllByOrderByNgayTaoDesc(Pageable pageable);

            @Query("SELECT sp FROM SanPham sp " +
            "JOIN sp.chiTietSanPhamList ct " +  // Kết nối với bảng ChiTietSanPham
            "WHERE (:ten IS NULL OR LOWER(sp.ten) LIKE LOWER(CONCAT('%', :ten, '%'))) " +
            "AND (:nhaSanXuat IS NULL OR sp.nsx.id = :nhaSanXuat) " +  // Không dùng LOWER cho Long
            "AND (:thuongHieu IS NULL OR sp.thuongHieu.id = :thuongHieu) " +  // Không dùng LOWER cho Long
            "AND (:kieuDang IS NULL OR sp.kieuDang.id = :kieuDang) " +  // Không dùng LOWER cho Long
            "AND (:chatLieu IS NULL OR sp.chatLieu.id = :chatLieu) " +  // Không dùng LOWER cho Long
            "AND (:minPrice IS NULL OR ct.giaBan >= :minPrice) " +
            "AND (:maxPrice IS NULL OR ct.giaBan <= :maxPrice) " )
    Page<SanPham> filterSanPham(
            @Param("ten") String ten,
            @Param("nhaSanXuat") Long nhaSanXuat,
            @Param("thuongHieu") Long thuongHieu,
            @Param("kieuDang") Long kieuDang,
            @Param("chatLieu") Long chatLieu,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            Pageable pageable
    );

}
