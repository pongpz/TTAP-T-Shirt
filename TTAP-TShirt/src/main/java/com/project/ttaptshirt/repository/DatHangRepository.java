package com.project.ttaptshirt.repository;

import com.project.ttaptshirt.entity.DatHang;
import com.project.ttaptshirt.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DatHangRepository extends JpaRepository<DatHang, Long> {
    @Query("select d from DatHang d where d.user = :user and d.user.enable = :status")
    Optional<DatHang> findByUserAndStatus(User user,boolean status);
}
