package com.project.ttaptshirt.controller.customerController;

import com.project.ttaptshirt.dto.CartDTO;
import com.project.ttaptshirt.dto.CartItemDTO;
import com.project.ttaptshirt.entity.HoaDon;
import com.project.ttaptshirt.repository.HoaDonChiTietRepository;
import com.project.ttaptshirt.service.impl.CartService;
import com.project.ttaptshirt.service.impl.HoaDonServiceImpl;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/TTAP/cart/")
@SessionAttributes("cart")
public class CartController {
    @Autowired
    private CartService cartService;

    @Autowired
    private HoaDonServiceImpl hoaDonService;

    @Autowired
    HoaDonChiTietRepository hdctr;

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
                           RedirectAttributes redirectAttributes,
                           Model model) {
        try {
           HoaDon hoaDon = hoaDonService.createHoaDon(cart, fullName, phoneNumber, address);

            // Xoá giỏ hàng sau khi thanh toán
            cart.setItems(new ArrayList<>());
            cart.setTotalAmount(BigDecimal.ZERO);

//            redirectAttributes.addFlashAttribute("message", "Thanh toán thành công!");
            model.addAttribute("hoaDon", hoaDon);
            return "/user/home/checkout";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/TTAP/cart/view";
        }
    }


    @GetMapping("/hoa-don-chi-tiet/hien-thi")
    public String hienThi(@RequestParam Long id, Model model){
        model.addAttribute("listHDCT",hdctr.getHoaDonChiTietByHoaDonId(id));
        return "/user/home/checkout";
    }



}

