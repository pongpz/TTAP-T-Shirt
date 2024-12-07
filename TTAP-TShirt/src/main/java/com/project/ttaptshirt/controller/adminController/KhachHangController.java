package com.project.ttaptshirt.controller.adminController;

import com.project.ttaptshirt.entity.DiaChi;
import com.project.ttaptshirt.entity.KhachHang;
import com.project.ttaptshirt.entity.User;
import com.project.ttaptshirt.security.CustomUserDetail;
import com.project.ttaptshirt.service.KhachHangService;
import com.project.ttaptshirt.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/admin/khach-hang")
public class KhachHangController {

    @Autowired
    private KhachHangService khachHangService;
    @Autowired
    private UserServiceImpl serUser;
    @Autowired
    private UserServiceImpl userServiceImpl;


    @GetMapping("/home")
    public String home(Model mol) {
        List<User> cusLst = serUser.findAll();
        System.out.println("danh sanh kh: " + cusLst);
        mol.addAttribute("cusLst", cusLst);
        return "user/khachhang/index";
    }

    @GetMapping("/view")
    public String viewChatLieu(Model mol, Authentication authentication, @RequestParam("p") Optional<Integer> page) {
        if (authentication != null) {
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            User user = customUserDetail.getUser();
            mol.addAttribute("userLogged", user);
        }
        Pageable pageable = PageRequest.of(page.orElse(0), 5);
        Page<User> userPage = serUser.findAll(pageable);
        mol.addAttribute("cus", userPage);
        return "user/khachhang/index";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
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
            if (khachHang.getSoDienThoai().equalsIgnoreCase(soDienThoai)) {
                redirectAttributes.addFlashAttribute("addCustomerFailed", true);
                redirectAttributes.addFlashAttribute("messageAddCustomer", "Số điện thoại đã tồn tại.");
                return "redirect:/admin/ban-hang/hoa-don/chi-tiet?hoadonId=" + idHD;
            }
        }
        try {
            // Save customer logic
            KhachHang khachHang = new KhachHang();
            khachHang.setHoTen(hoTen);
            khachHang.setSoDienThoai(soDienThoai);
            khachHang.setNgayTao(LocalDate.now());
            khachHangService.save(khachHang);
            redirectAttributes.addFlashAttribute("addCustomerSuccess", true);
            redirectAttributes.addFlashAttribute("messageAddCustomer", "Thêm khách hàng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("addCustomerFailed", true);
            redirectAttributes.addFlashAttribute("messageAddCustomer", "Thêm khách hàng thất bại!");
        }
        return "redirect:/admin/ban-hang/hoa-don/chi-tiet?hoadonId=" + idHD;
    }


    @GetMapping("/searchByPhoneNumber")
    public ResponseEntity<List<KhachHang>> searchKhachHangBySdt(@RequestParam("phoneNumber") String phoneNumber) {
        List<KhachHang> listKhachHang = khachHangService.searchCustomerByPhoneNumber(phoneNumber);
        return ResponseEntity.ok(listKhachHang);
    }

    // Xử lý việc đăng ký người dùng
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, @RequestParam("password") String password, RedirectAttributes redirectAttributes) {
        try {
            user.setPassword(new BCryptPasswordEncoder().encode(password));
            user.setEnable(true);
            user.setNgayTao(LocalDate.now());
            serUser.save(user);

            // Gán vai trò mặc định cho người dùng
            User newUser = serUser.findUserByUsername(user.getUsername());
            serUser.insertDefaultUserRole(newUser.getId());

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
        User user = serUser.findById(id);
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
            User user = serUser.findById(id);
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

    @GetMapping("/search")
    public String searchByPhoneNumber(@RequestParam("phoneNumber") String phoneNumber,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size,
                                      Model model) {
        // Tạo Pageable với trang và kích thước
        Pageable pageable = PageRequest.of(page, size);

        // Lấy danh sách người dùng theo số điện thoại và phân trang
        Page<User> userPage = userServiceImpl.searchByPhoneNumber(phoneNumber, pageable);

        // Thêm vào model để hiển thị
        model.addAttribute("cus", userPage);
        model.addAttribute("phoneNumber", phoneNumber);

        return "user/khachhang/index"; // Trả về trang danh sách người dùng
    }

    @PostMapping("/updateUser")
    public String updateUser(@PathVariable("id") Long id, @ModelAttribute("user") User updatedUser,
                             RedirectAttributes redirectAttributes) {
        try {
            User user = serUser.findById(id);
            if (user != null) {
                // Cập nhật thông tin từ form
                user.setHoTen(updatedUser.getHoTen());
                user.setUsername(updatedUser.getUsername());
                user.setGioiTinh(updatedUser.getGioiTinh());
                user.setSoDienthoai(updatedUser.getSoDienthoai());
                user.setEmail(updatedUser.getEmail());
                user.setUsername(updatedUser.getUsername());

                serUser.save(user); // Lưu lại thay đổi vào database
                redirectAttributes.addFlashAttribute("success", "User updated successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "User not found.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating user: " + e.getMessage());
        }
        return "redirect:/users"; // Điều hướng về danh sách người dùng sau khi cập nhật
    }

    @PostMapping("/update/{id}/DiaChi")
    public String updateDiaChi(@PathVariable("id") Long id,
                               @ModelAttribute("diaChi") DiaChi diaChi,
                               RedirectAttributes redirectAttributes) {
        try {
            // Gọi service để cập nhật địa chỉ
            serUser.updateDiachi(id, diaChi);
            // Gửi thông báo thành công
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật địa chỉ thành công!");
        } catch (Exception e) {
            // Gửi thông báo lỗi nếu có ngoại lệ
            redirectAttributes.addFlashAttribute("errorMessage", "Cập nhật địa chỉ thất bại: " + e.getMessage());
        }
        // Chuyển hướng về trang chi tiết của user
        return "redirect:/admin/users/detail/" + id;
    }

}
