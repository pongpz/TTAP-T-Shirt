package com.project.ttaptshirt.controller;


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
import com.project.ttaptshirt.service.HinhAnhService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.DecimalFormat;
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
    HinhAnhService hinhAnhService;

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
        model.addAttribute("listChatLieu", chatLieuRepository.findAll());
        model.addAttribute("listKieuDang", kieuDangRepository.findAll());
        model.addAttribute("listNSX", nsxRepository.findAll());
        model.addAttribute("listThuongHieu", thuongHieuRepository.findAll());
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
        model.addAttribute("listHinhAnh", hinhAnhService.findAll());
        model.addAttribute("listKichCo", kichCoService.findAll());
        return "admin/sanpham/them-chi-tiet-san-pham";
    }

    @PostMapping("/add")
    public String createNewCTSP(
            @RequestParam("idSanPham") Long idSanPham,
            @RequestParam("idMauSac") Long idMauSac,
            @RequestParam("idKichCo") Long idKichCo,
            @RequestParam("idHinhAnh") Long idHinhAnh,
            @RequestParam(required = false, value = "idKhuyenMai") Long idKhuyenMai,
            ChiTietSanPham chiTietSanPham,
            RedirectAttributes redirectAttributes) {

        SanPham sanPham = new SanPham();
        sanPham.setId(idSanPham);
        chiTietSanPham.setSanPham(sanPham);

        MauSac mauSac = new MauSac();
        mauSac.setId(idMauSac);
        chiTietSanPham.setMauSac(mauSac);

        KichCo kichCo = new KichCo();
        kichCo.setId(idKichCo);
        chiTietSanPham.setKichCo(kichCo);


        HinhAnh anh = new HinhAnh();
        anh.setId(idHinhAnh);
        chiTietSanPham.setHinhAnh(anh);


        chiTietSanPhamService.save(chiTietSanPham);

        // Use the path variable to build the redirect URL
        redirectAttributes.addAttribute("id", idSanPham);

        return "redirect:/admin/chi-tiet-san-pham/{id}";
    }


    @PostMapping("/update/{idCTSP}")
    public String updateCTSP(
            @PathVariable("idCTSP") Long idCTSP,
            @RequestParam(required = false, value = "idMauSac") Long idMauSac,
            @RequestParam(required = false, value = "idKichCo") Long idKichCo,
            @RequestParam(required = false, value = "idChatLieu") Long idChatLieu,
            @RequestParam(required = false, value = "idKieuDang") Long idKieuDang,
            @RequestParam(required = false, value = "idNSX") Long idNSX,
            @RequestParam(required = false, value = "idThuongHieu") Long idThuongHieu,
            @RequestParam(required = false, value = "idKhuyenMai") String idKhuyenMai,
            @RequestParam(required = false, value = "soLuong") Integer soLuong,
            @RequestParam(required = false, value = "giaBan") Double giaBan,
            RedirectAttributes redirectAttributes) {

        // Tìm đối tượng ChiTietSanPham hiện tại từ cơ sở dữ liệu
        ChiTietSanPham existingCTSP = chiTietSanPhamService.findById(idCTSP);

        if (existingCTSP == null) {
            // Xử lý lỗi nếu không tìm thấy đối tượng
            return "redirect:/admin/error"; // Hoặc trang lỗi phù hợp
        }

        // Cập nhật thuộc tính nếu có giá trị mới từ form
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

        // Cập nhật các thuộc tính khác nếu có trong form
        if (soLuong != null) {
            existingCTSP.setSoLuong(soLuong);
        }
        if (giaBan != null) {
            existingCTSP.setGiaBan(giaBan);
        }

        // Lưu đối tượng cập nhật
        chiTietSanPhamService.save(existingCTSP);

        // Thêm ID sản phẩm vào RedirectAttributes
        redirectAttributes.addAttribute("id", existingCTSP.getSanPham().getId());

        // Redirect đến trang chi tiết sản phẩm
        return "redirect:/admin/chi-tiet-san-pham/{id}";
    }



}
