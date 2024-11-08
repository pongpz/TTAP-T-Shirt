package com.project.ttaptshirt.controller.customerController;

import com.project.ttaptshirt.entity.ChiTietSanPham;
import com.project.ttaptshirt.entity.DatHang;
import com.project.ttaptshirt.entity.DatHangChiTiet;
import com.project.ttaptshirt.repository.ChiTietSanPhamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.Collections;

@Controller
@RequestMapping("/cart")
@SessionAttributes("cart")
public class GioHangController {
    @ModelAttribute("cart")
    public DatHang initializeCart() {
        DatHang cart = new DatHang();
        cart.setPhiVanChuyen(50_000.0); // Giá trị phí vận chuyển mặc định
        cart.setGiamGia(0.0); // Mặc định không có giảm giá
        return cart;
    }

    @GetMapping
    public String viewCart(@ModelAttribute("cart") DatHang cart, Model model) {
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            model.addAttribute("message", "Giỏ hàng của bạn đang trống.");
            model.addAttribute("cartItems", Collections.emptyList()); // Đảm bảo không có item nào để lặp qua trong view
            model.addAttribute("totalPrice", 0.0);
            return "/views/user/home/cart"; // Trả về cùng trang với thông báo giỏ hàng trống
        }
        double totalPrice = cart.getItems().stream()
                .mapToDouble(item -> item.getGia() * item.getSoLuong()).sum();
        totalPrice += cart.getPhiVanChuyen() - cart.getGiamGia();
        cart.setTongTien(totalPrice);

        model.addAttribute("cartItems", cart.getItems());
        model.addAttribute("phiVanChuyen", cart.getPhiVanChuyen());
        model.addAttribute("giamGia", cart.getGiamGia());
        model.addAttribute("totalPrice", cart.getTongTien());
        return "/views/user/home/cart";
    }

    @PostMapping("/apply-discount")
    public String applyDiscount(@ModelAttribute("cart") DatHang cart,
                                @RequestParam("discountCode") String discountCode) {
        // Giả sử mã "DISCOUNT10" giảm 10% tổng giá trị
        if ("DISCOUNT10".equalsIgnoreCase(discountCode)) {
            double discount = cart.getTongTien() * 0.1;
            cart.setGiamGia(discount);
        }
        return "redirect:/cart";
    }

    @PostMapping("/add")
    public String addItemToCart(@ModelAttribute("cart") DatHang cart,
                                @RequestParam("productId") Long productId,
                                @RequestParam("productName") String productName,
                                @RequestParam("quantity") int quantity,
                                @RequestParam("price") double price,
                                @Autowired ChiTietSanPhamRepository chiTietSanPhamRepository) {
        ChiTietSanPham chiTietSanPham = chiTietSanPhamRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        DatHangChiTiet newItem = new DatHangChiTiet();
        newItem.setChiTietSanPham(chiTietSanPham);
        newItem.setSoLuong(quantity);
        newItem.setGia(price);
        newItem.setDatHang(cart);

        cart.getItems().add(newItem);
        return "redirect:/cart";
    }
}
