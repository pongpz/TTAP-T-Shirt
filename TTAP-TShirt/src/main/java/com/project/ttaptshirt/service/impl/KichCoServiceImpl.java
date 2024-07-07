package com.project.ttaptshirt.service.impl;

import com.project.ttaptshirt.entity.KichCo;
import com.project.ttaptshirt.repository.KichCoRepository;
import com.project.ttaptshirt.service.KichCoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class KichCoServiceImpl implements KichCoService {
    @Autowired
    KichCoRepository kichCoRepository;
    @Override
    public void save(KichCo kichCo) {
        kichCoRepository.save(kichCo);
    }

    @Override
    public KichCo findById(Long id) {
        return kichCoRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteById(Long id) {
    kichCoRepository.deleteById(id);
    }

    @Override
    public List<KichCo> findAll() {
        return kichCoRepository.findAll();
    }
}
