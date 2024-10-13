package com.project.ttaptshirt.controller;


import com.project.ttaptshirt.entity.*;
import com.project.ttaptshirt.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/san-pham")
public class SanPhamController {

    @Autowired
    SanPhamRepository sanPhamRepository;
    @Autowired
    NSXRepository nsxRepository;
    @Autowired
    ChatLieuRepository chatLieuRepository;
    @Autowired
    ThuongHieuRepository thuongHieuRepository;
    @Autowired
    KieuDangRepository kieuDangRepository;

    @GetMapping
    public String index(Model model){
        List<SanPham> listSP = sanPhamRepository.findAll();
        model.addAttribute("listSP",listSP);

        List<NSX> listNsx= nsxRepository.findAll();
        model.addAttribute("listNsx",listNsx);

        List<ChatLieu> listChatLieu= chatLieuRepository.findAll();
        model.addAttribute("listChatLieu",listChatLieu);

        List<ThuongHieu> thuongHieu= thuongHieuRepository.findAll();
        model.addAttribute("listThuongHieu",thuongHieu);

        List<KieuDang> kieuDang= kieuDangRepository.findAll();
        model.addAttribute("listKieuDang",kieuDang);

        return "/admin/sanpham/san-pham";
    }

    @GetMapping("/add")
    public String add(Model model){
        model.addAttribute("sanpham", new SanPham());
        return "/admin/sanpham/san-pham-new";
    }

    @PostMapping("/add")
    public String add(SanPham sanPham){
        sanPhamRepository.save(sanPham);
        return "redirect:/admin/san-pham";
    }

}
