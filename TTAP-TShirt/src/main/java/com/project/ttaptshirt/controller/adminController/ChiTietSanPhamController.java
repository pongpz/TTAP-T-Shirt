package com.project.ttaptshirt.controller.adminController;


import com.project.ttaptshirt.entity.ChiTietSanPham;
import com.project.ttaptshirt.entity.HinhAnh;
import com.project.ttaptshirt.entity.KichCo;
import com.project.ttaptshirt.entity.MaGiamGia;
import com.project.ttaptshirt.entity.MauSac;
import com.project.ttaptshirt.entity.SanPham;
import com.project.ttaptshirt.repository.ChatLieuRepository;
import com.project.ttaptshirt.repository.ChiTietSanPhamRepository;
import com.project.ttaptshirt.repository.KieuDangRepository;
import com.project.ttaptshirt.repository.NSXRepository;
import com.project.ttaptshirt.repository.SanPhamRepository;
import com.project.ttaptshirt.repository.ThuongHieuRepository;
import com.project.ttaptshirt.service.ChiTietSanPhamService;
//import com.project.ttaptshirt.service.HinhAnhService;
import com.project.ttaptshirt.service.KichCoService;
import com.project.ttaptshirt.service.MauSacService;
import com.project.ttaptshirt.service.SanPhamService;
import com.project.ttaptshirt.service.impl.ChiTietSanPhamServiceImpl;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
    @Autowired
    ChatLieuRepository chatLieuRepository;
    @Autowired
    KieuDangRepository kieuDangRepository;
    @Autowired
    NSXRepository nsxRepository;
    @Autowired
    ThuongHieuRepository thuongHieuRepository;
    @Autowired
    ChiTietSanPhamRepository chiTietSanPhamRepository;
    @Autowired
    SanPhamRepository sanPhamRepository;
    @Autowired
    ChiTietSanPhamServiceImpl chiTietSanPhamServiceImpl;
//    @Autowired
//    HinhAnhService hinhAnhService;

    @GetMapping("{id}")
    public String index(@PathVariable("id") Long id,Model model) {
        SanPham sp = sanPhamService.findById(id);
        List<ChiTietSanPham> listCTSP = chiTietSanPhamRepository.findBySanPhamId(id);
        model.addAttribute("listSP",sp);
        model.addAttribute("listCTSP", listCTSP);
        return "admin/sanpham/chi-tiet-san-pham";
    }

    public String formatCurrency(Double amount) {
        if (amount == null) {
            return "0"; // Giá trị mặc định nếu amount là null
        }
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(amount);
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

    @GetMapping("/add/{id}")
    public String addCTSP(Model model, @PathVariable("id") Long id) {
        SanPham sp = sanPhamService.findById(id);
        List<ChiTietSanPham> listCTSP = chiTietSanPhamRepository.findBySanPhamId(id);
        model.addAttribute("listSP",sp);
        model.addAttribute("listMauSac", mauSacService.findAll());
//        model.addAttribute("listHinhAnh", hinhAnhService.findAll());
        model.addAttribute("listKichCo", kichCoService.findAll());
        return "admin/sanpham/them-chi-tiet-san-pham";
    }

    @Transactional
    @PostMapping("/add")
    public String createNewCTSP(
            @RequestParam("idSanPham") Long idSanPham,
            @RequestParam(value = "selectedVariants", required = false) List<String> selectedVariants,
            @RequestParam Map<String, String> allParams,
            RedirectAttributes redirectAttributes) {

        if (selectedVariants == null || selectedVariants.isEmpty()) {
            return "redirect:/admin/chi-tiet-san-pham/" + idSanPham;
        }

        SanPham sanPham = sanPhamRepository.getReferenceById(idSanPham);
        List<String> duplicateWarnings = new ArrayList<>();

        for (String variant : selectedVariants) {
            String[] variantIds = variant.split("-");
            Long mauSacId = Long.valueOf(variantIds[0]);
            Long kichCoId = Long.valueOf(variantIds[1]);


            MauSac mauSac = mauSacService.findById(mauSacId);
            KichCo kichCo = kichCoService.findById(kichCoId);


            String priceKey = "price_" + mauSacId + "_" + kichCoId;
            String quantityKey = "quantity_" + mauSacId + "_" + kichCoId;

            Double giaBan = Double.valueOf(allParams.getOrDefault(priceKey, "0"));
            Integer soLuong = Integer.valueOf(allParams.getOrDefault(quantityKey, "0"));


            boolean exists = chiTietSanPhamServiceImpl.existsBySanPhamAndMauSacAndKichCo(sanPham, mauSac, kichCo);
            if (exists) {
                duplicateWarnings.add(" Sản Phẩm Chi Tiết Có Màu sắc: " + mauSac.getTen() + " và Kích cỡ: " + kichCo.getTen() + " Đã Tồn Tại!");
                continue;
            }

//            System.out.println(sanPham.getTrangThai());
            ChiTietSanPham newChiTietSanPham = new ChiTietSanPham();
            newChiTietSanPham.setSanPham(sanPham);
            newChiTietSanPham.setTrangThai(sanPham.getTrangThai());
            newChiTietSanPham.setMa(generateUniqueCode());
            newChiTietSanPham.setMauSac(mauSac);
            newChiTietSanPham.setKichCo(kichCo);
            newChiTietSanPham.setGiaBan(giaBan);
            newChiTietSanPham.setSoLuong(soLuong);
            newChiTietSanPham.setTrangThai(0);
            chiTietSanPhamService.save(newChiTietSanPham);
        }

        if (!duplicateWarnings.isEmpty()) {
            redirectAttributes.addFlashAttribute("duplicateWarnings", duplicateWarnings);
        } else {
            redirectAttributes.addFlashAttribute("createSuccess", true);
        }

        redirectAttributes.addAttribute("id", idSanPham);
        return "redirect:/admin/chi-tiet-san-pham/{id}";
    }




    private String generateUniqueCode() {
        String generatedMa;
        do {
            generatedMa = "SPCT" + generateRandomCode(4);
        } while (chiTietSanPhamRepository.existsByMa(generatedMa));
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




    @PostMapping("/update/{idCTSP}")
    public String updateCTSP(
            @PathVariable("idCTSP") Long idCTSP,
            @RequestParam(required = false, value = "idMauSac") Long idMauSac,
            @RequestParam(required = false, value = "idKichCo") Long idKichCo,
            @RequestParam(required = false, value = "soLuong") Integer soLuong,
            @RequestParam(required = false, value = "giaBan") Double giaBan,
            @RequestParam(required = false, value = "trangThai") Integer trangThai,
            RedirectAttributes redirectAttributes,Model model) {

        // Tìm đối tượng ChiTietSanPham hiện tại từ cơ sở dữ liệu
        ChiTietSanPham existingCTSP = chiTietSanPhamService.findById(idCTSP);

        if (existingCTSP == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy Chi Tiết Sản Phẩm!");
            return "redirect:/admin/error";
        }

        // Lấy ID sản phẩm của ChiTietSanPham
        Long idSanPham = existingCTSP.getSanPham().getId();

        // Lấy các giá trị hiện tại
        Long currentIdMauSac = existingCTSP.getMauSac() != null ? existingCTSP.getMauSac().getId() : null;
        Long currentIdKichCo = existingCTSP.getKichCo() != null ? existingCTSP.getKichCo().getId() : null;

        // Kiểm tra nếu màu sắc hoặc kích cỡ thay đổi
        if ((idMauSac != null && !idMauSac.equals(currentIdMauSac)) ||
                (idKichCo != null && !idKichCo.equals(currentIdKichCo))) {

            // Kiểm tra trùng lặp trong cơ sở dữ liệu
            boolean isDuplicate = chiTietSanPhamServiceImpl.existsByMauSacAndKichCoAndSanPhamId(
                    idMauSac, idKichCo, idSanPham);

            if (isDuplicate) {
                // Thêm thông báo lỗi vào RedirectAttributes
                redirectAttributes.addFlashAttribute("error", "Màu sắc và kích cỡ này đã tồn tại cho sản phẩm hiện tại!");
                redirectAttributes.addAttribute("idCTSP", idCTSP);
                redirectAttributes.addFlashAttribute("createWarning", true);
                return "redirect:/admin/chi-tiet-san-pham/detail/{idCTSP}";
            }
        }

        // Cập nhật các giá trị mới nếu không trùng lặp
        if (idMauSac != null) {
            MauSac mauSac = new MauSac();
            mauSac.setId(idMauSac);
            existingCTSP.setMauSac(mauSac);
        }

        if (idKichCo != null) {
            KichCo kichCo = new KichCo();
            kichCo.setId(idKichCo);
            existingCTSP.setKichCo(kichCo);
        }

        if (soLuong != null) {
            existingCTSP.setSoLuong(soLuong);
        }
        if (giaBan != null) {
            existingCTSP.setGiaBan(giaBan);
        }
            // Lưu đối tượng cập nhật
            existingCTSP.setTrangThai(trangThai);
            chiTietSanPhamService.save(existingCTSP);
            // Thêm ID sản phẩm vào RedirectAttributes
            redirectAttributes.addAttribute("id", idSanPham);

            redirectAttributes.addFlashAttribute("updateSuccess", true);
            // Redirect đến trang chi tiết sản phẩm
            return "redirect:/admin/chi-tiet-san-pham/{id}";
    }

    @Scheduled(fixedRate = 1000)
    public void changeStatus() {
        // Lấy danh sách sản phẩm hết hạn
        List<ChiTietSanPham> expiredProducts = chiTietSanPhamRepository.getSPCTHetHan();

        // Duyệt danh sách sản phẩm hết hạn
        if (!expiredProducts.isEmpty()) {
            expiredProducts.forEach(product -> {
                // Nếu trạng thái = 0 và số lượng = 0, chuyển trạng thái thành 2
                if (product.getTrangThai() == 0 && product.getSoLuong() == 0) {
                    product.setTrangThai(2);
                }
            });
            // Lưu tất cả sản phẩm đã cập nhật trạng thái
            chiTietSanPhamRepository.saveAll(expiredProducts);
        }
    }


}