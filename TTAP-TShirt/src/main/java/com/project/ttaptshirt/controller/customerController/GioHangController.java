package com.project.ttaptshirt.controller.customerController;

import com.project.ttaptshirt.dto.AddToCartRequest;
import com.project.ttaptshirt.dto.CartItemDTO;
import com.project.ttaptshirt.dto.NumberUtils;
import com.project.ttaptshirt.entity.*;
import com.project.ttaptshirt.repository.HoaDonChiTietRepository;
import com.project.ttaptshirt.repository.MaGiamGiaRepo;
import com.project.ttaptshirt.repository.UserRepo;
import com.project.ttaptshirt.security.CustomUserDetail;
import com.project.ttaptshirt.service.DiaChiService;
import com.project.ttaptshirt.service.impl.DiscountService;
import com.project.ttaptshirt.service.impl.GioHangService;
import com.project.ttaptshirt.service.impl.HoaDonServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/TTAP/cart")
public class GioHangController {
    @Autowired
    private HoaDonServiceImpl hoaDonService;

    @Autowired
    HoaDonChiTietRepository hdctr;

    @Autowired
    private GioHangService gioHangService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private DiscountService discountService;

    @Autowired
    private DiaChiService serDc;


    @GetMapping("/add")
    public String addItemToCart(@RequestParam Long productId, @RequestParam int quantity,
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
    public String viewCart(Model model, Authentication authentication,
                           @RequestParam(value = "selectedAddressId", required = false) Long selectedAddressId,
                           @ModelAttribute DiaChi address) {
        if (authentication != null) {
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            User user = customUserDetail.getUser();
            GioHang cart = gioHangService.getOrCreateCart(user);
            model.addAttribute("cart", cart);
            model.addAttribute("userLogged", user);

            // Lấy danh sách địa chỉ
            List<DiaChi> addresses = serDc.findAddressesByUser(user.getId());
            model.addAttribute("addresses", addresses);

            if (address == null) {
                address = new DiaChi(); // Tạo đối tượng mới nếu không có
            }
            // Lấy địa chỉ đã chọn hoặc địa chỉ đầu tiên mặc định
            DiaChi selectedAddress = (selectedAddressId != null) ?
                    addresses.stream()
                            .filter(addr -> addr.getId().equals(selectedAddressId))
                            .findFirst()
                            .orElse(null)
                    : (!addresses.isEmpty() ? addresses.get(0) : null);
            model.addAttribute("selectedAddress", selectedAddress);
            model.addAttribute("address", address);

            // Tiện ích số học
            NumberUtils numberUtils = new NumberUtils();
            model.addAttribute("numberUtils", numberUtils);

            return "/user/home/cart2";
        }
        return "redirect:/login";
    }

    @PostMapping("/updateAddress")
    public String updateAddress(@RequestParam("addressId") Long addressId, RedirectAttributes redirectAttributes) {
        redirectAttributes.addAttribute("selectedAddressId", addressId);
        return "redirect:/TTAP/cart/view"; // Quay lại trang giỏ hàng sau khi chọn
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
        redirectAttributes.addFlashAttribute("removemessage", true);
        redirectAttributes.addFlashAttribute("alertType", "success"); // Loại thông báo
        return "redirect:/TTAP/cart/view"; // Điều hướng đến trang giỏ hàng
    }

    // Tạo đơn hàng từ giỏ hàng
    @GetMapping("/checkout")
    public String checkout(@RequestParam(value = "selectedProductIds", required = false)
                           List<Long> selectedProductIds, Authentication authentication, RedirectAttributes redirectAttributes) {
        if (authentication != null) {
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            User user = customUserDetail.getUser();
            try {
                List<CartItemDTO> selectedItems = getSelectedItemsFromCart(user, selectedProductIds);
                GioHang order = gioHangService.createOrderFromCart(user, selectedItems); // Tạo đơn hàng từ giỏ hàng
                redirectAttributes.addFlashAttribute("message", "Đơn hàng đã được tạo thành công.");
                return "redirect:/TTAP/order/view/" + order.getId(); // Điều hướng đến trang xem đơn hàng
            } catch (RuntimeException e) {
                redirectAttributes.addFlashAttribute("error", e.getMessage()); // Hiển thị lỗi nếu giỏ hàng trống
            }
        }
        return "redirect:/TTAP/cart/view"; // Quay lại trang giỏ hàng nếu có lỗi
    }

//    @PostMapping("/checkout")
//    public String checkout(
//            @RequestParam("selectedProductIds") List<Long> selectedProductIds,
//            @RequestParam("fullName") String fullName,
//            @RequestParam("phoneNumber") String phoneNumber,
//            @RequestParam("address") String address,
//            HttpServletRequest request, Authentication authentication, Model model
//            ) {
//        if (authentication != null) {
//            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
//            User user = customUserDetail.getUser();
//
//            // Tạo đơn hàng từ các sản phẩm được chọn
//            List<CartItemDTO> selectedItems = getSelectedItemsFromCart(user, selectedProductIds);
//
//            try {
//                GioHang order = gioHangService.createOrderFromCart(user, selectedItems);
//                return "redirect:/TTAP/order/view/" + order.getId(); // Điều hướng đến trang xem đơn hàng
//            } catch (Exception e) {
//                return "redirect:/view?error=" + e.getMessage(); // Nếu có lỗi, quay lại giỏ hàng
//            }
//        }
//        return "redirect:/view";
//    }

    private List<CartItemDTO> getSelectedItemsFromCart(User user, List<Long> selectedProductIds) {
        // Lấy giỏ hàng của user và lọc các sản phẩm được chọn
        GioHang cart = gioHangService.getOrCreateCart(user);
        return cart.getItems().stream()
                .filter(item -> selectedProductIds.contains(item.getChiTietSanPham().getId()))
                .map(item -> new CartItemDTO(item.getChiTietSanPham().getId(), item.getChiTietSanPham().getSanPham().getTen(),
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
                               @RequestParam String diaChi,
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
                    return "redirect:/TTAP/cart/view"; // Quay lại trang giỏ hàng
                }
                List<Long> selectedProductIds = Arrays.stream(selectedProductIdsStr.split(","))
                        .map(Long::parseLong)
                        .collect(Collectors.toList());
                HoaDon hoaDon = gioHangService.checkoutCart(user, selectedProductIds, diaChi);
                redirectAttributes.addFlashAttribute("message", "Hóa đơn đã được tạo thành công!");
                model.addAttribute("userLogged", user);
                return "redirect:/TTAP/cart/hoa-don"; // Chuyển đến trang chi tiết hóa đơn
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Lỗi khi tạo hóa đơn: " + e.getMessage());
                return "redirect:/TTAP/cart/view"; // Quay lại trang giỏ hàng
            }
        }
        return "redirect:/login"; // Nếu chưa đăng nhập, chuyển hướng đến trang login
    }

    @PostMapping("/add-to-cart")
    public String addItemToCart(@RequestParam Long productId,
                                @RequestParam int quantity,
                                @RequestParam(required = false) Long sizeId,
                                @RequestParam(required = false) Long colorId,
                                RedirectAttributes redirectAttributes,
                                Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login?redirect=" + "/TTAP/san-pham-detail/" + productId;
        }

        CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
        User user = customUserDetail.getUser();

        if (sizeId == null) {
            redirectAttributes.addFlashAttribute("errorSize", "Vui lòng chọn kích thước.");
            return "redirect:/TTAP/san-pham-detail/" + productId;
        }

        if (colorId == null) {
            redirectAttributes.addFlashAttribute("erroColor", "Vui lòng chọn màu sắc.");
            return "redirect:/TTAP/san-pham-detail/" + productId;
        }

        AddToCartRequest request = new AddToCartRequest();
        request.setProductId(productId);
        request.setQuantity(quantity);
        request.setSize(sizeId);
        request.setColor(colorId);

        try {
            gioHangService.addToCart(user, request);
            redirectAttributes.addFlashAttribute("message", true);
        } catch (GioHangService.ProductNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", "Sản phẩm không tồn tại: " + e.getMessage());
        } catch (GioHangService.InsufficientStockException e) {
            redirectAttributes.addFlashAttribute("error", "Không đủ hàng: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi không xác định.");
        }

        return "redirect:/TTAP/san-pham-detail/" + productId;
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
                return "redirect:/TTAP/cart/view"; // Quay lại trang giỏ hàng
            }
        }
        return "redirect:/login"; // Nếu chưa đăng nhập, chuyển hướng đến trang login
    }

    @GetMapping("/hoa-don-chi-tiet/hien-thi")
    public String hienThi(@RequestParam Long id, Model model, Authentication authentication) {
        if (authentication != null) {
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            User user = customUserDetail.getUser();

            HoaDon hoaDon = hoaDonService.getDonHang(id);
            model.addAttribute("hoaDon", hoaDon);
            model.addAttribute("listHDCT", hdctr.getHoaDonChiTietByHoaDonId(id));
            NumberUtils numberUtils = new NumberUtils();
            model.addAttribute("numberUtils", numberUtils);
            model.addAttribute("userLogged", user);
            return "/user/home/cart"; // Đường dẫn đến view danh sách hóa đơn

        }
        return "redirect:/login";
    }

    @PostMapping("/updateProductQuantity")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateProductQuantity(@RequestParam Long productId,
                                                                     @RequestParam int newQuantity,
                                                                     Authentication authentication) {
        if (authentication != null) {
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            User user = customUserDetail.getUser();

            try {
                // Lấy hoặc tạo giỏ hàng cho người dùng
                GioHang cart = gioHangService.getOrCreateCart(user);

                // Cập nhật số lượng sản phẩm trong giỏ
                gioHangService.updateProductQuantity(cart, productId, newQuantity);

                // Chuẩn bị dữ liệu trả về
                Map<String, Object> response = new HashMap<>();
                response.put("newQuantity", newQuantity);

                return ResponseEntity.ok(response);

            } catch (IllegalArgumentException e) {
                // Xử lý lỗi nếu sản phẩm không tồn tại trong giỏ hàng
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", e.getMessage());
                return ResponseEntity.badRequest().body(errorResponse);
            } catch (Exception e) {
                // Xử lý các lỗi không mong muốn khác
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "An unexpected error occurred: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("error", "User not authenticated"));
    }

    @PostMapping("/getdiscount")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> applyDiscount(@RequestParam String code) {
        // Lấy mã giảm giá từ service
        MaGiamGia discount = discountService.getMaGiamGia(code);
        Map<String, Object> response = new HashMap<>();
        // Kiểm tra nếu mã giảm giá hợp lệ
        if (discount != null) {
            response.put("discount", Map.of("discountPercentage", discount.getGiaTriGiam()));
            response.put("message", "Mã giảm giá hợp lệ");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Mã giảm giá không hợp lệ hoặc đã hết hạn");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

}
