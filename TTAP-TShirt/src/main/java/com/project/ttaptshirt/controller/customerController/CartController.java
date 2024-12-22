package com.project.ttaptshirt.controller.customerController;

import com.project.ttaptshirt.dto.CartDTO;
import com.project.ttaptshirt.entity.HoaDon;
import com.project.ttaptshirt.entity.HoaDonLog;
import com.project.ttaptshirt.entity.TaiKhoan;
import com.project.ttaptshirt.repository.HoaDonChiTietRepository;
import com.project.ttaptshirt.repository.HoaDonLogRepository;
import com.project.ttaptshirt.security.CustomUserDetail;
import com.project.ttaptshirt.service.impl.CartService;
import com.project.ttaptshirt.service.impl.HoaDonServiceImpl;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Controller
@SessionAttributes("cart")
public class CartController {
    @Autowired
    private CartService cartService;

    @Autowired
    private HoaDonServiceImpl hoaDonService;

    @Autowired
    HoaDonChiTietRepository hdctr;

    @Autowired
    HoaDonLogRepository hdlogr;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @ModelAttribute("cart")
    public CartDTO createCart() {
        return new CartDTO(); // Tạo giỏ hàng mới nếu chưa tồn tại
    }

    @GetMapping("/add")
    public String addItem(@ModelAttribute("cart") CartDTO cart, @RequestParam Long productId, @RequestParam int quantity,
                          RedirectAttributes redirectAttributes) {
        cartService.addItem(cart, productId, quantity);
        redirectAttributes.addFlashAttribute("message", "Sản phẩm đã được thêm vào giỏ hàng.");
        return "redirect:/TTAP/cart/view";
    }

    @GetMapping("/view")
    public String viewCart(@ModelAttribute("cart") CartDTO cart, Model model, Authentication authentication) {
        if (authentication != null) {
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            TaiKhoan user = customUserDetail.getUser();
            model.addAttribute("userLogged", user);
        }
        if (cart.getItems() != null && !cart.getItems().isEmpty()) {
            model.addAttribute("cart", cart); // Thêm giỏ hàng vào Model
            System.out.println("all:"+model.asMap());
        } else {
            model.addAttribute("message", "Giỏ hàng của bạn trống.");
        }
        return "/user/home/cart"; // Trả về trang Thymeleaf hiển thị giỏ hàng
    }

    @GetMapping("/remove")
    public String removeItem(@ModelAttribute("cart") CartDTO cart, @RequestParam Long productId,
                             RedirectAttributes redirectAttributes) {
        cartService.removeItem(cart, productId); // Xóa sản phẩm khỏi giỏ
        redirectAttributes.addFlashAttribute("message", "Sản phẩm đã được xóa khỏi giỏ hàng.");
        return "redirect:/TTAP/cart/view";
    }

    @PostMapping("/checkout")
    public String checkout(@ModelAttribute("cart") CartDTO cart,
                           @RequestParam String fullName,
                           @RequestParam String phoneNumber,
                           @RequestParam String address,
                           RedirectAttributes redirectAttributes,
                           HttpSession session,
                           Model model) {
        try {
           HoaDon hoaDon = hoaDonService.createHoaDon(cart, fullName, phoneNumber, address);

            session.setAttribute("hoaDon", hoaDon);
            // Xoá giỏ hàng sau khi thanh toán
            cart.setItems(new ArrayList<>());
            cart.setTotalAmount(BigDecimal.ZERO);

            model.addAttribute("hoaDon", hoaDon);
            return "/user/home/checkout";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/TTAP/cart/view";
        }
    }


//    @GetMapping("/hoa-don-chi-tiet/hien-thi")
//    public String hienThi(@RequestParam Long id, Model model,HttpSession session, Authentication authentication){
//        if (authentication != null) {
//            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
//            User user = customUserDetail.getUser();
//            model.addAttribute("userLogged", user);
//        }
//        HoaDon hoaDon = (HoaDon) session.getAttribute("hoaDon");
//        model.addAttribute("hoaDon", hoaDon);
//        model.addAttribute("listHDCT",hdctr.getHoaDonChiTietByHoaDonId(id));
//        return "/user/home/checkout";
//    }

    @GetMapping("/viewInvoice")
    public String viewInvoice(HttpSession session, Model model) {
        // Lấy hóa đơn từ session
        HoaDon hoaDon = (HoaDon) session.getAttribute("hoaDon");

        if (hoaDon != null) {
            // Nếu có hóa đơn, truyền vào model để hiển thị
            model.addAttribute("hoaDon", hoaDon);
            return "/user/home/checkout"; // Trả về view hiển thị thông tin hóa đơn
        } else {
            // Nếu không có hóa đơn, hiển thị thông báo lỗi
            model.addAttribute("message", "Không tìm thấy hóa đơn.");
            return "redirect:/TTAP/cart/view"; // Quay lại trang giỏ hàng
        }
    }

    @Transactional
    @GetMapping("/cancel-hoa-don/online/{idHD}")
    public String cancelHD(@PathVariable("idHD") Long idHD, Authentication authentication,Model model){
        HoaDonLog hdlog = new HoaDonLog();
        if (authentication != null) {
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            TaiKhoan user = customUserDetail.getUser();
            hdlog.setNguoiThucHien(user.getNhanVien() != null?user.getNhanVien().getHoTen():"");
            model.addAttribute("userLogged", user); // Gửi thông tin người dùng vào model
        }
        HoaDon hd = hoaDonService.findById(idHD);
        hd.setTrangThai(2);
        hoaDonService.save(hd);
        hdlog.setHoaDon(hd);
        hdlog.setHanhDong("Hủy hóa đơn");
        hdlog.setTrangThai(0);
        hdlog.setThoiGian(LocalDateTime.now());
        hdlog.setGhiChu("Đã thực hiện hủy đơn hàng");
        hdlogr.save(hdlog);
        return "redirect:/TTAP/cart/hoa-don";
    }


}

