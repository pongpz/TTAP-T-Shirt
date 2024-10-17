package com.project.ttaptshirt.controller.customerController;

import com.project.ttaptshirt.dto.CartDTO;
import com.project.ttaptshirt.service.impl.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/TTAP/cart/")
public class CartController {
    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public String addProductToCart(
            @RequestParam("productId") Long productId,
            @RequestParam("sizeId") Long sizeId,
            @RequestParam("colorId") Long colorId,
            @RequestParam("quantity") int quantity,
            @RequestParam("totalPrice") double totalPrice,
            @RequestParam("userId") Long userId,
            RedirectAttributes redirectAttributes) {

        cartService.addSanPham(productId, quantity, totalPrice, userId);
        redirectAttributes.addFlashAttribute("message", "Product added to cart successfully!");
        return "redirect:/TTAP/trangchu";
    }

    @PostMapping("/update")
    public String updateProductQuantity(
            @RequestParam("productId") Long productId,
            @RequestParam("quantity") int quantity,
            @RequestParam("userId") Long userId,
            RedirectAttributes redirectAttributes) {

        cartService.updateProductQuantity(userId, productId, quantity);
        redirectAttributes.addFlashAttribute("message", "Product quantity updated successfully!");
        return "redirect:/cart/view";
    }

    @PostMapping("/remove")
    public String removeProductFromCart(
            @RequestParam("productId") Long productId,
            @RequestParam("userId") Long userId,
            RedirectAttributes redirectAttributes) {

        cartService.removeProductFromCart(userId, productId);
        redirectAttributes.addFlashAttribute("message", "Product removed from cart successfully!");
        return "redirect:/cart/view";
    }

    @GetMapping("/view")
    public String viewCart(
            @RequestParam("userId") Long userId,
            Model model) {

        CartDTO cart = cartService.getCart(userId);
        model.addAttribute("cart", cart);
        return "cart/view";
    }
}

