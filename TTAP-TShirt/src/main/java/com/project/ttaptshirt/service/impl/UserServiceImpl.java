package com.project.ttaptshirt.service.impl;

import com.project.ttaptshirt.entity.User;
import com.project.ttaptshirt.repository.UserRepo;
import com.project.ttaptshirt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepo userRepo;
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
    public User findUserByUsername(String userName) {
        return userRepo.findByUsername(userName) ;
    }

    @Override
    public void insertDefaultUserRole(Long userId) {
        userRepo.insertUserRole(userId,Long.valueOf(2));
    }
}
