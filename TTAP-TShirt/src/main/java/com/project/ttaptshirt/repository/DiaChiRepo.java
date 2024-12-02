package com.project.ttaptshirt.repository;


import com.project.ttaptshirt.entity.DiaChi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiaChiRepo extends JpaRepository<DiaChi, Long> {
    List<DiaChi> findByUserId(Long userId);

    Optional<DiaChi> findById(Long id);
}
