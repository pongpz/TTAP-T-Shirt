package com.project.ttaptshirt.repository;



import com.project.ttaptshirt.entity.GioHangChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GioHangRepo extends JpaRepository<GioHangChiTiet, Long> {
}
