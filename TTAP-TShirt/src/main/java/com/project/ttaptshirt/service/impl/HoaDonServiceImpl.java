package com.project.ttaptshirt.service.impl;

import com.project.ttaptshirt.entity.HoaDon;
import com.project.ttaptshirt.repository.HoaDonRepository;
import com.project.ttaptshirt.service.HoaDonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HoaDonServiceImpl implements HoaDonService {
    @Autowired
    HoaDonRepository hoaDonRepository;
    @Override
    public void save(HoaDon hoaDon) {
        hoaDonRepository.save(hoaDon);
    }

    @Override
    public HoaDon findById(Long id) {
        return hoaDonRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteById(Long id) {
        hoaDonRepository.deleteById(id);
    }

    @Override
    public List<HoaDon> findAll() {
        return hoaDonRepository.findAll();
    }

    @Override
    public void updateTrangThaiHD(int trangThai, Long id) {
         hoaDonRepository.updateHoaDonStatus(trangThai,id);
    }

    @Override
    public List<HoaDon> getListHDChuaThanhToan() {
        return hoaDonRepository.getListHDChuaThanhToan();
    }

    @Override
    public List<HoaDon> getListHDDaThanhToan() {
        return hoaDonRepository.getListHDDaThanhToan();
    }

    @Override
    public void updateTongTien(Long idHd, Double tongTien) {
        hoaDonRepository.updateTongTienHd(idHd,tongTien);
    }
}
