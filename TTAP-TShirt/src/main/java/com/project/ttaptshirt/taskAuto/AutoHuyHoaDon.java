package com.project.ttaptshirt.taskAuto;


import com.project.ttaptshirt.entity.ChiTietSanPham;
import com.project.ttaptshirt.entity.SanPham;
import com.project.ttaptshirt.repository.ChiTietSanPhamRepository;
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
    @Autowired
    private ChiTietSanPhamRepository chiTietSanPhamRepository;

    @Scheduled(cron = "0 0 * * * *")
    public void tuDongHuyHoaDonCho(){
        hoaDonService.huyHoaDonCho();
        System.out.println("đã tự động check hóa đơn chờ");
    }

    @Scheduled(cron = "*/2 * * * * *")
    public void doitrangthaisp() {
        List<SanPham> sanPhams = sanPhamRepository.findAll();
        for (SanPham sanPham : sanPhams) {
            // Kiểm tra danh sách chi tiết sản phẩm liên quan
            List<ChiTietSanPham> chiTietSanPhams = chiTietSanPhamRepository.findBySanPhamId(sanPham.getId());

            boolean tatCaNgungBan = chiTietSanPhams.stream().allMatch(ctsp -> ctsp.getTrangThai() == 1); // 2: Ngừng bán
            boolean coSanPhamConSoLuong = chiTietSanPhams.stream().anyMatch(ctsp -> ctsp.getSoLuong() > 0);

            if (tatCaNgungBan) {
                sanPham.setTrangThai(1); // Ngừng bán
            } else if (!coSanPhamConSoLuong) {
                sanPham.setTrangThai(2); // Hết hàng
            } else {
                sanPham.setTrangThai(0); // Đang bán
            }

            sanPhamRepository.save(sanPham);
        }
    }



}
