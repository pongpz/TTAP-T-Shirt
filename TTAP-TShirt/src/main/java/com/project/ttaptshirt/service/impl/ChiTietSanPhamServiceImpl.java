package com.project.ttaptshirt.service.impl;

import com.project.ttaptshirt.entity.ChiTietSanPham;
import com.project.ttaptshirt.entity.KichCo;
import com.project.ttaptshirt.entity.MauSac;
import com.project.ttaptshirt.entity.SanPham;
import com.project.ttaptshirt.repository.ChiTietSanPhamRepository;
import com.project.ttaptshirt.service.ChiTietSanPhamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChiTietSanPhamServiceImpl implements ChiTietSanPhamService {
    @Autowired
    ChiTietSanPhamRepository chiTietSanPhamRepository;

    //lấy giá sản phẩm thấp nhất để hiển thị
    public Double getMinGiaBan(Long sanPhamId) {
        return chiTietSanPhamRepository.findMinGiaBan(sanPhamId);
    }

    @Override
    public void save(ChiTietSanPham chiTietSanPham) {
        chiTietSanPhamRepository.save(chiTietSanPham);
    }

    @Override
    public ChiTietSanPham findById(Long id) {
        return chiTietSanPhamRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteById(Long id) {
        chiTietSanPhamRepository.deleteById(id);
    }

    @Override
    public List<ChiTietSanPham> findAll() {
        return chiTietSanPhamRepository.findAll();
    }

    @Override
    public void updateSoLuongCtsp(Integer soLuong, Long id) {
        chiTietSanPhamRepository.updateSoLuongCTSP(soLuong,id);
    }

    public boolean existsBySanPhamAndMauSacAndKichCo(SanPham sanPham, MauSac mauSac, KichCo kichCo) {
        return chiTietSanPhamRepository.existsBySanPhamAndMauSacAndKichCo(sanPham, mauSac, kichCo);
    }

    public boolean existsByMauSacAndKichCoAndSanPhamId(Long idMauSac, Long idKichCo, Long sanPhamId) {
        return chiTietSanPhamRepository.existsByMauSacIdAndKichCoIdAndSanPhamId(idMauSac, idKichCo, sanPhamId);
    }

    public List<ChiTietSanPham> getChiTietSanPhamBySanPhamAndMauSac(Long sanPhamId, Long mauSacId) {
        return chiTietSanPhamRepository.findBySanPhamAndAndMauSac(sanPhamId, mauSacId);
    }
    public Optional<ChiTietSanPham> getsolg(Long sanPhamId, Long mauSacId, Long kichCoId) {
        return chiTietSanPhamRepository.findsoluong(sanPhamId, mauSacId,kichCoId);
    }
}
