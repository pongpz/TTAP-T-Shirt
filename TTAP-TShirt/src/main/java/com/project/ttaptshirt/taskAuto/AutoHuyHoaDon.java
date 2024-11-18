package com.project.ttaptshirt.taskAuto;


import com.project.ttaptshirt.service.impl.HoaDonServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AutoHuyHoaDon {

    @Autowired
    HoaDonServiceImpl hoaDonService;

    @Scheduled(cron = "0 0 * * * *")
    public void tuDongHuyHoaDonCho(){
        hoaDonService.huyHoaDonCho();
        System.out.println("đã chạy check tự động hủy hóa đơn chờ");
    }
}
