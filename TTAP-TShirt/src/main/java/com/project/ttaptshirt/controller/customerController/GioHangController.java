package com.project.ttaptshirt.controller.customerController;

import com.project.ttaptshirt.dto.AddToCartRequest;
import com.project.ttaptshirt.dto.CartItemDTO;
import com.project.ttaptshirt.dto.NumberUtils;
import com.project.ttaptshirt.entity.*;
import com.project.ttaptshirt.repository.HinhAnhRepository;
import com.project.ttaptshirt.repository.HoaDonChiTietRepository;
import com.project.ttaptshirt.repository.MaGiamGiaRepo;
import com.project.ttaptshirt.repository.UserRepo;
import com.project.ttaptshirt.security.CustomUserDetail;
import com.project.ttaptshirt.service.DiaChiService;
import com.project.ttaptshirt.service.impl.ChiTietSanPhamServiceImpl;
import com.project.ttaptshirt.service.HoaDonLogService;
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

import java.time.LocalDateTime;
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

    @Autowired
    private HinhAnhRepository hinhAnhRepository;

    @Autowired

    private ChiTietSanPhamServiceImpl chiTietSanPhamService;

    @Autowired
    private HoaDonLogService hoaDonLogService;



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
                    : user.getDefaultAddress();
            model.addAttribute("selectedAddress", selectedAddress);
            model.addAttribute("address", address);

            List<GioHangChiTiet> cartItems = cart.getItems();

            // Lấy hình ảnh đầu tiên cho mỗi sản phẩm
            Map<Long, String> productImages = new HashMap<>();
            Map<Long, Integer> productStockQuantities = new HashMap<>();
            for (GioHangChiTiet item : cartItems) {
                Long productId = item.getChiTietSanPham().getSanPham().getId();

                // Lấy hình ảnh đầu tiên
                List<String> images = hinhAnhRepository.findBySanPhamId(productId);
                String firstImage = images.isEmpty() ? "/default-image.jpg" : images.get(0);
                productImages.put(productId, firstImage);

                // Lấy số lượng sản phẩm trong kho
                Integer stockQuantity = item.getChiTietSanPham().getSoLuong(); // Lấy số lượng trong kho của sản phẩm
                productStockQuantities.put(productId, stockQuantity);
            }
            model.addAttribute("productImages", productImages);
            model.addAttribute("productStockQuantities", productStockQuantities); // Thêm số lượng vào mô hình


            // Tiện ích số học
            NumberUtils numberUtils = new NumberUtils();
            model.addAttribute("numberUtils", numberUtils);

            return "/user/home/cart2";
        }
        return "redirect:/login";
    }

    @PostMapping("/updateAddress")
    public String updateAddress(@RequestParam("addressId") Long addressId,Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        DiaChi selectedAddress = serDc.findById(addressId);

        // Cập nhật địa chỉ mặc định cho người dùng
        CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
        User user = customUserDetail.getUser();
        user.setDefaultAddress(selectedAddress); // Giả sử bạn có phương thức để gán địa chỉ mặc định
        userRepo.save(user);  // Lưu người dùng với địa chỉ mặc định
        redirectAttributes.addAttribute("selectedAddressId", addressId);
        redirectAttributes.addFlashAttribute("successDcMessage", true);
        return "redirect:/TTAP/cart/view"; // Quay lại trang giỏ hàng sau khi chọn
    }

    @PostMapping("/clearCart")
    public String clearCart(Authentication authentication, RedirectAttributes redirectAttributes) {
        if (authentication != null) {
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            User user = customUserDetail.getUser();

            // Lấy giỏ hàng hiện tại của người dùng
            GioHang cart = gioHangService.getOrCreateCart(user);

            // Xóa tất cả sản phẩm trong giỏ hàng
            cart.getItems().clear();

            // Lưu giỏ hàng đã cập nhật


            // Thêm thông báo thành công
            redirectAttributes.addFlashAttribute("message", "Giỏ hàng đã được làm trống.");

            return "redirect:/TTAP/cart/view"; // Quay lại trang giỏ hàng sau khi xóa hết sản phẩm
        }
        return "redirect:/login"; // Nếu chưa đăng nhập, chuyển hướng đến trang login
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

    private List<CartItemDTO> getSelectedItemsFromCart(User user, List<Long> selectedProductIds) {
        // Lấy giỏ hàng của user và lọc các sản phẩm được chọn
        GioHang cart = gioHangService.getOrCreateCart(user);
        return cart.getItems().stream()
                .filter(item -> selectedProductIds.contains(item.getChiTietSanPham().getId()))
                .map(item -> new CartItemDTO(item.getChiTietSanPham().getId(), item.getChiTietSanPham().getSanPham().getTen(),
                        item.getGia(), item.getSoLuong()))
                .collect(Collectors.toList());
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
                // Kiểm tra số lượng và trạng thái sản phẩm
                for (Long productId : selectedProductIds) {
                    ChiTietSanPham productDetail = chiTietSanPhamService.findById(productId);

                    // Kiểm tra nếu không tìm thấy sản phẩm
                    if (productDetail == null) {
                        // Loại bỏ sản phẩm khỏi giỏ hàng nếu cần thiết
                        gioHangService.removeProductFromCart(user, productId);
                        redirectAttributes.addFlashAttribute("error",
                                "Sản phẩm với ID " + productId + " không tồn tại hoặc đã bị xóa.");
                        return "redirect:/TTAP/cart/view"; // Quay lại trang giỏ hàng
                    }

                    // Kiểm tra trạng thái sản phẩm
                    if (productDetail.getTrangThai() != 1) {
                        // Loại bỏ sản phẩm khỏi giỏ hàng nếu cần thiết
                        gioHangService.removeProductFromCart(user, productId);
                        redirectAttributes.addFlashAttribute("error",
                                "Sản phẩm " + productDetail.getSanPham().getTen() +
                                        " (Size: " + productDetail.getKichCo().getTen() +
                                        ", Màu sắc: " + productDetail.getMauSac().getTen() + ") không còn hoạt động.");
                        return "redirect:/TTAP/cart/view"; // Quay lại trang giỏ hàng
                    }

                    // Kiểm tra số lượng sản phẩm
                    if (productDetail.getSoLuong() <= 0) {
                        // Loại bỏ sản phẩm khỏi giỏ hàng nếu cần thiết
                        gioHangService.removeProductFromCart(user, productId);
                        redirectAttributes.addFlashAttribute("error",
                                "Sản phẩm " + productDetail.getSanPham().getTen() +
                                        " (Size: " + productDetail.getKichCo().getTen() +
                                        ", Màu sắc: " + productDetail.getMauSac().getTen() + ") đã hết hàng.");
                        return "redirect:/TTAP/cart/view"; // Quay lại trang giỏ hàng
                    }
                }
                HoaDon hoaDon = gioHangService.checkoutCart(user, selectedProductIds, diaChi);
                redirectAttributes.addFlashAttribute("message", true);

                HoaDonLog hoaDonLog = new HoaDonLog();
                hoaDonLog.setHoaDon(hoaDon);
                hoaDonLog.setHanhDong("Đặt hàng");
                hoaDonLog.setThoiGian(LocalDateTime.now());
                hoaDonLog.setNguoiThucHien(user.getSoDienthoai());
                hoaDonLog.setGhiChu("đã thực hiện đặt hàng online");
                hoaDonLog.setTrangThai(0);
                hoaDonLogService.save(hoaDonLog);


                model.addAttribute("userLogged", user);
                return "redirect:/TTAP/cart/view"; // Chuyển đến trang chi tiết hóa đơn
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

    @PostMapping("/huy-hoa-don-online")
    public String huyHDOnline(@RequestParam("idHD") Long idHD,RedirectAttributes redirectAttributes,Authentication authentication){
        CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
        User user = customUserDetail.getUser();
        hoaDonService.huyHoaDonOnline(idHD,user.getSoDienthoai()+" đã thực hiện hủy hóa đơn !");
        redirectAttributes.addFlashAttribute("cancelHoaDon", true);
        return "redirect:/TTAP/cart/hoa-don-chi-tiet/hien-thi?id=" + idHD;
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

    @GetMapping("/deleteAddress/{id}")
    public String deleteAddress(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            serDc.deleteAddress(id); // Xử lý xóa trong service
            redirectAttributes.addFlashAttribute("successRemoveaddress", "Địa chỉ đã được xóa thành công.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorRemoveaddress", "Xóa địa chỉ thất bại. Vui lòng thử lại.");
        }
        return "redirect:/TTAP/cart/view"; // Quay lại trang giỏ hàng
    }


}
