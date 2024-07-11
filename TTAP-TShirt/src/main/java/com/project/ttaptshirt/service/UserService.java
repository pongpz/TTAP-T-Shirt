package com.project.ttaptshirt.service;


import com.project.ttaptshirt.entity.DiaChi;
import com.project.ttaptshirt.entity.User;
import com.project.ttaptshirt.repository.DiaChiRepo;
import com.project.ttaptshirt.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepo repoUser;

    @Autowired
    private DiaChiRepo repoDc;

    public User save (User user){
        return repoUser.save(user);
    }

    public List<User> findAll(){
        return repoUser.findAll();
    }

    public Optional<User> findById(Long id){
        return repoUser.findById(id);
    }

    public void deleteById(Long id){
        repoUser.deleteById(id);
    }

    public User updateDiachi(Long UserId, DiaChi diaChi){
        Optional<User> userOptional =repoUser.findById(UserId);
        if (userOptional.isPresent()){
            User user = userOptional.get();
            repoDc.save(diaChi);
            user.setDc(diaChi);
            return repoUser.save(user);
        }else {
            throw new RuntimeException("User not found with id " + UserId);
        }
    }
}
