package com.project.ttaptshirt.repository;

import com.project.ttaptshirt.entity.KieuDang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface KieuDangRepository extends JpaRepository<KieuDang,Long> {

    @Query("select kd from KieuDang kd where kd.ten =:ten")
    KieuDang getKieuDangByTen(String ten);
}
