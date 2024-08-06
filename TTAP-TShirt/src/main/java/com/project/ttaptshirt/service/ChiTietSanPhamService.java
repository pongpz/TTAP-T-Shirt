package com.project.ttaptshirt.service;

import com.project.ttaptshirt.entity.ChiTietSanPham;

import java.util.List;

public interface ChiTietSanPhamService extends CommonService<ChiTietSanPham>{

    void updateSoLuongCtsp(Integer soLuong,Long id);
}
