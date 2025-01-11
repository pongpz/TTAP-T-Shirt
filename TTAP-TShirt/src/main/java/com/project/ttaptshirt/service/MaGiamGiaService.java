package com.project.ttaptshirt.service;

import com.project.ttaptshirt.entity.MaGiamGia;

import java.util.List;

public interface MaGiamGiaService extends CommonService<MaGiamGia> {
    List<MaGiamGia> getMaGiamGia(Long idKh);

}
