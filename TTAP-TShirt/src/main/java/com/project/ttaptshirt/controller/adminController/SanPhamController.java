package com.project.ttaptshirt.controller.adminController;


import com.project.ttaptshirt.entity.*;
import com.project.ttaptshirt.repository.*;
import com.project.ttaptshirt.security.CustomUserDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Controller
@RequestMapping("/admin/san-pham")
public class SanPhamController {

    public String formatLocalDateTime(LocalDateTime dateTime) {
        Date date = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm dd/MM/yyyy");
        return formatter.format(date);
    }

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
    public String index(Model model, Authentication authentication) {
        System.out.println(authentication);
        if (authentication != null) {
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            User user = customUserDetail.getUser();
            model.addAttribute("userLogged", user);
            System.out.println(user);
        }
        List<SanPham> listSP = sanPhamRepository.findAll();
        model.addAttribute("listSP", listSP);

        List<NSX> listNsx = nsxRepository.findAll();
        model.addAttribute("listNsx", listNsx);

        List<ChatLieu> listChatLieu = chatLieuRepository.findAll();
        model.addAttribute("listChatLieu", listChatLieu);

        List<ThuongHieu> thuongHieu = thuongHieuRepository.findAll();
        model.addAttribute("listThuongHieu", thuongHieu);

        List<KieuDang> kieuDang = kieuDangRepository.findAll();
        model.addAttribute("listKieuDang", kieuDang);

        return "/admin/sanpham/san-pham";
    }

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute("sanpham", new SanPham());
        return "/admin/sanpham/san-pham-new";
    }

    @PostMapping("/add")
    public String add(SanPham sanPham,
                      @RequestParam("idNSX") Long idNsx,
                      @RequestParam("idChatLieu") Long idChatLieu,
                      @RequestParam("idThuongHieu") Long idThuongHieu,
                      @RequestParam("idKieuDang") Long idKieuDang) {

        // Thiết lập các thuộc tính cho sản phẩm
        sanPham.setNsx(nsxRepository.findById(idNsx).orElse(null));
        sanPham.setChatLieu(chatLieuRepository.findById(idChatLieu).orElse(null));
        sanPham.setThuongHieu(thuongHieuRepository.findById(idThuongHieu).orElse(null));
        sanPham.setKieuDang(kieuDangRepository.findById(idKieuDang).orElse(null));

        // Sinh mã duy nhất cho sản phẩm
        sanPham.setMa(generateUniqueCode());

        sanPhamRepository.save(sanPham);
        return "redirect:/admin/san-pham";
    }

    private String generateUniqueCode() {
        String generatedMa;
        do {
            generatedMa = "SP" + generateRandomCode(5);
        } while (sanPhamRepository.existsByMa(generatedMa));
        return generatedMa;
    }

    private String generateRandomCode(int length) {
        String characters = "0123456789";
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            code.append(characters.charAt(random.nextInt(characters.length())));
        }
        return code.toString();
    }


    @GetMapping("sua-san-pham/{id}")
    public String update(@PathVariable("id") Long id, Model model) {
        SanPham sanPham = sanPhamRepository.findById(id).orElse(null);
        model.addAttribute("ssanpham", sanPham);
        model.addAttribute("nsxList", nsxRepository.findAll());
        model.addAttribute("chatLieuList", chatLieuRepository.findAll());
        model.addAttribute("thuongHieuList", thuongHieuRepository.findAll());
        model.addAttribute("kieuDangList", kieuDangRepository.findAll());
        return "/admin/sanpham/sua-san-pham";
    }


    @PostMapping("sua-san-pham/{id}")
    public String updatesp(SanPham sanPham, @PathVariable("id") Long id,
                           @RequestParam("idNSX") Long idNsx,
                           @RequestParam("idChatLieu") Long idChatLieu,
                           @RequestParam("idThuongHieu") Long idThuongHieu,
                           @RequestParam("idKieuDang") Long idKieuDang) {

        SanPham existingSanPham = sanPhamRepository.findById(id).orElse(null);
        if (existingSanPham == null) {
            return "redirect:/admin/san-pham";
        }

        sanPham.setMa(existingSanPham.getMa());

        existingSanPham.setTen(sanPham.getTen());
        existingSanPham.setMoTa(sanPham.getMoTa());
        existingSanPham.setTrangThai(sanPham.getTrangThai());

        existingSanPham.setNsx(nsxRepository.findById(idNsx).orElse(null));
        existingSanPham.setChatLieu(chatLieuRepository.findById(idChatLieu).orElse(null));
        existingSanPham.setThuongHieu(thuongHieuRepository.findById(idThuongHieu).orElse(null));
        existingSanPham.setKieuDang(kieuDangRepository.findById(idKieuDang).orElse(null));

        sanPhamRepository.save(existingSanPham);

        return "redirect:/admin/san-pham";
    }



}
