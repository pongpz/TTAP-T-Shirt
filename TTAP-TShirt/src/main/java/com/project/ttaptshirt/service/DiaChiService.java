package com.project.ttaptshirt.service;


import com.project.ttaptshirt.entity.DiaChi;
import com.project.ttaptshirt.repository.DiaChiRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DiaChiService {

    @Autowired
    private DiaChiRepo repoDc;

    public DiaChi save(DiaChi dc){
        return repoDc.save(dc);
    }

    public List<DiaChi> findAll(){
        return repoDc.findAll();
    }

    public Optional<DiaChi> findById(Long id){
        return repoDc.findById(id);
    }

    public void deleteById(Long id){
        repoDc.deleteById(id);
    }

}
