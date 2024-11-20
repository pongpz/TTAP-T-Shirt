package com.project.ttaptshirt.repository;

import com.project.ttaptshirt.entity.SanPham;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SanPhamRepository extends JpaRepository<SanPham,Long> {
    List<SanPham> findByTenContaining(String ten);
    boolean existsByMa(String ma);

    @Query("SELECT sp FROM SanPham sp " +
            "JOIN sp.chiTietSanPhamList ct " +  // Kết nối với bảng ChiTietSanPham
            "WHERE (:ten IS NULL OR LOWER(sp.ten) LIKE LOWER(CONCAT('%', :ten, '%'))) " +
            "AND (:nhaSanXuat IS NULL OR LOWER(sp.nsx) LIKE LOWER(CONCAT('%', :nhaSanXuat, '%'))) " +
            "AND (:thuongHieu IS NULL OR LOWER(sp.thuongHieu) LIKE LOWER(CONCAT('%', :thuongHieu, '%'))) " +
            "AND (:kieuDang IS NULL OR LOWER(sp.kieuDang) LIKE LOWER(CONCAT('%', :kieuDang, '%'))) " +
            "AND (:chatLieu IS NULL OR LOWER(sp.chatLieu) LIKE LOWER(CONCAT('%', :chatLieu, '%'))) " +
            "AND (:minPrice IS NULL OR ct.giaBan >= :minPrice) " +
            "AND (:maxPrice IS NULL OR ct.giaBan <= :maxPrice) " +
            "AND (:kichCoId IS NULL OR ct.kichCo.id = :kichCoId) " +
            "AND (:mauSacId IS NULL OR ct.mauSac.id = :mauSacId)")
    Page<SanPham> filterSanPham(
            @Param("ten") String ten,
            @Param("nhaSanXuat") String nhaSanXuat,
            @Param("thuongHieu") String thuongHieu,
            @Param("kieuDang") String kieuDang,
            @Param("chatLieu") String chatLieu,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("kichCoId") Long kichCoId,
            @Param("mauSacId") Long mauSacId,
            Pageable pageable
    );
}
