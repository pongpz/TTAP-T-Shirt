package com.project.ttaptshirt.service.impl;

import com.project.ttaptshirt.entity.DiaChi;
import com.project.ttaptshirt.entity.TaiKhoan;
import com.project.ttaptshirt.repository.DiaChiRepo;
import com.project.ttaptshirt.repository.UserRepo;
import com.project.ttaptshirt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepo userRepo;

    @Autowired
    DiaChiRepo diaChiRepo;

    @Override
    public void save(TaiKhoan user) {
        userRepo.save(user);
    }

    @Override
    public TaiKhoan findById(Long id) {
        return userRepo.findById(id).orElse(null);

    }

    @Override
    public void deleteById(Long id) {
        userRepo.deleteById(id);
    }

    @Override
    public List<TaiKhoan> findAll() {
        return userRepo.findAll();
    }

    @Override
    public Page<TaiKhoan> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending()); // Sắp xếp theo `id` giảm dần
        return userRepo.findAll(pageable);
    }

    @Override
    public Page<TaiKhoan> findAll(Pageable pageable){
        return userRepo.findAll(pageable);
    }

    @Override
    public Page<TaiKhoan> findAllNv(String rolename, Pageable pageable) {
        return userRepo.findUsersByRoleName(rolename, pageable);
    }


    @Override
    public TaiKhoan findUserByUsername(String userName) {
        return userRepo.findByUsername(userName) ;
    }





    @Override
    public TaiKhoan updateDiachi(Long userId, DiaChi diaChi) {
        // Tìm người dùng theo ID
        Optional<TaiKhoan> userOptional = userRepo.findById(userId);
        if (userOptional.isPresent()) {
            TaiKhoan user = userOptional.get();

            // Thiết lập liên kết giữa DiaChi và User
            diaChi.setTaiKhoan(user);
            diaChiRepo.save(diaChi);

            // Thêm địa chỉ mới vào danh sách DiaChi của User
            user.getDiaChiList().add(diaChi);

            // Lưu thông tin người dùng
            return userRepo.save(user);
        } else {
            throw new RuntimeException("User not found with id " + userId);
        }
    }

//    @Override
//    public Optional<TaiKhoan> findBySdt(String sdt) {
//        return userRepo.findBySoDienthoai(sdt);
//    }

//    @Override
//    public Page<TaiKhoan> searchByPhoneNumber(String phoneNumber, Pageable pageable) {
//        return userRepo.findBySoDienthoaiContaining(phoneNumber, pageable);
//    }
}
