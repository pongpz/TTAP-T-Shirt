package com.project.ttaptshirt.controller.customerController;

import com.project.ttaptshirt.entity.*;
import com.project.ttaptshirt.repository.*;
//import com.project.ttaptshirt.service.HinhAnhService;
import com.project.ttaptshirt.security.CustomUserDetail;
import com.project.ttaptshirt.service.impl.ChiTietSanPhamServiceImpl;
import com.project.ttaptshirt.service.impl.SanPhamServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/TTAP")
public class SanPhamCustomerController {

    @Autowired
    private SanPhamRepository sanPhamRepository;

    @Autowired
    private ChiTietSanPhamRepository chiTietSanPhamRepository;

    @Autowired
    private ThuongHieuRepository thuongHieuRepository;

    @Autowired
    private KieuDangRepository kieuDangRepository;

    @Autowired
    private NSXRepository nsxRepository;

    @Autowired
    private KichCoRepository kichCoRepository;

    @Autowired
    private ChatLieuRepository chatLieuRepository;
    @Autowired
    private SanPhamServiceImpl sanPhamServiceImpl;
    @Autowired
    private ChiTietSanPhamServiceImpl chiTietSanPhamServiceImpl;
    @Autowired
    private HinhAnhRepository hinhAnhRepository;

    @Autowired
    private  MauSacRepository mauSacRepository;

//    @Autowired
//    HinhAnhService hinhAnhService;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PriceRanger {
        int id;
        double minValue;
        double maxValue;
        String display;
    }

    private final List<PriceRanger> priceRangerList = Arrays.asList(
            new PriceRanger(0, 0, Double.MAX_VALUE, "Tất cả"),
            new PriceRanger(1, 0, 100000, "Dưới 100 nghìn"),
            new PriceRanger(2, 100000, 500000, "Từ 100-500 nghìn"),
            new PriceRanger(3, 500000, 1000000, "Từ 500-1 triệu"),
            new PriceRanger(4, 1000000, 2000000, "Từ 1-2 triệu"),
            new PriceRanger(5, 2000000, 5000000, "Từ 2-5 triệu"),
            new PriceRanger(6, 5000000, Double.MAX_VALUE, "Trên 5 triệu")
    );

    @GetMapping("/san-pham")
    public String sanPhamCustomer(HttpServletRequest request, Model model,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(required = false) String ten,
                                  @RequestParam(required = false) Long nhaSanXuatId,
                                  @RequestParam(required = false) Long thuongHieuId,
                                  @RequestParam(required = false) Long kieuDangId,
                                  @RequestParam(required = false) Long chatLieuId,
                                  @RequestParam(defaultValue = "0") int priceRangerId,
                                  Authentication authentication) {
        if (authentication != null) {
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            User user = customUserDetail.getUser();
            model.addAttribute("userLogged", user);
        }
        model.addAttribute("requestURI", request.getRequestURI());

        double minPrice = 0;
        double maxPrice = Double.MAX_VALUE;
        if (priceRangerId >= 0 && priceRangerId < priceRangerList.size()) {
            minPrice = priceRangerList.get(priceRangerId).getMinValue();
            maxPrice = priceRangerList.get(priceRangerId).getMaxValue();
        }

        Pageable pageable = PageRequest.of(page, 6);

        Page<SanPham> sanPhamPage;
        if (ten != null || nhaSanXuatId != null || thuongHieuId != null ||
                kieuDangId != null || chatLieuId != null ) {
            sanPhamPage = sanPhamRepository.filterSanPham(
                    ten, nhaSanXuatId, thuongHieuId, kieuDangId, chatLieuId,
                    minPrice, maxPrice,pageable
            );
        } else {
            sanPhamPage = sanPhamRepository.findAll(pageable);
        }


//        Pageable pageable = PageRequest.of(page, 6);
//        Page<SanPham> sanPhamPage = sanPhamRepository.findAll(pageable);

        // Lấy giá sp
        Map<Long, Double> giaSanPham = new HashMap<>();
        for (SanPham sanPham : sanPhamPage) {
            Double giaMin = chiTietSanPhamServiceImpl.getMinGiaBan(sanPham.getId());
            giaSanPham.put(sanPham.getId(), giaMin != null ? giaMin : 0);
        }

        // Lấy hình ảnh
        Map<Long, String> hinhAnhSanPham = new HashMap<>();
        for (SanPham sanPham : sanPhamPage) {
            if (sanPham.getHinhAnhList() != null && !sanPham.getHinhAnhList().isEmpty()) {
                String imageUrl = sanPham.getHinhAnhList().get(0).getPath();
                hinhAnhSanPham.put(sanPham.getId(), imageUrl);
            }
        }

        model.addAttribute("listsp", sanPhamPage);
        model.addAttribute("giasanpham", giaSanPham);
        model.addAttribute("hinhAnhSanPham", hinhAnhSanPham);
        model.addAttribute("kichCoList", kichCoRepository.findAll());
        model.addAttribute("mauSacList", mauSacRepository.findAll());
        model.addAttribute("nhaSanXuatList", nsxRepository.findAll());
        model.addAttribute("chatLieuList", chatLieuRepository.findAll());
        model.addAttribute("thuongHieuList", thuongHieuRepository.findAll());
        model.addAttribute("kieuDangList", kieuDangRepository.findAll());
        model.addAttribute("priceRangerList", priceRangerList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", sanPhamPage.getTotalPages());
        model.addAttribute("totalItems", sanPhamPage.getTotalElements());
        model.addAttribute("requestURI", request.getRequestURI());

        return "user/home/sanpham";
    }




    @GetMapping("/san-pham-detail/{idSP}")
    public String sanPhamDetail(@PathVariable Long idSP, Model model) {
        SanPham sanPham = sanPhamRepository.findById(idSP)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm!"));

        List<String> images = hinhAnhRepository.findBySanPhamId(idSP);

        List<ChiTietSanPham> chiTietSanPhamList = chiTietSanPhamRepository.findBySanPhamId(idSP);

        if (chiTietSanPhamList.isEmpty()) {
            model.addAttribute("noDetails", true);
        }

        Set<MauSac> colors = chiTietSanPhamList.stream()
                .map(ChiTietSanPham::getMauSac)
                .collect(Collectors.toSet());

        Set<KichCo> sizes = chiTietSanPhamList.stream()
                .map(ChiTietSanPham::getKichCo)
                .collect(Collectors.toSet());

        ChiTietSanPham chiTietSanPham = !chiTietSanPhamList.isEmpty() ? chiTietSanPhamList.get(0) : null;
        double giaBan = chiTietSanPham != null ? chiTietSanPham.getGiaBan() : 0;

        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        String giaBan1 = decimalFormat.format(giaBan);

        model.addAttribute("giaBan", giaBan1);
        model.addAttribute("sanPham", sanPham);
        model.addAttribute("mainImage", images.isEmpty() ? "/images/no-image.png" : images.get(0));
        model.addAttribute("images", images);
        model.addAttribute("colors", colors);
        model.addAttribute("sizes", sizes);

        return "user/home/sanphamdetail";
    }




}