package com.project.ttaptshirt.service.impl;

import com.project.ttaptshirt.entity.MauSac;
import com.project.ttaptshirt.repository.MauSacRepository;
import com.project.ttaptshirt.service.MauSacService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MauSacServiceImpl implements MauSacService {
    @Autowired
    MauSacRepository mauSacRepository;
    @Override
    public void save(MauSac mauSac) {
        mauSacRepository.save(mauSac);
    }

    @Override
    public MauSac findById(Long id) {
        return mauSacRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteById(Long id) {
        mauSacRepository.deleteById(id);
    }

    @Override
    public List<MauSac> findAll() {
        return mauSacRepository.findAll();
    }
}
