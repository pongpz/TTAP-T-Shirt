package com.project.ttaptshirt.controller.adminController;


import com.project.ttaptshirt.entity.*;
import com.project.ttaptshirt.repository.*;
import com.project.ttaptshirt.security.CustomUserDetail;
import com.project.ttaptshirt.service.HinhAnhService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @Autowired
    ChiTietSanPhamRepository chiTietSanPhamRepository;

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

        return "/admin/sanpham/san-pham";
    }


    @GetMapping("/them-san-pham")
    public String themSanPham(Model model){
        List<NSX> listNsx = nsxRepository.findAll();
        model.addAttribute("listNsx", listNsx);

        List<ChatLieu> listChatLieu = chatLieuRepository.findAll();
        model.addAttribute("listChatLieu", listChatLieu);

        List<ThuongHieu> thuongHieu = thuongHieuRepository.findAll();
        model.addAttribute("listThuongHieu", thuongHieu);

        List<KieuDang> kieuDang = kieuDangRepository.findAll();
        model.addAttribute("listKieuDang", kieuDang);

        List<HinhAnh> images = hinhAnhRepository.findBySanPhamIsNull(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("images", images);

        return "/admin/sanpham/them-san-pham";
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
    public String addSanPhamWithImages(SanPham sanPham,
                                       @RequestParam("selectedImages") String selectedImages, // Danh sách ID ảnh
                                       @RequestParam("idNSX") Long idNsx,
                                       @RequestParam("idChatLieu") Long idChatLieu,
                                       @RequestParam("idThuongHieu") Long idThuongHieu,
                                       @RequestParam("idKieuDang") Long idKieuDang,
                                       RedirectAttributes redirectAttributes,
                                       Model model) {

        if (sanPhamRepository.getSanPhamEmpty(sanPham.getTen(), idThuongHieu,idChatLieu,idKieuDang,idKieuDang).size() > 0){
            model.addAttribute("sanPhamEmpty","Sản phẩm đã tồn tại!");
            model.addAttribute("ten",sanPham.getTen());

            List<NSX> listNsx = nsxRepository.findAll();
            model.addAttribute("listNsx", listNsx);

            List<ChatLieu> listChatLieu = chatLieuRepository.findAll();
            model.addAttribute("listChatLieu", listChatLieu);

            List<ThuongHieu> thuongHieu = thuongHieuRepository.findAll();
            model.addAttribute("listThuongHieu", thuongHieu);

            List<KieuDang> kieuDang = kieuDangRepository.findAll();
            model.addAttribute("listKieuDang", kieuDang);

            List<HinhAnh> images = hinhAnhRepository.findBySanPhamIsNull(Sort.by(Sort.Direction.DESC, "id"));
            model.addAttribute("images", images);
            return "/admin/sanpham/them-san-pham";
        }

        // Thiết lập các thuộc tính cho sản phẩm
        sanPham.setNsx(nsxRepository.findById(idNsx).orElse(null));
        sanPham.setChatLieu(chatLieuRepository.findById(idChatLieu).orElse(null));
        sanPham.setThuongHieu(thuongHieuRepository.findById(idThuongHieu).orElse(null));
        sanPham.setKieuDang(kieuDangRepository.findById(idKieuDang).orElse(null));

        // Sinh mã duy nhất cho sản phẩm
        sanPham.setMa(generateUniqueCode());
        sanPham.setTrangThai(0);

        // Lưu sản phẩm
        SanPham savedSanPham = sanPhamRepository.save(sanPham);

        // Xử lý danh sách ID ảnh
        if (selectedImages != null && !selectedImages.isEmpty()) {
            String[] imageIds = selectedImages.split(","); // Tách chuỗi ID ảnh
            for (String id : imageIds) {
                HinhAnh image = hinhAnhRepository.findById(Long.parseLong(id)).orElse(null);
                if (image != null) {
                    image.setSanPham(savedSanPham); // Gắn sản phẩm vào ảnh
                    hinhAnhRepository.save(image);
                }
            }
        }

        redirectAttributes.addFlashAttribute("createSuccess", true);
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

    @Transactional
    @PostMapping("sua-san-pham/{id}")
    public String updatesp(SanPham sanPham, @PathVariable("id") Long id,
                           @RequestParam("idNSX") Long idNsx,
                           @RequestParam("idChatLieu") Long idChatLieu,
                           @RequestParam("idThuongHieu") Long idThuongHieu,
                           @RequestParam("idKieuDang") Long idKieuDang,
                           Model model) {

        SanPham existingSanPham = sanPhamRepository.findById(id).orElse(null);
        if (existingSanPham == null) {
            return "redirect:/admin/san-pham";
        }

        if (sanPhamRepository.getSanPhamUpdateEmpty(sanPham.getTen(),idThuongHieu,idChatLieu,idNsx,idKieuDang,id).size() > 0){
            model.addAttribute("ssanpham", sanPham);
            model.addAttribute("nsxList", nsxRepository.findAll());
            model.addAttribute("chatLieuList", chatLieuRepository.findAll());
            model.addAttribute("thuongHieuList", thuongHieuRepository.findAll());
            model.addAttribute("kieuDangList", kieuDangRepository.findAll());
            model.addAttribute("errorSanPhamUpdateEmpty","Sản phẩm đã tồn tại!");
            return "/admin/sanpham/sua-san-pham";
        }

        sanPham.setMa(existingSanPham.getMa());

        existingSanPham.setTen(sanPham.getTen());
        existingSanPham.setMoTa(sanPham.getMoTa());
        existingSanPham.setTrangThai(sanPham.getTrangThai());
        if(existingSanPham.getTrangThai()==1){
            List<ChiTietSanPham> ls = chiTietSanPhamRepository.getCTSPByIdSP(sanPham.getId());
            if (ls != null){
                for (int i = 0 ; i < ls.size() ; i ++){
                    ChiTietSanPham ctsp = ls.get(i);
                    ctsp.setTrangThai(1);
                    chiTietSanPhamRepository.save(ctsp);
                }
            }
//            System.out.println(ls);
        }
        existingSanPham.setNsx(nsxRepository.findById(idNsx).orElse(null));
        existingSanPham.setChatLieu(chatLieuRepository.findById(idChatLieu).orElse(null));
        existingSanPham.setThuongHieu(thuongHieuRepository.findById(idThuongHieu).orElse(null));
        existingSanPham.setKieuDang(kieuDangRepository.findById(idKieuDang).orElse(null));

        sanPhamRepository.save(existingSanPham);

        return "redirect:/admin/san-pham";
    }


    @PostMapping("/add/nsx")
    public String addNSX(NSX nsx,Model model,RedirectAttributes redirectAttributes) {
        List<NSX> listNsx = nsxRepository.findAll();
        model.addAttribute("listNsx", listNsx);

        List<ChatLieu> listChatLieu = chatLieuRepository.findAll();
        model.addAttribute("listChatLieu", listChatLieu);

        List<ThuongHieu> thuongHieu = thuongHieuRepository.findAll();
        model.addAttribute("listThuongHieu", thuongHieu);

        List<KieuDang> kieuDang = kieuDangRepository.findAll();
        model.addAttribute("listKieuDang", kieuDang);

        List<HinhAnh> images = hinhAnhRepository.findBySanPhamIsNull(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("images", images);
        NSX NSXCheck = nsxRepository.getNSXByTen(nsx.getTen());
        if (NSXCheck == null){
            nsxRepository.save(nsx);
            redirectAttributes.addFlashAttribute("addNSXSuccess",true);
            return "redirect:/admin/san-pham/them-san-pham";
        }else if (nsx.getTen().isEmpty()){
            model.addAttribute("errorNSX","Không được bỏ trống trường này!");
            model.addAttribute("showModalnsx",true);
            return "admin/sanpham/them-san-pham";
        }
        else {
            model.addAttribute("errorNSX","Nhà sản xuất này đã tồn tại!");
            model.addAttribute("showModalnsx",true);
            return "admin/sanpham/them-san-pham";
        }
    }

    @PostMapping("/add/chat-lieu")
    public String addChatLieu(ChatLieu cl,Model model,RedirectAttributes redirectAttributes){
        List<NSX> listNsx = nsxRepository.findAll();
        model.addAttribute("listNsx", listNsx);

        List<ChatLieu> listChatLieu = chatLieuRepository.findAll();
        model.addAttribute("listChatLieu", listChatLieu);

        List<ThuongHieu> thuongHieu = thuongHieuRepository.findAll();
        model.addAttribute("listThuongHieu", thuongHieu);

        List<KieuDang> kieuDang = kieuDangRepository.findAll();
        model.addAttribute("listKieuDang", kieuDang);

        List<HinhAnh> images = hinhAnhRepository.findBySanPhamIsNull(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("images", images);
        ChatLieu ClCheck = chatLieuRepository.getChatLieuByTen(cl.getTen());
        if (ClCheck == null){
            redirectAttributes.addFlashAttribute("addNSXSuccess",true);
            chatLieuRepository.save(cl);
            return "redirect:/admin/san-pham/them-san-pham";
        }else if (cl.getTen().isEmpty()){
            model.addAttribute("errorCl","Không được bỏ trống trường này!");
            model.addAttribute("showModalCl",true);
            return "admin/sanpham/them-san-pham";
        }
        else {
            model.addAttribute("errorCl","Chất liệu này đã tồn tại!");
            model.addAttribute("showModalCl",true);
            return "admin/sanpham/them-san-pham";
        }
    }

    @PostMapping("/add/thuong-hieu")
    public String addThuongHieu(ThuongHieu th,Model model,RedirectAttributes redirectAttributes){
        List<NSX> listNsx = nsxRepository.findAll();
        model.addAttribute("listNsx", listNsx);

        List<ChatLieu> listChatLieu = chatLieuRepository.findAll();
        model.addAttribute("listChatLieu", listChatLieu);

        List<ThuongHieu> thuongHieu = thuongHieuRepository.findAll();
        model.addAttribute("listThuongHieu", thuongHieu);

        List<KieuDang> kieuDang = kieuDangRepository.findAll();
        model.addAttribute("listKieuDang", kieuDang);

        List<HinhAnh> images = hinhAnhRepository.findBySanPhamIsNull(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("images", images);
        ThuongHieu ThCheck = thuongHieuRepository.getThuongHieuByTen(th.getTen());
        if (ThCheck == null){
            redirectAttributes.addFlashAttribute("addNSXSuccess",true);
            thuongHieuRepository.save(th);
            return "redirect:/admin/san-pham/them-san-pham";
        }else if (th.getTen().isEmpty()){
            model.addAttribute("errorTh","Không được bỏ trống trường này!");
            model.addAttribute("showModalTh",true);
            return "admin/sanpham/them-san-pham";
        }
        else {
            model.addAttribute("errorTh","Thương hiệu này đã tồn tại!");
            model.addAttribute("showModalTh",true);
            return "admin/sanpham/them-san-pham";
        }
    }

    @PostMapping("/add/kieu-dang")
    public String addKieuDang(KieuDang kieuDang2,Model model,RedirectAttributes redirectAttributes){
        List<NSX> listNsx = nsxRepository.findAll();
        model.addAttribute("listNsx", listNsx);

        List<ChatLieu> listChatLieu = chatLieuRepository.findAll();
        model.addAttribute("listChatLieu", listChatLieu);

        List<ThuongHieu> thuongHieu = thuongHieuRepository.findAll();
        model.addAttribute("listThuongHieu", thuongHieu);

        List<KieuDang> kieuDang = kieuDangRepository.findAll();
        model.addAttribute("listKieuDang", kieuDang);

        List<HinhAnh> images = hinhAnhRepository.findBySanPhamIsNull(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("images", images);
        KieuDang CdCheck = kieuDangRepository.getKieuDangByTen(kieuDang2.getTen());
        if (CdCheck == null){
            redirectAttributes.addFlashAttribute("addNSXSuccess",true);
            kieuDangRepository.save(kieuDang2);
            return "redirect:/admin/san-pham/them-san-pham";
        }else if (kieuDang2.getTen().isEmpty()){
            model.addAttribute("errorKd","Không được bỏ trống trường này!");
            model.addAttribute("showModalKd",true);
            return "admin/sanpham/them-san-pham";
        }
        else {
            model.addAttribute("errorKd","Kiểu dáng này đã tồn tại!");
            model.addAttribute("showModalKd",true);
            return "admin/sanpham/them-san-pham";
        }
    }
}
