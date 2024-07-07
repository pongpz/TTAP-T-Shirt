package com.project.ttaptshirt.service;


import com.project.ttaptshirt.entity.ChucVu;
import com.project.ttaptshirt.repository.ChucVuRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChucVuService {

    @Autowired
    private ChucVuRepo repoCv;

    public List<ChucVu> findAll(){
        return repoCv.findAll();
    }

    public ChucVu save(ChucVu cv){
        return repoCv.save(cv);
    }


    public Optional<ChucVu> findById(Long id){
        return repoCv.findById(id);
    }

    public void deleteById(Long id){
        repoCv.deleteById(id);
    }


}
