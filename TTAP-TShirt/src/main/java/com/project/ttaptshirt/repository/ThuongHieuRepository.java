
package com.project.ttaptshirt.repository;


import com.project.ttaptshirt.entity.ChatLieu;
import com.project.ttaptshirt.entity.ThuongHieu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ThuongHieuRepository extends JpaRepository<ThuongHieu, Long> {
    @Query("select cl from ThuongHieu cl where cl.ten =:ten")
    ThuongHieu getThuongHieuByTen(String ten);
}
