package com.project.ttaptshirt.controller.customerController;

import com.project.ttaptshirt.dto.CartItemDTO;
import com.project.ttaptshirt.entity.GioHang;
import com.project.ttaptshirt.entity.User;
import com.project.ttaptshirt.repository.HoaDonChiTietRepository;
import com.project.ttaptshirt.repository.UserRepo;
import com.project.ttaptshirt.security.CustomUserDetail;
import com.project.ttaptshirt.service.impl.GioHangService;
import com.project.ttaptshirt.service.impl.HoaDonServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class GioHangController {
    @Autowired
    private HoaDonServiceImpl hoaDonService;

    @Autowired
    HoaDonChiTietRepository hdctr;

    @Autowired
    private GioHangService gioHangService;

    @Autowired
    private UserRepo userRepo;

    @GetMapping("/add")
    public String addItemToCart( @RequestParam Long productId, @RequestParam int quantity,
                                RedirectAttributes redirectAttributes, Authentication authentication) {
        if (authentication != null) {
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            User user = customUserDetail.getUser();
            gioHangService.addItemToCart(user, productId, quantity); // Thêm sản phẩm vào giỏ hàng của user
        }
        redirectAttributes.addFlashAttribute("message", "Sản phẩm đã được thêm vào giỏ hàng.");
        return "redirect:/view"; // Điều hướng đến trang giỏ hàng
    }

    // Xem giỏ hàng
    @GetMapping("/view")
    public String viewCart(Model model, Authentication authentication) {
        if (authentication != null) {
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            User user = customUserDetail.getUser();
            GioHang cart = gioHangService.getOrCreateCart(user); // Lấy giỏ hàng của người dùng
            if (cart.getItems() != null && !cart.getItems().isEmpty()) {
                model.addAttribute("cart", cart);
            } else {
                model.addAttribute("message", "Giỏ hàng của bạn trống.");
            }
            model.addAttribute("userLogged", user);
        }else {
            return "redirect:/login";
        }
        return "/user/home/cart2"; // Trả về trang giỏ hàng
    }

    // Xóa sản phẩm khỏi giỏ hàng
    @GetMapping("/remove")
    public String removeItemFromCart(@RequestParam Long productId, RedirectAttributes redirectAttributes,
                                     Authentication authentication) {
        if (authentication != null) {
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            User user = customUserDetail.getUser();
            gioHangService.removeProductFromCart(user, productId); // Xóa sản phẩm khỏi giỏ
        }
        System.out.println("sasaasa");
        redirectAttributes.addFlashAttribute("message", "Sản phẩm đã được xóa khỏi giỏ hàng.");
        return "redirect:/view"; // Điều hướng đến trang giỏ hàng
    }

    // Tạo đơn hàng từ giỏ hàng
    @GetMapping("/checkout")
    public String checkout(@RequestParam(value = "selectedProductIds", required = false)
                               List<Long> selectedProductIds,Authentication authentication, RedirectAttributes redirectAttributes) {
        if (authentication != null) {
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            User user = customUserDetail.getUser();
            try {
                List<CartItemDTO> selectedItems = getSelectedItemsFromCart(user, selectedProductIds);
                GioHang order = gioHangService.createOrderFromCart(user,selectedItems); // Tạo đơn hàng từ giỏ hàng
                redirectAttributes.addFlashAttribute("message", "Đơn hàng đã được tạo thành công.");
                return "redirect:/TTAP/order/view/" + order.getId(); // Điều hướng đến trang xem đơn hàng
            } catch (RuntimeException e) {
                redirectAttributes.addFlashAttribute("error", e.getMessage()); // Hiển thị lỗi nếu giỏ hàng trống
            }
        }
        return "redirect:/view"; // Quay lại trang giỏ hàng nếu có lỗi
    }

    @PostMapping("/TTAP/checkout")
    public String checkout(
            @RequestParam("selectedProductIds") List<Long> selectedProductIds,
            @RequestParam("fullName") String fullName,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("address") String address,
            HttpServletRequest request, Authentication authentication, Model model
            ) {
        if (authentication != null) {
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            User user = customUserDetail.getUser();

            // Tạo đơn hàng từ các sản phẩm được chọn
            List<CartItemDTO> selectedItems = getSelectedItemsFromCart(user, selectedProductIds);

            try {
                GioHang order = gioHangService.createOrderFromCart(user, selectedItems);
                return "redirect:/TTAP/order/view/" + order.getId(); // Điều hướng đến trang xem đơn hàng
            } catch (Exception e) {
                return "redirect:/view?error=" + e.getMessage(); // Nếu có lỗi, quay lại giỏ hàng
            }
        }
        return "redirect:/view";
    }

    private List<CartItemDTO> getSelectedItemsFromCart(User user, List<Long> selectedProductIds) {
        // Lấy giỏ hàng của user và lọc các sản phẩm được chọn
        GioHang cart = gioHangService.getOrCreateCart(user);
        return cart.getItems().stream()
                .filter(item -> selectedProductIds.contains(item.getChiTietSanPham().getId()))
                .map(item -> new CartItemDTO(item.getChiTietSanPham().getId(),item.getChiTietSanPham().getSanPham().getTen(),
                         item.getGia(), item.getSoLuong()))
                .collect(Collectors.toList());
    }
}
