package com.project.ttaptshirt.repository;

import com.project.ttaptshirt.entity.HinhAnh;
import com.project.ttaptshirt.entity.SanPham;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HinhAnhRepository extends JpaRepository<HinhAnh,Long> {


    @Query("select h.path FROM HinhAnh h where h.sanPham.id =:sanPhamId")
    List<String> findBySanPhamId(@Param("sanPhamId") Long sanPhamId);

    List<HinhAnh> findBySanPhamIsNull(Sort sort);

    List<HinhAnh> findBySanPham(SanPham sanPham);


}
