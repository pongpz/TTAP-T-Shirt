package com.project.ttaptshirt.controller.customerController;

import com.project.ttaptshirt.dto.NumberUtils;
import com.project.ttaptshirt.entity.ChiTietSanPham;
import com.project.ttaptshirt.entity.SanPham;
import com.project.ttaptshirt.entity.User;
import com.project.ttaptshirt.repository.ChiTietSanPhamRepository;
import com.project.ttaptshirt.repository.GioHangRepository;
import com.project.ttaptshirt.repository.HoaDonChiTietRepository;
import com.project.ttaptshirt.repository.SanPhamRepository;
import com.project.ttaptshirt.security.CustomUserDetail;
import com.project.ttaptshirt.service.KichCoService;
import com.project.ttaptshirt.service.MauSacService;
import com.project.ttaptshirt.service.impl.CartService;
import com.project.ttaptshirt.service.impl.ChiTietSanPhamServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/TTAP")
public class HomeCustomerController {

    @Autowired
    ChiTietSanPhamServiceImpl chiTietSanPhamServiceIplm;

    @Autowired
    SanPhamRepository spr;

    @Autowired
    HoaDonChiTietRepository hdctrp;

    @Autowired
    ChiTietSanPhamRepository ctspr;

    @Autowired
    MauSacService mauSacService;

    @Autowired
    KichCoService kichCoService;

    @Autowired
    CartService serDatHang;

    @Autowired
    GioHangRepository repoDathang;

//    @GetMapping("/trang-chu")
//    public String home(HttpServletRequest request, Model model, Authentication authentication) {
//        if (authentication != null) {
//            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
//            User user = customUserDetail.getUser();
//            model.addAttribute("userLogged", user);
//        }
//        model.addAttribute("requestURI", request.getRequestURI());
//        // Lấy danh sách tất cả chi tiết sản phẩm
//        List<ChiTietSanPham> lsCTSP = ctspr.findAll();
//        for(ChiTietSanPham sp : lsCTSP){
//            if (sp == null || sp.getGiaBan() == null) {
//                System.out.println("KO cos gia");
//            }else {
//                System.out.println(sp.getGiaBan());
//            }
//        }
//        model.addAttribute("lsSPCT", lsCTSP);
//
//        return "user/home/trangchu"; // Trả về trang chủ
//    }

    @GetMapping("/trang-chu")
    public String sanPhamCustomer(HttpServletRequest request, Model model,
                                  @RequestParam(defaultValue = "0") int page,
                                  Authentication authentication) {
        if (authentication != null) {
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            User user = customUserDetail.getUser();
            model.addAttribute("userLogged", user);
        }
        model.addAttribute("requestURI", request.getRequestURI());

        Pageable pageable = PageRequest.of(page, 6);

        Page<SanPham> sanPhamPage = spr.findAll(pageable);
        Page<SanPham> sanPhamMoiPage = spr.pageSPMoi(pageable);


//        Pageable pageable = PageRequest.of(page, 6);
//        Page<SanPham> sanPhamPage = sanPhamRepository.findAll(pageable);

        // Lấy giá sp
        Map<Long, Double> giaSanPham = new HashMap<>();
        for (SanPham sanPham : sanPhamPage) {
            Double giaMin = chiTietSanPhamServiceIplm.getMinGiaBan(sanPham.getId());
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

        Map<Long, Double> giaSanPhamMoi = new HashMap<>();
        for (SanPham sanPhamm : sanPhamMoiPage) {
            Double giaMin = chiTietSanPhamServiceIplm.getMinGiaBan(sanPhamm.getId());
            giaSanPhamMoi.put(sanPhamm.getId(), giaMin != null ? giaMin : 0);
        }

        // Lấy hình ảnh
        Map<Long, String> hinhAnhSanPhamMoi = new HashMap<>();
        for (SanPham sanPhamm : sanPhamMoiPage) {
            if (sanPhamm.getHinhAnhList() != null && !sanPhamm.getHinhAnhList().isEmpty()) {
                String imageUrl = sanPhamm.getHinhAnhList().get(0).getPath();
                hinhAnhSanPhamMoi.put(sanPhamm.getId(), imageUrl);
            }
        }
        NumberUtils numberUtils = new NumberUtils();

        model.addAttribute("numberUtils", numberUtils);
        model.addAttribute("listsp", sanPhamPage);
        model.addAttribute("listspm", sanPhamMoiPage);
        model.addAttribute("giasanpham", giaSanPham);
        model.addAttribute("giasanphamm", giaSanPhamMoi);
        model.addAttribute("hinhAnhSanPham", hinhAnhSanPham);
        model.addAttribute("hinhAnhSanPhamm", hinhAnhSanPhamMoi);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", sanPhamPage.getTotalPages());
        model.addAttribute("totalItems", sanPhamPage.getTotalElements());
        model.addAttribute("requestURI", request.getRequestURI());

        return "user/home/trangchu";
    }

    @GetMapping("/san-pham/{id}")
    public String detailSP(HttpServletRequest request, Model model, Authentication authentication, @PathVariable Long id){
        if (authentication != null) {
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            User user = customUserDetail.getUser();
            model.addAttribute("userLogged", user);
        }
        model.addAttribute("requestURI", request.getRequestURI());
        // Kiểm tra danh sách trả về từ findByIDSanPham trước khi truy cập phần tử
//        model.addAttribute("mauSac", mauSacService.findAll());
//        model.addAttribute("kc", kichCoService.findAll());
        // Tìm sản phẩm chi tiết theo id
        Optional<ChiTietSanPham> ctspOptional = ctspr.findById(id);
        // Kiểm tra và xử lý kết quả
        if (ctspOptional.isPresent()) {
            ChiTietSanPham ctsp = ctspOptional.get();  // Lấy đối tượng nếu tồn tại
            model.addAttribute("spct", ctsp);          // Thêm vào model
        } else {
            // Xử lý khi không tìm thấy sản phẩm chi tiết
            model.addAttribute("spct", null);          // Hoặc trả về thông báo lỗi
            model.addAttribute("errorMessage", "Không tìm thấy sản phẩm chi tiết.");
        }
        return "user/home/sanphamdetail";
    }


    @GetMapping("/chinh-sach-van-chuyen")
    public String chinhSachVanChuyen(){
        return "user/chinhsach/chinh-sach-van-chuyen";
    }

    @GetMapping("/chinh-sach-bao-mat")
    public String chinhSachBaoMat(){
        return "user/chinhsach/chinh-sach-bao-mat";
    }

    @GetMapping("/chinh-sach-doi-tra")
    public String chinhSachDoiTra(){
        return "user/chinhsach/chinh-sach-doi-tra";
    }

    @GetMapping("/chinh-sach-xu-ly-khieu-lai")
    public String chinhSachXuLyKhieuLai(){
        return "user/chinhsach/chinh-sach-xu-ly-khieu-lai";
    }

    @GetMapping("/huong-dan-mua-hang")
    public String huongDanMuaHang(){
        return "user/chinhsach/huong-dan-mua-hang";
    }
    @GetMapping("/hinh-thuc-thanh-toan")
    public String hinhThucThanhToan(){
        return "user/chinhsach/hinh-thuc-thanh-toan";
    }
}