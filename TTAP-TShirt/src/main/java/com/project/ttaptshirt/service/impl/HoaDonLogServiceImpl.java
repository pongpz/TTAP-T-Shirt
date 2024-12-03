package com.project.ttaptshirt.service.impl;

import com.project.ttaptshirt.entity.HoaDonLog;
import com.project.ttaptshirt.repository.HoaDonLogRepository;
import com.project.ttaptshirt.service.HoaDonLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class HoaDonLogServiceImpl implements HoaDonLogService {

    @Autowired
    HoaDonLogRepository hoaDonLogRepository;
    @Override
    public void save(HoaDonLog hoaDonLog) {
        hoaDonLogRepository.save(hoaDonLog);
    }

    @Override
    public HoaDonLog findById(Long id) {
        return hoaDonLogRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteById(Long id) {
        hoaDonLogRepository.deleteById(id);
    }

    @Override
    public List<HoaDonLog> findAll() {
        return hoaDonLogRepository.findAll();
    }

    @Override
    public List<HoaDonLog> getListHoaDonLogByIdHd(Long idHd) {
        return hoaDonLogRepository.getListHoaDonLogByIdHd(idHd);
    }
}
