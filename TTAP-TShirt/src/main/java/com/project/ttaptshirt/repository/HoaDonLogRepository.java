package com.project.ttaptshirt.repository;

import com.project.ttaptshirt.entity.HoaDonLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface HoaDonLogRepository extends JpaRepository<HoaDonLog,Long> {
    @Query(value = "select * from hoa_don_log where id_hoa_don = ?1 order by thoi_gian DESC",nativeQuery = true)
    List<HoaDonLog> getListHoaDonLogByIdHd(Long idHd);
}
