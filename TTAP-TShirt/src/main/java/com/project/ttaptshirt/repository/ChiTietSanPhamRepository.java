package com.project.ttaptshirt.repository;

import com.project.ttaptshirt.entity.ChiTietSanPham;
import com.project.ttaptshirt.entity.KichCo;
import com.project.ttaptshirt.entity.MauSac;
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
import java.util.Optional;

@Repository
public interface ChiTietSanPhamRepository extends JpaRepository<ChiTietSanPham,Long> {
    boolean existsByMauSacIdAndKichCoIdAndSanPhamId(Long idMauSac, Long idKichCo, Long sanPhamId);


    @Transactional
    @Modifying
    @Query(value = "update chi_tiet_san_pham set so_luong = ?1 where id = ?2",nativeQuery = true)
    void  updateSoLuongCTSP(Integer soLuong,Long idCtsp);

    List<ChiTietSanPham> findBySanPhamId(Long id);

    @Query("select spct from ChiTietSanPham spct where spct.sanPham.id =:idSP and (:kichCo is null or spct.kichCo.id =:kichCo) and (:mauSac is null or spct.mauSac.id =:mauSac)")
    List<ChiTietSanPham> findByIDSanPham(Long idSP, String kichCo, String mauSac);

    @Query("select spct from ChiTietSanPham spct where (:ten is null or spct.sanPham.ten like %:ten%) and spct.trangThai = 0 order by spct.id DESC")
    List<ChiTietSanPham> findByTenSanPham(String ten);


    @Transactional
    @Modifying
    @Query(value = "update chi_tiet_san_pham set trang_thai = ?1 where id = ?2",nativeQuery = true)
    void  updateTrangThai(Integer trangThai,Long idCtsp);

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

    @Query("select spct from ChiTietSanPham spct where spct.soLuong = 0")
    List<ChiTietSanPham> getSPCTHetHan();

    @Query("select Min(c.giaBan) FROM ChiTietSanPham c WHERE c.sanPham.id = :sanPhamId")
    Double findMinGiaBan(@Param("sanPhamId") Long sanPhamId);

    @Query("SELECT c FROM ChiTietSanPham c WHERE c.sanPham.id = :sanPhamId AND c.kichCo.id = :kichCo AND c.mauSac.id = :mauSac")
    Optional<ChiTietSanPham> findChiTietSanPham(@Param("sanPhamId") Long sanPhamId,
                                                @Param("kichCo") Long kichCo,
                                                @Param("mauSac") Long mauSac);

//    @Query("select ctsp from ChiTietSanPham ctsp where (:ten is null or ctsp.sanPham.ten like %:ten%) and (ctsp.giaBan between :giaMin and :giaMax) and (ctsp.sanPham.nsx.id=:nsx) and (ctsp.sanPham.kieuDang.id=:kieuDang) and (ctsp.sanPham.thuongHieu.id=:thuongHieu) and (ctsp.sanPham.chatLieu.id=:chatLieu)")
//    List<ChiTietSanPham> searchCTSP(String ten,Double giaMax, Double giaMin,Long nsx,Long thuongHieu,Long kieuDang,Long chatLieu,Pageable pageable);

//    @Query("select ctsp from ChiTietSanPham ctsp where (:ten is null or ctsp.sanPham.ten like %:ten%) and (:giaMax is null or ctsp.giaBan<=:giaMax) and (:giaMin is null or ctsp.giaBan>=:giaMin) and (:nsx is null or ctsp.sanPham.nsx.id=:nsx) and (:kieuDang is null or ctsp.sanPham.kieuDang.id=:kieuDang) and (:thuongHieu is null or ctsp.sanPham.thuongHieu.id=:thuongHieu) and (:chatLieu is null or ctsp.sanPham.chatLieu.id=:chatLieu) order by ctsp.sanPham.id desc")
//    Page<ChiTietSanPham> searchCTSP(String ten, Double giaMax, Double giaMin, Long nsx, Long thuongHieu, Long kieuDang, Long chatLieu, Pageable pageable);


    boolean existsByMa(String ma);

    boolean existsBySanPhamAndMauSacAndKichCo(SanPham sanPham, MauSac mauSac, KichCo kichCo);

    @Query("select ctsp from ChiTietSanPham ctsp where ctsp.sanPham.id =:idSP")
    List<ChiTietSanPham> getCTSPByIdSP(Long idSP);

    @Query("SELECT c from ChiTietSanPham c where c.sanPham.id =:sanPhamId and c.mauSac.id =:mauSacId")
    List<ChiTietSanPham> findBySanPhamAndAndMauSac( Long sanPhamId,
                                                   Long mauSacId);

    @Query("SELECT c from ChiTietSanPham c where c.sanPham.id =:sanPhamId and c.mauSac.id =:mauSacId and c.kichCo.id =:kichCoId")
    Optional<ChiTietSanPham> findsoluong( Long sanPhamId,
                                                    Long mauSacId,Long kichCoId);
}
