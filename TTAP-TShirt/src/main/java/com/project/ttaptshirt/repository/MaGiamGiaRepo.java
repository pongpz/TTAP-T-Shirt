package com.project.ttaptshirt.repository;

import com.project.ttaptshirt.entity.MaGiamGia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaGiamGiaRepo extends JpaRepository<MaGiamGia, Long> {
    @Query("select mgg from MaGiamGia mgg where mgg.ma =:ma")
    List<MaGiamGia> findbyMa(String ma);

    @Query("select mgg.ma from MaGiamGia mgg where mgg.id <>:id")
    List<String> findAllMaNotHaveThisId(Long id);
}
