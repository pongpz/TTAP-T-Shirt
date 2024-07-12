package com.project.ttaptshirt.controller;


import com.project.ttaptshirt.entity.ChiTietSanPham;
import com.project.ttaptshirt.entity.KichCo;
import com.project.ttaptshirt.entity.MauSac;
import com.project.ttaptshirt.entity.SanPham;
import com.project.ttaptshirt.service.ChiTietSanPhamService;
import com.project.ttaptshirt.service.KichCoService;
import com.project.ttaptshirt.service.MauSacService;
import com.project.ttaptshirt.service.SanPhamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin/chi-tiet-san-pham")
public class ChiTietSanPhamController {
    @Autowired
    SanPhamService sanPhamService;
    @Autowired
    ChiTietSanPhamService chiTietSanPhamService;
    @Autowired
    MauSacService mauSacService;
    @Autowired
    KichCoService kichCoService;

    @GetMapping("{id}")
    public String index(@PathVariable("id") Long id,Model model) {
        SanPham sp = sanPhamService.findById(id);
        List<ChiTietSanPham> listCTSP = chiTietSanPhamService.findAll();
        model.addAttribute("listSP",sp);
//        model.addAttribute("listSP", sanPhamService.findAll());
        model.addAttribute("listMauSac", mauSacService.findAll());
        model.addAttribute("listKichCo", kichCoService.findAll());
        model.addAttribute("listCTSP", listCTSP);
        return "admin/sanpham/chi-tiet-san-pham";
    }


    @GetMapping("/delete/{idCTSP}")
    public String delete(@PathVariable("idCTSP") Long idCTSP) {
        System.out.println(idCTSP);
        chiTietSanPhamService.deleteById(idCTSP);
        return "redirect:/admin/chi-tiet-san-pham";
    }

    @GetMapping("/detail/{idCTSP}")
    public String openViewCTSPdetail(@PathVariable("idCTSP") Long idCTSP,Model model) {
        System.out.println(idCTSP);
        model.addAttribute("listSP", sanPhamService.findAll());
        model.addAttribute("listMauSac", mauSacService.findAll());
        model.addAttribute("listKichCo", kichCoService.findAll());
        ChiTietSanPham chiTietSanPham = chiTietSanPhamService.findById(idCTSP);
        model.addAttribute("ctsp",chiTietSanPham);
        return "admin/sanpham/ctsp-view-update";
    }
    @PostMapping("/add")
    public String createNewCTSP(@RequestParam("idSanPham") Long idSanPham,
                                @RequestParam("idMauSac") Long idMauSac,
                                @RequestParam("idKichCo") Long idKichCo,
                                ChiTietSanPham chiTietSanPham) {
        SanPham sanPham = new SanPham();
        sanPham.setId(idSanPham);
        MauSac mauSac = new MauSac();
        mauSac.setId(idMauSac);
        KichCo kichCo = new KichCo();
        kichCo.setId(idKichCo);
        chiTietSanPham.setSanPham(sanPham);
        chiTietSanPham.setKichCo(kichCo);
        chiTietSanPham.setMauSac(mauSac);
        chiTietSanPhamService.save(chiTietSanPham);
        return "redirect:/admin/chi-tiet-san-pham";
    }

    @PostMapping("/update/{idCTSP}")
    public String updateCTSP(@RequestParam("idSanPham") Long idSanPham,
                                @RequestParam("idMauSac") Long idMauSac,
                                @RequestParam("idKichCo") Long idKichCo,
                                ChiTietSanPham chiTietSanPham,
                             @PathVariable("idCTSP") Long idCTSP) {
        SanPham sanPham = new SanPham();
        sanPham.setId(idSanPham);
        MauSac mauSac = new MauSac();
        mauSac.setId(idMauSac);
        KichCo kichCo = new KichCo();
        kichCo.setId(idKichCo);
        chiTietSanPham.setSanPham(sanPham);
        chiTietSanPham.setKichCo(kichCo);
        chiTietSanPham.setMauSac(mauSac);
        chiTietSanPham.setId(idCTSP);
        chiTietSanPhamService.save(chiTietSanPham);
        return "redirect:/admin/chi-tiet-san-pham";
    }

}
