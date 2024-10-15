package com.project.ttaptshirt.controller;

import com.project.ttaptshirt.entity.ChiTietSanPham;
import com.project.ttaptshirt.entity.KichCo;
import com.project.ttaptshirt.entity.KieuDang;
import com.project.ttaptshirt.entity.MauSac;
import com.project.ttaptshirt.entity.SanPham;
import com.project.ttaptshirt.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
            new PriceRanger(6, 5000000, Double.MAX_VALUE, "Trên 5 triệu") // Sửa 'Từ 2-5 triệu' thành 'Trên 5 triệu'
    );

    @GetMapping("/san-pham")
    public String sanPhamCustomer(HttpServletRequest request, Model model,
                                  @RequestParam(defaultValue = "") String ten,
                                  @RequestParam(defaultValue = "0") int priceRangerId,
                                  @RequestParam(defaultValue = "0") int kichCoId,
                                  @RequestParam(defaultValue = "0") int page) {

        // Kiểm tra và xử lý giá trị của priceRanger
        double minPrice = 0;
        double maxPrice = Double.MAX_VALUE; // Giá trị mặc định nếu không chọn khoảng giá

        if (priceRangerId >= 0 && priceRangerId < priceRangerList.size()) {
            minPrice = priceRangerList.get(priceRangerId).getMinValue();
            maxPrice = priceRangerList.get(priceRangerId).getMaxValue();
        }

        Pageable pageable = PageRequest.of(page, 6);

        Page<ChiTietSanPham> sanPhamPage = chiTietSanPhamRepository.findByTenContainingAndPriceBetween(
                ten.isEmpty() ? null : ten,
                minPrice,
                maxPrice,
                kichCoId == 0 ? null : kichCoId,
                pageable);

        model.addAttribute("kichCoList", kichCoRepository.findAll());
        model.addAttribute("kichCoId", kichCoId);
        model.addAttribute("requestURI", request.getRequestURI());
        model.addAttribute("listctsp", sanPhamPage.getContent());
        model.addAttribute("totalPage", sanPhamPage.getTotalPages());
        model.addAttribute("priceRangerList", priceRangerList);
        model.addAttribute("ten", ten);
        model.addAttribute("priceRangerId", priceRangerId);

        return "user/home/sanpham";
    }


    @GetMapping("/san-pham-detail/{idSP}")
    public String sanPhamDetail(@PathVariable Long idSP, Model model,@RequestParam(required = false, value = "mauSac") String mauSac,@RequestParam(required = false, value = "kichCo") String kichCo){
        List<ChiTietSanPham> ls = chiTietSanPhamRepository.findByIDSanPham(idSP,kichCo,mauSac);
        model.addAttribute("SPCTFist", ls.stream().findFirst().orElse(null));
        List<MauSac> lsms = new ArrayList<>();
        List<KichCo> lskk = new ArrayList<>();
        List<ChiTietSanPham> lsctsp = chiTietSanPhamRepository.getThuocTinhSPCT(idSP);
        for (int i = 0 ; i < lsctsp.size() ; i++){
            lsms.add(lsctsp.get(i).getMauSac());
            lskk.add(lsctsp.get(i).getKichCo());
        }
        model.addAttribute("lsms",lsms);
        model.addAttribute("lskk",lskk);
        return "user/home/sanphamdetail";
    }

    public String formatCurrency(double amount) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(amount);
    }
}
