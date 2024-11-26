package com.project.ttaptshirt.repository;

import com.project.ttaptshirt.entity.MaGiamGia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MaGiamGiaRepo extends JpaRepository<MaGiamGia, Long> {
    @Query("select mgg from MaGiamGia mgg where mgg.ma =:ma")
    List<MaGiamGia> findbyMa(String ma);

    @Query("select mgg from MaGiamGia mgg order by mgg.id desc")
    Page<MaGiamGia> findAllDESC(Pageable pageable);

    @Query("select mgg.ma from MaGiamGia mgg where mgg.id <>:id")
    List<String> findAllMaNotHaveThisId(Long id);

    @Query("select mgg from MaGiamGia mgg where (mgg.ten like %:ten% or mgg.ten is null) and (mgg.ma like %:ma% or mgg.ma is null) and (:hinhThuc is null or mgg.hinhThuc =:hinhThuc) and ((:thoiHan1 is null or mgg.ngayKetThuc >:thoiHan1) and (:thoiHan2 is null or mgg.ngayKetThuc <:thoiHan2)) and ((:soLuong1 is null or mgg.soLuong >:soLuong1) and (:soLuong2 is null or mgg.soLuong <=:soLuong2)) order by mgg.id desc")
    List<MaGiamGia> searchByAll(String ten, String ma, Boolean hinhThuc, LocalDateTime thoiHan1, LocalDateTime thoiHan2, Integer soLuong1, Integer soLuong2, Pageable pageable);

    @Query("select mgg from MaGiamGia mgg where mgg.ngayKetThuc <:ngayKetThuc or mgg.soLuong <= 0 and mgg.trangThai = true")
    List<MaGiamGia> getMGGHetHan(LocalDateTime ngayKetThuc);

    @Query("select mgg from MaGiamGia mgg where mgg.trangThai =:trangThai and (:keyword is null or mgg.ma like %:keyword% or mgg.ten like %:keyword%) order by mgg.id desc")
    List<MaGiamGia> getMaGiamGiaByTrangThaiKeyword (Boolean trangThai,String keyword);
}
