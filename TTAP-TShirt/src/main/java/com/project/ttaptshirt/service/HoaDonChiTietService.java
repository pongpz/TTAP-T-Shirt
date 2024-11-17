package com.project.ttaptshirt.service;

import com.project.ttaptshirt.entity.HoaDonChiTiet;

import java.util.List;

public interface HoaDonChiTietService extends CommonService<HoaDonChiTiet> {
    public List<HoaDonChiTiet> getHDCTByIdHD(Long id);
    public void updateSoLuongHdct(Integer soLuong,Long idHdct);

    List<HoaDonChiTiet> getListHdctByIdHd(Long idHd);
}
