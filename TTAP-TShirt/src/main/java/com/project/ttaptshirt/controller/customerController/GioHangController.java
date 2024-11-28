package com.project.ttaptshirt.controller.customerController;

import com.project.ttaptshirt.dto.AddToCartRequest;
import com.project.ttaptshirt.dto.CartItemDTO;
import com.project.ttaptshirt.dto.NumberUtils;
import com.project.ttaptshirt.entity.GioHang;
import com.project.ttaptshirt.entity.HoaDon;
import com.project.ttaptshirt.entity.User;
import com.project.ttaptshirt.repository.HoaDonChiTietRepository;
import com.project.ttaptshirt.repository.UserRepo;
import com.project.ttaptshirt.security.CustomUserDetail;
import com.project.ttaptshirt.service.impl.GioHangService;
import com.project.ttaptshirt.service.impl.HoaDonServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
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
            model.addAttribute("cart", cart); // Đảm bảo luôn truyền giỏ hàng vào model
            model.addAttribute("userLogged", user);
            NumberUtils numberUtils = new NumberUtils();

            model.addAttribute("numberUtils", numberUtils);
            return "/user/home/cart2";
        }
        return "redirect:/login";
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

    @PostMapping("/tao-don")
    public String taoDonHang(
            @ModelAttribute List<CartItemDTO> selectedItems,
            RedirectAttributes redirectAttributes,
            Authentication authentication) {
        try {
            if (authentication != null) {
                // Lấy thông tin người dùng từ Authentication
                CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
                User user = customUserDetail.getUser();

                // Gọi service để tạo đơn hàng
                GioHang order = gioHangService.createOrderFromCart(user, selectedItems);

                // Thông báo thành công và chuyển hướng tới trang chi tiết đơn hàng
                redirectAttributes.addFlashAttribute("message", "Đơn hàng đã được tạo thành công!");
                return "redirect:/don-hang/chi-tiet/" + order.getId();
            } else {
                // Nếu không có thông tin đăng nhập
                redirectAttributes.addFlashAttribute("error", "Bạn cần đăng nhập để thực hiện thao tác này.");
                return "redirect:/login";
            }
        } catch (Exception e) {
            // Xử lý lỗi và trả về thông báo
            redirectAttributes.addFlashAttribute("error", "Lỗi khi tạo đơn hàng: " + e.getMessage());
            return "redirect:/gio-hang";
        }
    }

    @PostMapping("/checkout")
    public String checkoutCart(@RequestParam("selectedProductIds") String selectedProductIdsStr,
                               @RequestParam String fullName,
                               @RequestParam String phoneNumber,
                               @RequestParam String address,
                               RedirectAttributes redirectAttributes,
                               Authentication authentication,
                               Model model) {
        if (authentication != null) {
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            User user = customUserDetail.getUser();

            try {
                // Nếu danh sách sản phẩm được chọn trống hoặc null
                if (selectedProductIdsStr == null || selectedProductIdsStr.trim().isEmpty()) {
                    redirectAttributes.addFlashAttribute("error", "Vui lòng chọn ít nhất một sản phẩm để thanh toán.");
                    return "redirect:/view"; // Quay lại trang giỏ hàng
                }
                List<Long> selectedProductIds = Arrays.stream(selectedProductIdsStr.split(","))
                        .map(Long::parseLong)
                        .collect(Collectors.toList());
                HoaDon hoaDon = gioHangService.checkoutCart(user, selectedProductIds,fullName,phoneNumber,address);
                redirectAttributes.addFlashAttribute("message", "Hóa đơn đã được tạo thành công!");
                model.addAttribute("userLogged", user);
                return "redirect:/hoa-don"; // Chuyển đến trang chi tiết hóa đơn
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Lỗi khi tạo hóa đơn: " + e.getMessage());
                return "redirect:/view"; // Quay lại trang giỏ hàng
            }
        }
        return "redirect:/login"; // Nếu chưa đăng nhập, chuyển hướng đến trang login
    }

    @PostMapping("/add-to-cart")
    public String addItemToCart(@RequestParam Long productId,
                                @RequestParam int quantity,
                                @RequestParam Long sizeId,
                                @RequestParam Long colorId,
                                RedirectAttributes redirectAttributes,
                                Authentication authentication
                                ) {
        if (authentication != null) {
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            User user = customUserDetail.getUser();

            // Tạo request với thông tin sản phẩm, kích cỡ và màu sắc
            AddToCartRequest request = new AddToCartRequest();
            request.setProductId(productId);
            request.setQuantity(quantity);
            request.setSize(sizeId);
            request.setColor(colorId);

            try {
                // Gọi service để thêm sản phẩm vào giỏ hàng
                gioHangService.addToCart(user, request);
                redirectAttributes.addFlashAttribute("message", "Sản phẩm đã được thêm vào giỏ hàng.");
            } catch (GioHangService.ProductNotFoundException e) {
                // Thông báo lỗi khi không tìm thấy sản phẩm
                redirectAttributes.addFlashAttribute("error", "sản phẩm chi tiết chưa có: " + e.getMessage());
                return "redirect:/TTAP/san-pham-detail/" + request.getProductId();
            } catch (GioHangService.InsufficientStockException e) {
                // Thông báo lỗi khi không đủ sản phẩm trong kho
                redirectAttributes.addFlashAttribute("error", "sản phẩm không còn: " + e.getMessage());
                return "redirect:/TTAP/san-pham-detail/" + request.getProductId();
            }
        }

        return "redirect:/view"; // Điều hướng đến trang giỏ hàng
    }

    @GetMapping("/hoa-don")
    public String listHoaDon(Model model, Authentication authentication) {
        if (authentication != null) {
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            User user = customUserDetail.getUser();

            List<HoaDon> hoaDonList = hoaDonService.getListDonHang(user.getKhachHang());
            if (hoaDonList != null) {
                NumberUtils numberUtils = new NumberUtils();

                model.addAttribute("numberUtils", numberUtils);
                model.addAttribute("hoaDon", hoaDonList);
                model.addAttribute("userLogged", user);
                return "/user/home/checkout"; // Đường dẫn đến view danh sách hóa đơn
            } else {
                // Nếu không có hóa đơn, hiển thị thông báo lỗi
                model.addAttribute("message", "Không tìm thấy hóa đơn.");
                return "redirect:/view"; // Quay lại trang giỏ hàng
            }
        }
        return "redirect:/login"; // Nếu chưa đăng nhập, chuyển hướng đến trang login
    }

    @GetMapping("/hoa-don-chi-tiet/hien-thi")
    public String hienThi(@RequestParam Long id, Model model, Authentication authentication){
        if (authentication != null) {
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            User user = customUserDetail.getUser();

                HoaDon hoaDon = hoaDonService.getDonHang(id);
                model.addAttribute("hoaDon", hoaDon);
                model.addAttribute("listHDCT",hdctr.getHoaDonChiTietByHoaDonId(id));
            model.addAttribute("userLogged", user);
                return "/user/home/cart"; // Đường dẫn đến view danh sách hóa đơn

        }
        return "redirect:/login";
    }

}
