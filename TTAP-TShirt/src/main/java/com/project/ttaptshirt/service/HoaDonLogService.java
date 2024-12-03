package com.project.ttaptshirt.service;

import com.project.ttaptshirt.entity.HoaDonLog;

import java.util.List;

public interface HoaDonLogService extends CommonService<HoaDonLog> {

    List<HoaDonLog> getListHoaDonLogByIdHd(Long idHd);
}
