package com.project.ttaptshirt.controller.customerController;

import com.project.ttaptshirt.entity.DatHang;
import com.project.ttaptshirt.entity.User;
import com.project.ttaptshirt.repository.HoaDonChiTietRepository;
import com.project.ttaptshirt.repository.UserRepo;
import com.project.ttaptshirt.security.CustomUserDetail;
import com.project.ttaptshirt.service.impl.CartService;
import com.project.ttaptshirt.service.impl.DatHangService;
import com.project.ttaptshirt.service.impl.HoaDonServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
public class DatHangController {
    @Autowired
    private HoaDonServiceImpl hoaDonService;

    @Autowired
    HoaDonChiTietRepository hdctr;

    @Autowired
    private DatHangService datHangService;
    @Autowired
    private CartService cartService;

    @Autowired
    private UserRepo userRepo;

    @PostMapping("/TTAP/checkout")
    public String checkout(
            @RequestParam("selectedProductIds") List<Long> selectedProductIds,
            @RequestParam("fullName") String fullName,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("address") String address,
            HttpServletRequest request, Authentication authentication, Model model
            ) {
        try {
            // Kiểm tra người dùng đăng nhập
            if (authentication == null) {
                model.addAttribute("error", "Bạn cần đăng nhập để thanh toán.");
                return "redirect:/login"; // Chuyển hướng đến trang đăng nhập
            }

            // Lấy thông tin người dùng
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            User user = customUserDetail.getUser();
            model.addAttribute("userLogged", user);

            // Lấy giỏ hàng hiện tại
            DatHang cart = datHangService.getOrCreateCart(user);

            // Kiểm tra sản phẩm được chọn
            if (selectedProductIds == null || selectedProductIds.isEmpty()) {
                model.addAttribute("error", "Vui lòng chọn ít nhất một sản phẩm để thanh toán.");
                return "redirect:/TTAP/cart"; // Quay lại giỏ hàng
            }

            // Tạo hóa đơn
            hoaDonService.createHoaDon2(cart, selectedProductIds, fullName, phoneNumber, address);

            // Chuyển hướng về trang giỏ hàng sau khi thanh toán
            return "redirect:/TTAP/cart";
        } catch (Exception e) {
            // Xử lý ngoại lệ và thông báo lỗi
            model.addAttribute("error", "Đã xảy ra lỗi trong quá trình thanh toán: " + e.getMessage());
            return "redirect:/TTAP/cart"; // Quay lại giỏ hàng
        }
    }
}
