package com.project.ttaptshirt.service;

import com.project.ttaptshirt.entity.ChiTietSanPham;
import com.project.ttaptshirt.entity.KichCo;
import com.project.ttaptshirt.entity.MauSac;
import com.project.ttaptshirt.entity.SanPham;

import java.util.List;

public interface ChiTietSanPhamService extends CommonService<ChiTietSanPham>{

    void updateSoLuongCtsp(Integer soLuong,Long id);

}
