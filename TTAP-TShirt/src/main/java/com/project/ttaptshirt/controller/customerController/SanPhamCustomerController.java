package com.project.ttaptshirt.controller.customerController;

import com.project.ttaptshirt.dto.ChiTietSanPhamDTO;
import com.project.ttaptshirt.dto.NumberUtils;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
            TaiKhoan user = customUserDetail.getUser();
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
                kieuDangId != null || chatLieuId != null || priceRangerId != 0) {
            sanPhamPage = sanPhamRepository.filterSanPham(
                    ten, nhaSanXuatId, thuongHieuId, kieuDangId, chatLieuId, minPrice, maxPrice, pageable);
        } else {
            sanPhamPage = sanPhamRepository.pageSP(pageable);
        }

        System.out.println("Price Ranger ID: " + priceRangerId);
        System.out.println("Min Price: " + minPrice);
        System.out.println("Max Price: " + maxPrice);
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

        NumberUtils numberUtils = new NumberUtils();

        model.addAttribute("numberUtils", numberUtils);
        model.addAttribute("listsp", sanPhamPage);
        model.addAttribute("ten", ten);
        model.addAttribute("nsxId", nhaSanXuatId);
        model.addAttribute("thuongHieuId", thuongHieuId);
        model.addAttribute("kieuDangId", kieuDangId);
        model.addAttribute("chatLieuId", chatLieuId);
        model.addAttribute("priceRangerId", priceRangerId);
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
    public String sanPhamDetail(@PathVariable Long idSP, Model model, Authentication authentication,
                                HttpServletRequest request) {
        if (authentication != null) {
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            TaiKhoan user = customUserDetail.getUser();
            model.addAttribute("userLogged", user);
        }
        model.addAttribute("requestURI", request.getRequestURI());

        // Lấy thông tin sản phẩm
        SanPham sanPham = sanPhamRepository.findById(idSP)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm!"));

        // Lấy danh sách hình ảnh sản phẩm
        List<String> images = hinhAnhRepository.findBySanPhamId(idSP);

        // Lấy chi tiết sản phẩm
        List<ChiTietSanPham> chiTietSanPhamList = chiTietSanPhamRepository.findBySanPhamId(idSP);

        if (chiTietSanPhamList.isEmpty()) {
            model.addAttribute("noDetails", true);
        }

        // Khởi tạo các biến cần thiết
        double giaBan = 0;
        int totalQuantity = 0;
        double minPrice = Double.MAX_VALUE; // Giá ban đầu là giá lớn nhất có thể

        Set<Map.Entry<Long, String>> availableColors = new HashSet<>();
        Set<Map.Entry<Long, String>> availableSizes = new HashSet<>();

        // Duyệt qua các chi tiết sản phẩm để tính tổng số lượng và giá thấp nhất
        for (ChiTietSanPham detail : chiTietSanPhamList) {
            // Kiểm tra số lượng sản phẩm có sẵn
            if (detail.getSoLuong() > 0) {
                totalQuantity += detail.getSoLuong();

                // Cập nhật giá thấp nhất nếu cần
                if (detail.getGiaBan() < minPrice) {
                    minPrice = detail.getGiaBan();
                }

                // Kiểm tra màu sắc có sẵn
                if (detail.getMauSac() != null) {
                    availableColors.add(new AbstractMap.SimpleEntry<>(detail.getMauSac().getId(), detail.getMauSac().getTen()));
                }

                // Kiểm tra kích cỡ có sẵn
                if (detail.getKichCo() != null) {
                    availableSizes.add(new AbstractMap.SimpleEntry<>(detail.getKichCo().getId(), detail.getKichCo().getTen()));
                }
            }
        }

        // Nếu không có chi tiết sản phẩm hợp lệ, gán giá và số lượng mặc định
        if (minPrice == Double.MAX_VALUE) {
            minPrice = 0; // Nếu không có giá hợp lệ, gán giá bằng 0
        }

        NumberUtils numberUtils = new NumberUtils();

        // Gửi các dữ liệu cần thiết tới giao diện người dùng
        model.addAttribute("numberUtils", numberUtils);
        model.addAttribute("giaBan", minPrice); // Giá thấp nhất
        model.addAttribute("sanPham", sanPham);
        model.addAttribute("totalQuantity", totalQuantity); // Tổng số lượng
        model.addAttribute("mainImage", images.isEmpty() ? "/images/no-image.png" : images.get(0));
        model.addAttribute("images", images);

        // Chỉ thêm màu sắc và kích cỡ nếu có
        if (!availableColors.isEmpty()) {
            model.addAttribute("colors", availableColors);
        }
        if (!availableSizes.isEmpty()) {
            model.addAttribute("sizes", availableSizes);
        }

        return "user/home/sanphamdetail";
    }



    @GetMapping("/product/{id}/sizes")
    @ResponseBody
    public List<Long> getAvailableSizes(@PathVariable Long id, @RequestParam long color) {
        // Lấy danh sách ChiTietSanPham từ service
        List<ChiTietSanPham> chiTietSanPhamList = chiTietSanPhamServiceImpl.getChiTietSanPhamBySanPhamAndMauSac(id, color);

        // Lấy danh sách kích thước khả dụng từ ChiTietSanPham
        List<Long> sizes = chiTietSanPhamList.stream()
                .map(ChiTietSanPham::getKichCo)  // Lấy kích cỡ từ ChiTietSanPham
                .map(KichCo::getId)  // Lấy tên kích cỡ
                .collect(Collectors.toList());

        return sizes;
    }

    @GetMapping("/product/{productId}/details")
    public ResponseEntity<ChiTietSanPhamDTO> getProductDetails(
            @PathVariable Long productId,
            @RequestParam Long colorId,
            @RequestParam Long sizeId) {
        Optional<ChiTietSanPham> chiTietSanPham = chiTietSanPhamServiceImpl.getsolgandgia(productId, colorId, sizeId);
        if (chiTietSanPham.isPresent()) {
            int soLuongHienThi = Math.max(chiTietSanPham.get().getSoLuong(), 0);
            ChiTietSanPhamDTO dto = new ChiTietSanPhamDTO(
                    chiTietSanPham.get().getGiaBan(),
                    soLuongHienThi
            );
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}