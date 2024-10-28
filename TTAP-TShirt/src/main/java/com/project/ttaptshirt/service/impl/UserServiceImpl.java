package com.project.ttaptshirt.service.impl;

import com.project.ttaptshirt.entity.DiaChi;
import com.project.ttaptshirt.entity.User;
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
    public void save(User user) {
        userRepo.save(user);
    }

    @Override
    public User findById(Long id) {
        return userRepo.findById(id).orElse(null);

    }

    @Override
    public void deleteById(Long id) {
        userRepo.deleteById(id);
    }

    @Override
    public List<User> findAll() {
        return userRepo.findAll();
    }

    @Override
    public Page<User> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending()); // Sắp xếp theo `id` giảm dần
        return userRepo.findAll(pageable);
    }

    @Override
    public Page<User> findAll(Pageable pageable){
        return userRepo.findAll(pageable);
    }

    @Override
    public User findUserByUsername(String userName) {
        return userRepo.findByUsername(userName) ;
    }

    @Override
    public void insertDefaultUserRole(Long userId) {
        userRepo.insertUserRole(userId,Long.valueOf(2));
    }

    @Override
    public User updateDiachi(Long userId, DiaChi diaChi){
        Optional<User> userOptional =userRepo.findById(userId);
        if (userOptional.isPresent()){
            User user = userOptional.get();
            diaChiRepo.save(diaChi);
            user.setDc(diaChi);
            return userRepo.save(user);
        }else {
            throw new RuntimeException("User not found with id " + userId);
        }
    }
}
