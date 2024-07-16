package com.project.ttaptshirt.repository;


import com.project.ttaptshirt.entity.ChucVu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChucVuRepo extends JpaRepository<ChucVu, Long> {
    ChucVu findByTen(String name);
}
