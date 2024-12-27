package com.project.ttaptshirt.service;


import com.project.ttaptshirt.entity.DiaChi;
import com.project.ttaptshirt.entity.TaiKhoan;
import com.project.ttaptshirt.repository.DiaChiRepo;
import com.project.ttaptshirt.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DiaChiService {

    @Autowired
    private DiaChiRepo repoDc;

    @Autowired
    private UserRepo userRepo;

    public DiaChi save(DiaChi dc){
        return repoDc.save(dc);
    }

    public List<DiaChi> findAll(){
        return repoDc.findAll();
    }

//    public Optional<DiaChi> findById(Long id){
//        return repoDc.findById(id);
//    }

    public void deleteById(Long id){
        repoDc.deleteById(id);
    }

    public DiaChi taoDiaChi(DiaChi dc, Long userId) {
       TaiKhoan user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        dc.setTaiKhoan(user);
        return repoDc.save(dc);
    }

    public List<DiaChi> findAddressesByUser(Long userId) {
        return repoDc.findByTaiKhoanId(userId);
    }

    public DiaChi findById(Long id) {
        return repoDc.findById(id).orElseThrow(() -> new RuntimeException("Address not found"));
    }

    @Transactional
    public void deleteAddress(Long addressId) {
        // Cập nhật tất cả người dùng có địa chỉ này thành NULL
        userRepo.updateAddressToNull(addressId);

        // Sau đó xóa địa chỉ
        repoDc.deleteById(addressId);
    }


    @Autowired
    private DiaChiRepo diaChiRepository;

    public DiaChi getDiaChiByUserId(Long userId) {
        List<DiaChi> diaChiList = diaChiRepository.findByTaiKhoanId(userId);
        if (diaChiList != null && !diaChiList.isEmpty()) {
            return diaChiList.get(0); // Lấy địa chỉ đầu tiên, có thể thay đổi nếu cần
        }
        return null; // Trả về null nếu không có địa chỉ
    }

}
