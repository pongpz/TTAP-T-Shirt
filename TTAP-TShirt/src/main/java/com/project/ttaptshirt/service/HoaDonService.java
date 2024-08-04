package com.project.ttaptshirt.service;

import com.project.ttaptshirt.entity.HoaDon;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface HoaDonService extends CommonService<HoaDon> {

    @Transactional
    public void updateTrangThaiHD(int trangThai,Long id);

    public List<HoaDon> getListHDChuaThanhToan();
}
