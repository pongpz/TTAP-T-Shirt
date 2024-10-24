package com.project.ttaptshirt.service;

import com.project.ttaptshirt.entity.DiaChi;
import com.project.ttaptshirt.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService extends CommonService<User> {

    Page<User> findAll(int page, int size);

    Page<User> findAll(Pageable pageable);

    User findUserByUsername(String userName);

    void insertDefaultUserRole(Long userId);


    User updateDiachi(Long userId, DiaChi diaChi);
}
