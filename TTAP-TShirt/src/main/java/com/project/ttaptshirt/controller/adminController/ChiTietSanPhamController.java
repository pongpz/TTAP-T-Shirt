package com.project.ttaptshirt.controller.adminController;


import com.project.ttaptshirt.entity.ChiTietSanPham;
import com.project.ttaptshirt.entity.HinhAnh;
import com.project.ttaptshirt.entity.KichCo;
import com.project.ttaptshirt.entity.MauSac;
import com.project.ttaptshirt.entity.SanPham;
import com.project.ttaptshirt.repository.ChatLieuRepository;
import com.project.ttaptshirt.repository.ChiTietSanPhamRepository;
import com.project.ttaptshirt.repository.KieuDangRepository;
import com.project.ttaptshirt.repository.NSXRepository;
import com.project.ttaptshirt.repository.ThuongHieuRepository;
import com.project.ttaptshirt.service.ChiTietSanPhamService;
//import com.project.ttaptshirt.service.HinhAnhService;
import com.project.ttaptshirt.service.KichCoService;
import com.project.ttaptshirt.service.MauSacService;
import com.project.ttaptshirt.service.SanPhamService;
import com.project.ttaptshirt.service.impl.ChiTietSanPhamServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.DecimalFormat;
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

    @PostMapping("/add")
    public String createNewCTSP(
            @RequestParam("idSanPham") Long idSanPham,
            @RequestParam(value = "mauSacIds", required = false) List<Long> mauSacIds,
            @RequestParam(value = "kichCoIds", required = false) List<Long> kichCoIds,
            @RequestParam Map<String, String> allParams, // Nhận toàn bộ request params
            RedirectAttributes redirectAttributes) {

        // Kiểm tra xem có màu sắc và kích cỡ nào được chọn không
        if (mauSacIds == null || mauSacIds.isEmpty() || kichCoIds == null || kichCoIds.isEmpty()) {
            return "redirect:/admin/chi-tiet-san-pham/" + idSanPham;
        }

        SanPham sanPham = new SanPham();
        sanPham.setId(idSanPham);
        List<String> duplicateWarnings = new ArrayList<>();
        // Duyệt từng kết hợp màu sắc và kích cỡ
        for (Long mauSacId : mauSacIds) {
            MauSac mauSac = new MauSac();
            mauSac.setId(mauSacId);

            for (Long kichCoId : kichCoIds) {
                KichCo kichCo = new KichCo();
                kichCo.setId(kichCoId);

                // Lấy giá bán và số lượng từ request params
                String priceKey = "price_" + mauSacId + "_" + kichCoId;
                String quantityKey = "quantity_" + mauSacId + "_" + kichCoId;

                Double giaBan = Double.valueOf(allParams.getOrDefault(priceKey, "0"));
                Integer soLuong = Integer.valueOf(allParams.getOrDefault(quantityKey, "0"));

                // Kiểm tra trùng lặp biến thể
                boolean exists = chiTietSanPhamServiceImpl.existsBySanPhamAndMauSacAndKichCo(sanPham, mauSac, kichCo);
                if (exists) {
                    duplicateWarnings.add("Màu sắc: " + mauSac.getTen() + " và Kích cỡ: " + kichCo.getTen() + " đã tồn tại.");
                    continue;
                }

                // Tạo ChiTietSanPham mới
                ChiTietSanPham newChiTietSanPham = new ChiTietSanPham();
                newChiTietSanPham.setSanPham(sanPham);
                newChiTietSanPham.setMa(generateUniqueCode()); // Mã tự động
                newChiTietSanPham.setMauSac(mauSac);
                newChiTietSanPham.setKichCo(kichCo);
                newChiTietSanPham.setGiaBan(giaBan);
                newChiTietSanPham.setSoLuong(soLuong);

                // Lưu vào DB
                chiTietSanPhamService.save(newChiTietSanPham);
            }
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
            RedirectAttributes redirectAttributes) {

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
        chiTietSanPhamService.save(existingCTSP);

        // Thêm ID sản phẩm vào RedirectAttributes
        redirectAttributes.addAttribute("id", idSanPham);

        redirectAttributes.addFlashAttribute("updateSuccess", true);
        // Redirect đến trang chi tiết sản phẩm
        return "redirect:/admin/chi-tiet-san-pham/{id}";
    }



}