package com.project.ttaptshirt.repository;

import com.project.ttaptshirt.entity.HinhAnh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HinhAnhRepository extends JpaRepository<HinhAnh,Long> {

    List<HinhAnh> findBySanPhamId(Long sanPhamId);
}
