package com.project.ttaptshirt.repository;


import com.project.ttaptshirt.entity.DiaChi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiaChiRepo extends JpaRepository<DiaChi, Long> {
}
