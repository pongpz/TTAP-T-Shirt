package com.project.ttaptshirt.controller.customerController;

import com.project.ttaptshirt.dto.CartDTO;
import com.project.ttaptshirt.service.impl.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;

@Controller
@RequestMapping("/TTAP/cart/")
@SessionAttributes("cart")
public class CartController {
    @Autowired
    private CartService cartService;

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
    public String viewCart(@ModelAttribute("cart") CartDTO cart, Model model) {
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
                           RedirectAttributes redirectAttributes) {
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Giỏ hàng của bạn đang trống.");
            return "redirect:/TTAP/cart/view";
        }

        // Xử lý thanh toán (Lưu thông tin đơn hàng vào database hoặc gửi email)
        System.out.println("Thanh toán:");
        System.out.println("Họ và tên: " + fullName);
        System.out.println("Số điện thoại: " + phoneNumber);
        System.out.println("Địa chỉ: " + address);
        System.out.println("Chi tiết giỏ hàng: " + cart.getItems());

        // Clear giỏ hàng sau khi thanh toán
        cart.setItems(new ArrayList<>());
        cart.setTotalAmount(BigDecimal.ZERO);

        redirectAttributes.addFlashAttribute("message", "Thanh toán thành công!");
        return "redirect:/TTAP/cart/view";
    }


}

