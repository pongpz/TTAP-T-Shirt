package com.project.ttaptshirt.service;

import com.project.ttaptshirt.entity.DiaChi;
import com.project.ttaptshirt.entity.TaiKhoan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserService extends CommonService<TaiKhoan> {

    Page<TaiKhoan> findAll(int page, int size);

    Page<TaiKhoan> findAll(Pageable pageable);

    Page<TaiKhoan> findAllNv(String rolename, Pageable pageable);

    TaiKhoan findUserByUsername(String userName);



    TaiKhoan updateDiachi(Long userId, DiaChi diaChi);

//    Optional<TaiKhoan> findBySdt(String sdt);

//    Page<TaiKhoan> searchByPhoneNumber(String phoneNumber, Pageable pageable);

}
