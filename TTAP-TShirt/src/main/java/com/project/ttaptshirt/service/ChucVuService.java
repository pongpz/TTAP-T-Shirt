package com.project.ttaptshirt.service;


import com.project.ttaptshirt.entity.Role;
import com.project.ttaptshirt.repository.ChucVuRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChucVuService {

    @Autowired
    private ChucVuRepo repoCv;

    public List<Role> findAll(){
        return repoCv.findAll();
    }

    public Role save(Role cv){
        return repoCv.save(cv);
    }


    public Optional<Role> findById(Long id){
        return repoCv.findById(id);
    }

    public void deleteById(Long id){
        repoCv.deleteById(id);
    }


}
