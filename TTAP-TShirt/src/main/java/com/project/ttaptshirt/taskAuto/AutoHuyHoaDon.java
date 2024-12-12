package com.project.ttaptshirt.taskAuto;


import com.project.ttaptshirt.entity.SanPham;
import com.project.ttaptshirt.repository.SanPhamRepository;
import com.project.ttaptshirt.service.impl.HoaDonServiceImpl;
import com.project.ttaptshirt.service.impl.SanPhamServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AutoHuyHoaDon {

    @Autowired
    HoaDonServiceImpl hoaDonService;

    @Autowired
    SanPhamServiceImpl sanPhamService;
    @Autowired
    private SanPhamRepository sanPhamRepository;

    @Scheduled(cron = "0 0 * * * *")
    public void tuDongHuyHoaDonCho(){
        hoaDonService.huyHoaDonCho();
        System.out.println("đã tự động check hóa đơn chờ");
    }

    @Scheduled(cron = "*/2 * * * * *")
    public void doitrangthaisp() {
        List<SanPham> sanPhams = sanPhamRepository.findAll();
        for (SanPham sanPham : sanPhams) {
            if (sanPham.tongSoLuong() == 0) {
                // Nếu sản phẩm không có số lượng và không phải đang ngừng bán, chuyển sang trạng thái hết hàng
                if (sanPham.getTrangThai() != 1) {
                    sanPham.setTrangThai(2); // Hết hàng
                }
            } else {
                // Nếu sản phẩm còn số lượng, chuyển về trạng thái đang bán
                sanPham.setTrangThai(0); // Đang bán
            }
            if (sanPham.tongSoLuong()>0){
                if (sanPham.getTrangThai() != 1) {
                    sanPham.setTrangThai(0);
                }
            }
            sanPhamRepository.save(sanPham);
        }
    }


}
