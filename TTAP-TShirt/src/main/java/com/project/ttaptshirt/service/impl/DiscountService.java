package com.project.ttaptshirt.service.impl;

import com.project.ttaptshirt.entity.MaGiamGia;
import com.project.ttaptshirt.repository.MaGiamGiaRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class DiscountService {
    @Autowired
    MaGiamGiaRepo maGiamGiaRepo;

    public MaGiamGiaRepo getMaGiamGiaRepo() {
        return maGiamGiaRepo;
    }

    public MaGiamGia getMaGiamGia(String code) {
        Optional<MaGiamGia> discountOptional = maGiamGiaRepo.findByCode(code);

        // Nếu mã giảm giá tồn tại, kiểm tra tính hợp lệ
        if (discountOptional.isPresent()) {
            MaGiamGia discount = discountOptional.get();

            // Kiểm tra xem mã giảm giá có còn hiệu lực không (chưa hết hạn và còn hoạt động)
            if (discount.getTrangThai() && (discount.getNgayBatDau() == null || discount.getNgayBatDau().isAfter(LocalDateTime.now()))) {
                return discount;
            }
        }

        return null;
    }
}
