package com.project.ttaptshirt.controller.adminController;

import com.project.ttaptshirt.dto.NumberUtils;
import com.project.ttaptshirt.entity.*;
import com.project.ttaptshirt.security.CustomUserDetail;
import com.project.ttaptshirt.service.KhachHangService;
import com.project.ttaptshirt.service.impl.HoaDonServiceImpl;
import com.project.ttaptshirt.service.impl.KhachHangServiceImpl;
import com.project.ttaptshirt.service.impl.MaGiamGiaServicelmpl;
import com.project.ttaptshirt.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/khach-hang")
public class KhachHangController {

    @Autowired
    private KhachHangService khachHangService;
    @Autowired
    private UserServiceImpl serUser;
    @Autowired
    private UserServiceImpl userServiceImpl;
    @Autowired
    private KhachHangServiceImpl khachHangServiceImpl;
    @Autowired
    private MaGiamGiaServicelmpl maGiamGiaServicelmpl;
    @Autowired
    private HoaDonServiceImpl hoaDonServiceImpl;


    @GetMapping("/view")
    public String getEmployees(
            Authentication authentication, @RequestParam("p") Optional<Integer> page,
            Model model) {

        if (authentication != null) {
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            TaiKhoan user = customUserDetail.getUser();
            model.addAttribute("userLogged", user);
        }
        Pageable pageable = PageRequest.of(page.orElse(0), 5);
        Page<KhachHang> customsPage = khachHangServiceImpl.findAll(pageable);

        model.addAttribute("cus", customsPage);
        return "user/khachhang/index";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new TaiKhoan());
        return "user/khachhang/register";
    }

    @PostMapping("/them-nhanh-khach-hang")
    public String createQuickCustomer(
            @RequestParam("idInvoice") Long idHD,
            @RequestParam("hoTen") String hoTen,
            @RequestParam("soDienThoai") String soDienThoai,
            RedirectAttributes redirectAttributes) {
        // Validate inputs
        if (hoTen == null || hoTen.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("addCustomerFailed", true);
            redirectAttributes.addFlashAttribute("messageAddCustomer", "Họ tên không được để trống.");
            return "redirect:/admin/ban-hang/hoa-don/chi-tiet?hoadonId=" + idHD;
        }
        if (hoTen.length() < 8) {
            redirectAttributes.addFlashAttribute("addCustomerFailed", true);
            redirectAttributes.addFlashAttribute("messageAddCustomer", "Họ tên phải có ít nhất 8 ký tự.");
            return "redirect:/admin/ban-hang/hoa-don/chi-tiet?hoadonId=" + idHD;
        }
        if (soDienThoai == null || soDienThoai.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("addCustomerFailed", true);
            redirectAttributes.addFlashAttribute("messageAddCustomer", "Số điện thoại không được để trống.");
            return "redirect:/admin/ban-hang/hoa-don/chi-tiet?hoadonId=" + idHD;
        }
        if (!soDienThoai.matches("\\d+")) {
            redirectAttributes.addFlashAttribute("addCustomerFailed", true);
            redirectAttributes.addFlashAttribute("messageAddCustomer", "Số điện thoại phải chỉ chứa các chữ số.");
            return "redirect:/admin/ban-hang/hoa-don/chi-tiet?hoadonId=" + idHD;
        }
        if (soDienThoai.length() < 10) {
            redirectAttributes.addFlashAttribute("addCustomerFailed", true);
            redirectAttributes.addFlashAttribute("messageAddCustomer", "Số điện thoại phải có ít nhất 10 chữ số.");
            return "redirect:/admin/ban-hang/hoa-don/chi-tiet?hoadonId=" + idHD;
        }

        List<KhachHang> listKH = khachHangService.findAll();
        for (KhachHang khachHang : listKH) {
            if (!ObjectUtils.isEmpty(khachHang.getSoDienThoai()) && khachHang.getSoDienThoai().equalsIgnoreCase(soDienThoai)) {
                redirectAttributes.addFlashAttribute("addCustomerFailed", true);
                redirectAttributes.addFlashAttribute("messageAddCustomer", "Số điện thoại đã tồn tại.");
                return "redirect:/admin/ban-hang/hoa-don/chi-tiet?hoadonId=" + idHD;
            }
        }
        try {
            String password = "pass" + (int) (Math.random() * 10000); // Sinh password ngẫu nhiên
            String username = soDienThoai;
            String validPhoneNumber = "+84" + soDienThoai.substring(1);
            // Mã hóa mật khẩu
            String encodedPassword = new BCryptPasswordEncoder().encode(password);

            TaiKhoan taiKhoan = new TaiKhoan();
            taiKhoan.setUsername(username);
            taiKhoan.setPassword(encodedPassword);
            Role role = new Role();
            role.setId(Long.parseLong("2"));
            taiKhoan.setRole(role);

            // Save customer logic
            KhachHang khachHang = new KhachHang();
            khachHang.setHoTen(hoTen);
            khachHang.setSoDienThoai(soDienThoai);
            khachHang.setNgayTao(LocalDate.now());
            khachHang.setTaiKhoan(taiKhoan);

            userServiceImpl.save(taiKhoan);
            khachHangService.save(khachHang);



            redirectAttributes.addFlashAttribute("addCustomerSuccess", true);
            redirectAttributes.addFlashAttribute("messageAddCustomer", "Thêm khách hàng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("addCustomerFailed", true);
            redirectAttributes.addFlashAttribute("messageAddCustomer", "Thêm khách hàng thất bại!");
        }
        return "redirect:/admin/ban-hang/hoa-don/chi-tiet?hoadonId=" + idHD;
    }


    @GetMapping("/{customerId}/details")
    public String getKhachHangDetails(@PathVariable Long customerId, Model model,Authentication authentication) {
        if (authentication != null) {
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            TaiKhoan user = customUserDetail.getUser();
            model.addAttribute("userLogged", user);
        }
        KhachHang khachHang = khachHangService.findById(customerId);
        model.addAttribute("customer", khachHang);
        List<HoaDon> hoaDonList = hoaDonServiceImpl.getHoaDonByKhachHangId(customerId);
        model.addAttribute("hoaDonList", hoaDonList);
        NumberUtils numberUtils = new NumberUtils();
        model.addAttribute("numberUtils", numberUtils);
        return "/user/khachhang/detail";
    }


    @GetMapping("/searchByPhoneNumber")
    public ResponseEntity<List<KhachHang>> searchKhachHangBySdt(@RequestParam("phoneNumber") String phoneNumber) {
        List<KhachHang> listKhachHang = khachHangService.searchCustomerByPhoneNumber(phoneNumber);
        return ResponseEntity.ok(listKhachHang);
    }

    // Xử lý việc đăng ký người dùng
    @PostMapping("/register")
    public String registerUser(@ModelAttribute TaiKhoan user, @RequestParam("password") String password, RedirectAttributes redirectAttributes) {
        try {
            user.setPassword(new BCryptPasswordEncoder().encode(password));
            user.setEnable(true);
            user.setNgayTao(LocalDate.now());
            serUser.save(user);

            // Gán vai trò mặc định cho người dùng
            TaiKhoan newUser = serUser.findUserByUsername(user.getUsername());
//            serUser.insertDefaultUserRole(newUser.getId());

            redirectAttributes.addFlashAttribute("success", "User registered successfully!");
            return "redirect:/admin/users/view";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Registration failed: " + e.getMessage());
            return "redirect:/admin/users/register";
        }
    }


    // Hiển thị thông tin người dùng theo ID
    @GetMapping("/detail/{id}")
    public String showUserDetails(@PathVariable("id") Long id, Model model) {
        TaiKhoan user = serUser.findById(id);
        if (user != null) {
            DiaChi diaChi = new DiaChi();
            model.addAttribute("user", user);
            model.addAttribute("diaChi", diaChi);
            return "user/khachhang/update";
        } else {
            model.addAttribute("error", "User not found");
            return "redirect:/admin/users/view";
        }
    }

    // Thay đổi trạng thái của người dùng (ngưng hoạt động thay vì xóa)
    @GetMapping("/deactivate/{id}")
    public String deactivateUser(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            TaiKhoan user = serUser.findById(id);
            if (user != null) {
                user.setEnable(false); // Đặt trạng thái enable thành false để ngưng hoạt động
                serUser.save(user); // Lưu lại người dùng với trạng thái mới
                redirectAttributes.addFlashAttribute("success", "User deactivated successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "User not found.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deactivating user: " + e.getMessage());
        }
        return "redirect:/admin/users/view";
    }

//    @GetMapping("/search")
//    public String searchByPhoneNumber(@RequestParam("phoneNumber") String phoneNumber,
//                                      @RequestParam(defaultValue = "0") int page,
//                                      @RequestParam(defaultValue = "10") int size,
//                                      Model model) {
//        // Tạo Pageable với trang và kích thước
//        Pageable pageable = PageRequest.of(page, size);
//
//        // Lấy danh sách người dùng theo số điện thoại và phân trang
//        Page<TaiKhoan> userPage = userServiceImpl.searchByPhoneNumber(phoneNumber, pageable);
//
//        // Thêm vào model để hiển thị
//        model.addAttribute("cus", userPage);
//        model.addAttribute("phoneNumber", phoneNumber);
//
//        return "user/khachhang/index"; // Trả về trang danh sách người dùng
//    }

    @PostMapping("/updateUser")
    public String updateUser(@PathVariable("id") Long id, @ModelAttribute("user") TaiKhoan updatedUser, @RequestParam("password") String passwordString,
                             RedirectAttributes redirectAttributes) {
        try {
            TaiKhoan user = serUser.findById(id);
            if (user != null) {
                String password = new BCryptPasswordEncoder().encode(passwordString);
                // Cập nhật thông tin từ form
//                user.setHoTen(updatedUser.getHoTen());
                user.setUsername(updatedUser.getUsername());
//                user.setSoDienthoai(updatedUser.getSoDienthoai());
                user.setPassword(password);
                serUser.save(user); // Lưu lại thay đổi vào database
                redirectAttributes.addFlashAttribute("success", "User updated successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "User not found.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating user: " + e.getMessage());
        }
        return "redirect:/TTAP/user/detail/view"; // Điều hướng về danh sách người dùng sau khi cập nhật
    }

}
