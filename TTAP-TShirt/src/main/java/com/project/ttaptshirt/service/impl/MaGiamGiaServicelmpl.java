package com.project.ttaptshirt.service.impl;

import com.project.ttaptshirt.entity.KhachHang;
import com.project.ttaptshirt.entity.MaGiamGia;
import com.project.ttaptshirt.repository.KhachHangRepository;
import com.project.ttaptshirt.repository.MaGiamGiaRepo;
import com.project.ttaptshirt.service.MaGiamGiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MaGiamGiaServicelmpl implements MaGiamGiaService {

    @Autowired
    private MaGiamGiaRepo maGiamGiaRepo;

    @Autowired
    private KhachHangRepository khachHangRepo;



    @Override
    public List<MaGiamGia> getMaGiamGia(Long idKh) {
        return null;
    }

//    public DiscountResponse applyDiscount(Long discountCode, GioHang cart, Long customerId)  {
//        // Kiểm tra mã giảm giá có tồn tại không
//        MaGiamGia voucher = maGiamGiaRepo.findById(discountCode).orElse(null);
//
//        // Kiểm tra điều kiện của mã giảm giá (ví dụ: đơn tối thiểu)
//        if (cart.getTongTien() < voucher.getGiaTriToiThieu()) {
//            throw new DiscountNotValidException("Giá trị đơn hàng chưa đủ để áp dụng mã giảm giá");
//        }
//
//        // Kiểm tra số lượng mã giảm giá còn lại
//        KhachHangVoucher khachHangVoucher = khachHangVoucherRepo.findByKhachHangIdAndVoucherId(customerId, voucher.getId())
//                .orElseThrow(() -> new DiscountNotValidException("Không tìm thấy mã giảm giá cho khách hàng"));
//
//        if (khachHangVoucher.getSoLuong() <= 0) {
//            throw new DiscountNotValidException("Mã giảm giá đã hết lượt sử dụng");
//        }
//
//        // Giảm số lượng mã giảm giá
//        khachHangVoucher.setSoLuong(khachHangVoucher.getSoLuong() - 1);
//        khachHangVoucher.setNgaySua(LocalDateTime.now());
//        khachHangVoucherRepo.save(khachHangVoucher);  // Cập nhật số lượng mã giảm giá
//
//        // Tính toán giá trị giảm giá
//        double discountAmount = cart.getTongTien() * (voucher.getGiaTriGiam()/ 100.0);
//        if (discountAmount > voucher.getGiaTriToiDa()) {
//            discountAmount = voucher.getGiaTriToiDa();
//        }
//
//        // Trả về thông tin giảm giá đã tính
//        return new DiscountResponse(voucher.getId(), discountAmount, voucher.getGiaTriToiThieu());
//    }


    @Override
    public void save(MaGiamGia maGiamGia) {

    }

    @Override
    public MaGiamGia findById(Long id) {
        return maGiamGiaRepo.findById(id).orElse(null);
    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public List<MaGiamGia> findAll() {
        return maGiamGiaRepo.findAll();
    }
}


