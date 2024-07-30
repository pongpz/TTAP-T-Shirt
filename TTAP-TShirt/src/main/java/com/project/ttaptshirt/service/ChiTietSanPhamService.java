package com.project.ttaptshirt.service;

import com.project.ttaptshirt.entity.ChiTietSanPham;

public interface ChiTietSanPhamService extends CommonService<ChiTietSanPham>{

    void updateSoLuongCtsp(Integer soLuong,Long id);
}
