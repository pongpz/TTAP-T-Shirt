package com.project.ttaptshirt.repository;

import com.project.ttaptshirt.entity.ChiTietSanPham;
import com.project.ttaptshirt.entity.KichCo;
import com.project.ttaptshirt.entity.SanPham;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("select spct from ChiTietSanPham spct where spct.sanPham.id =:idSP and (:kichCo is null or spct.kichCo.id =:kichCo) and (:mauSac is null or spct.mauSac.id =:mauSac)")
    List<ChiTietSanPham> findByIDSanPham(Long idSP, String kichCo, String mauSac);


    @Query("""
    SELECT spct FROM ChiTietSanPham spct WHERE 
    (:ten IS NULL OR spct.sanPham.ten LIKE %:ten%) AND
    (:minPrice IS NULL OR spct.giaBan >= :minPrice) AND
    (:maxPrice IS NULL OR spct.giaBan <= :maxPrice) AND
    (:kichCo IS NULL OR spct.kichCo.id = :kichCo)
""")
    Page<ChiTietSanPham> findByTenContainingAndPriceBetween(
            @Param("ten") String ten,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("kichCo") Integer kichCo,
            Pageable pageable);

    @Query("select spct from ChiTietSanPham  spct order by spct.ngayTao desc")
    List<ChiTietSanPham> getListNewCTSP();

    @Query("select spct from ChiTietSanPham spct where spct.sanPham.id =:id")
    List<ChiTietSanPham> getThuocTinhSPCT(Long id);

}
