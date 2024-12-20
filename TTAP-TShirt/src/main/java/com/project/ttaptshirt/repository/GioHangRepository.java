package com.project.ttaptshirt.repository;

import com.project.ttaptshirt.entity.GioHang;
import com.project.ttaptshirt.entity.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GioHangRepository extends JpaRepository<GioHang, Long> {
    @Query("select d from GioHang d where d.user = :user and d.user.enable = :status")
    Optional<GioHang> findByUserAndStatus(TaiKhoan user, boolean status);

    GioHang findByUser(TaiKhoan user);
}
