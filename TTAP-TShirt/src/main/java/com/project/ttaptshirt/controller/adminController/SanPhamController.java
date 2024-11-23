package com.project.ttaptshirt.controller.adminController;


import com.project.ttaptshirt.entity.*;
import com.project.ttaptshirt.repository.*;
import com.project.ttaptshirt.security.CustomUserDetail;
import com.project.ttaptshirt.service.HinhAnhService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
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

    @Autowired
    HinhAnhService hinhAnhService;

    @Autowired
    HinhAnhRepository hinhAnhRepository;

    @GetMapping
    public String index(Model model, Authentication authentication, @RequestParam(defaultValue = "0") int page) {
        System.out.println(authentication);
        if (authentication != null) {
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            User user = customUserDetail.getUser();
            model.addAttribute("userLogged", user);
            System.out.println(user);
        }

        Pageable pageable = PageRequest.of(page, 6);
        Page<SanPham> listSP = sanPhamRepository.findAllByOrderByNgayTaoDesc(pageable);
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

    @GetMapping("/tim-kiem")
    public String timKiem(@RequestParam("ten") String ten, Model model, @RequestParam(defaultValue = "0") int page){

        Pageable pageable = PageRequest.of(page, 6);
        Page<SanPham> timSp = ten.isEmpty()
                ? sanPhamRepository.findAllByOrderByNgayTaoDesc(pageable)
                : sanPhamRepository.findByTenContaining(ten, pageable);

        model.addAttribute("listSP", timSp);
        model.addAttribute("ten",ten);

        List<NSX> listNsx = nsxRepository.findAll();
        model.addAttribute("listNsx", listNsx);

        List<ChatLieu> listChatLieu = chatLieuRepository.findAll();
        model.addAttribute("listChatLieu", listChatLieu);

        List<ThuongHieu> listThuongHieu = thuongHieuRepository.findAll();
        model.addAttribute("listThuongHieu", listThuongHieu);

        List<KieuDang> listKieuDang = kieuDangRepository.findAll();
        model.addAttribute("listKieuDang", listKieuDang);

        return "/admin/sanpham/san-pham";
    }

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute("sanpham", new SanPham());
        return "/admin/sanpham/san-pham-new";
    }

    @PostMapping("/add")
    public String add(SanPham sanPham,
                      @RequestParam("image") MultipartFile file,
                      @RequestParam("idNSX") Long idNsx,
                      @RequestParam("idChatLieu") Long idChatLieu,
                      @RequestParam("idThuongHieu") Long idThuongHieu,
                      @RequestParam("idKieuDang") Long idKieuDang) throws IOException, GeneralSecurityException {

        // Thiết lập các thuộc tính cho sản phẩm
        sanPham.setNsx(nsxRepository.findById(idNsx).orElse(null));
        sanPham.setChatLieu(chatLieuRepository.findById(idChatLieu).orElse(null));
        sanPham.setThuongHieu(thuongHieuRepository.findById(idThuongHieu).orElse(null));
        sanPham.setKieuDang(kieuDangRepository.findById(idKieuDang).orElse(null));

        // Sinh mã duy nhất cho sản phẩm
        sanPham.setMa(generateUniqueCode());

        SanPham sanPham1 = sanPhamRepository.save(sanPham);

        // Tải ảnh lên Cloudinary
        String imageUrl = hinhAnhService.uploadFile(file); // Gọi phương thức uploadFile để lấy URL ảnh

        // Tạo và lưu thông tin hình ảnh vào cơ sở dữ liệu
        HinhAnh hinhAnh = new HinhAnh();
        hinhAnh.setSanPham(sanPham1);
        hinhAnh.setPath(imageUrl); // Lưu đường dẫn URL ảnh từ Cloudinary
        hinhAnh.setTrangThai(1);

        hinhAnhRepository.save(hinhAnh);

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
