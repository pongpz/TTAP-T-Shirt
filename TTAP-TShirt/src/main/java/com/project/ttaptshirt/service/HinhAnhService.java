package com.project.ttaptshirt.service;

import com.project.ttaptshirt.entity.HinhAnh;
import com.project.ttaptshirt.repository.HinhAnhRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HinhAnhService {

    @Autowired
    private HinhAnhRepository repoImage;

    public HinhAnh save(HinhAnh hinhAnh){
       return repoImage.save(hinhAnh);
    }

    public List<HinhAnh> findAll(){
        return repoImage.findAll();
    }

    public Optional<HinhAnh> findByid(Long id){
        return repoImage.findById(id);
    }

    public void delete( Long id){
        repoImage.deleteById(id);
    }


}
