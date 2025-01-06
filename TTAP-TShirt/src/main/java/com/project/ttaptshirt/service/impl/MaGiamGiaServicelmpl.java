package com.project.ttaptshirt.service.impl;

import com.project.ttaptshirt.dto.DiscountResponse;
import com.project.ttaptshirt.entity.GioHang;
import com.project.ttaptshirt.entity.KhachHang;
import com.project.ttaptshirt.entity.KhachHangVoucher;
import com.project.ttaptshirt.entity.MaGiamGia;
import com.project.ttaptshirt.repository.KhachHangRepository;
import com.project.ttaptshirt.repository.KhachHangVoucherRepository;
import com.project.ttaptshirt.repository.MaGiamGiaRepo;
import com.project.ttaptshirt.service.MaGiamGiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MaGiamGiaServicelmpl implements MaGiamGiaService {

    @Autowired
    private MaGiamGiaRepo maGiamGiaRepo;

    @Autowired
    private KhachHangRepository khachHangRepo;

    @Autowired
    private KhachHangVoucherRepository  khachHangVoucherRepo;

    @Override
    public List<MaGiamGia> getMaGiamGia(Long idKh) {
        return null;
    }

    @Override
    public MaGiamGia validateAndGetVoucher(Long voucherId, Long customerId) {
        // Tìm mã giảm giá theo ID
        MaGiamGia voucher = maGiamGiaRepo.findById(voucherId).orElse(null);

        // Kiểm tra mã giảm giá có tồn tại không
        if (voucher == null) {
            throw new IllegalArgumentException("Mã giảm giá không tồn tại.");
        }

        // Kiểm tra mã giảm giá có thuộc khách hàng không (nếu mã giảm giá liên kết với khách hàng)
        if (!voucher.getDanhSachKhachHangVoucher().stream().equals(customerId)) {
            throw new IllegalArgumentException("Mã giảm giá này không thuộc về bạn.");
        }

        // Kiểm tra mã giảm giá còn hiệu lực
        if (voucher.getSoLuong() <= 0) {
            throw new IllegalArgumentException("Mã giảm giá đã hết.");
        }

        if (voucher.getNgayKetThuc() != null && voucher.getNgayKetThuc().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Mã giảm giá đã hết hạn.");
        }

        return voucher;
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
    public void themVoucherChoKhachHang(Long voucherId, Long customerId, Integer quantity) {
        MaGiamGia voucher = maGiamGiaRepo.findById(voucherId).orElseThrow(() -> new RuntimeException("Voucher không tồn tại"));
        if (voucher.getSoLuong() < quantity) {
            throw new RuntimeException("Không đủ số lượng mã giảm giá");
        }

        KhachHang khachHang = khachHangRepo.findById(customerId).orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại"));

        // Tạo đối tượng KhachHangVoucher và lưu vào cơ sở dữ liệu
        KhachHangVoucher khachHangVoucher = new KhachHangVoucher();
        khachHangVoucher.setKhachHang(khachHang);
        khachHangVoucher.setMaGiamGia(voucher);
        khachHangVoucher.setSoLuong(quantity);

        khachHangVoucherRepo.save(khachHangVoucher);

        // Cập nhật lại số lượng voucher còn lại
        voucher.setSoLuong(voucher.getSoLuong() - quantity);
        maGiamGiaRepo.save(voucher);
    }


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
        return List.of();
    }
}


