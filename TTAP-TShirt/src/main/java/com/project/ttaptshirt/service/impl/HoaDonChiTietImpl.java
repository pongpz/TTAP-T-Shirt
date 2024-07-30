package com.project.ttaptshirt.service.impl;

import com.project.ttaptshirt.entity.HoaDonChiTiet;
import com.project.ttaptshirt.repository.HoaDonChiTietRepository;
import com.project.ttaptshirt.service.HoaDonChiTietService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class HoaDonChiTietImpl implements HoaDonChiTietService {

    @Autowired
    HoaDonChiTietRepository hoaDonChiTietRepository;
    @Override
    public void save(HoaDonChiTiet hoaDonChiTiet) {
        hoaDonChiTietRepository.save(hoaDonChiTiet);
    }

    @Override
    public HoaDonChiTiet findById(Long id) {
        return hoaDonChiTietRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteById(Long id) {
        hoaDonChiTietRepository.deleteById(id);
    }

    @Override
    public List<HoaDonChiTiet> findAll() {
        return hoaDonChiTietRepository.findAll();
    }

    @Override
    public List<HoaDonChiTiet> getHDCTByIdHD(Long id) {
        return hoaDonChiTietRepository.getHoaDonChiTietByHoaDonId(id);
    }

    @Override
    public void updateSoLuongHdct(Integer soLuong, Long idHdct) {
        hoaDonChiTietRepository.updateSoLuongSpHdct(soLuong,idHdct);
    }
}
