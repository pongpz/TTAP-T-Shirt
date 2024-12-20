package com.project.ttaptshirt.controller.customerController;

import com.project.ttaptshirt.entity.KhachHang;
import com.project.ttaptshirt.entity.TaiKhoan;
import com.project.ttaptshirt.security.CustomUserDetail;
import com.project.ttaptshirt.service.impl.KhachHangServiceImpl;
import com.project.ttaptshirt.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/TTAP/user/detail")
public class CustomerController {
    @Autowired
    private UserServiceImpl serUser;
    @Autowired
    private UserServiceImpl userServiceImpl;

    @Autowired
    private KhachHangServiceImpl serKhachHang;

    @GetMapping("/view")
    String view(Model model, RedirectAttributes redirectAttributes,
                Authentication authentication) {
        if (authentication != null) {
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            TaiKhoan user = customUserDetail.getUser();
            model.addAttribute("userLogged", user);
            return "/user/home/customer";
        }return "redirect:/login";

    }

    @PostMapping("/updateUser")
    public String updateUser(@ModelAttribute("userLogged") TaiKhoan updatedUser,
                             RedirectAttributes redirectAttributes, Authentication authentication) {
        try {
            // Lấy thông tin người dùng đã đăng nhập từ session hoặc authentication
            TaiKhoan user = serUser.findById(updatedUser.getId());

            if (user != null) {
                // Mã hóa mật khẩu
                // Cập nhật thông tin người dùng
                user.setUsername(updatedUser.getUsername());
           // Cập nhật mật khẩu đã mã hóa

                // Lấy thông tin khách hàng
                KhachHang khachHang = user.getKhachHang();  // Giả sử có phương thức để lấy khách hàng từ User

                // Cập nhật thông tin khách hàng
//                khachHang.setSoDienThoai(updatedUser.getSoDienthoai());  // Cập nhật số điện thoại khách hàng

                // Cập nhật thời gian tạo nếu cần
                khachHang.setNgayTao(LocalDate.now());  // Cập nhật ngày

                serKhachHang.save(khachHang);
                serUser.save(user); // Lưu thông tin vào cơ sở dữ liệu

                // Cập nhật lại thông tin người dùng trong session (sau khi thay đổi)
                CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
                customUserDetail.setUser(user); // Cập nhật thông tin người dùng trong customUserDetail

                // Tạo lại đối tượng Authentication với thông tin người dùng đã thay đổi
                UsernamePasswordAuthenticationToken updatedAuth = new UsernamePasswordAuthenticationToken(
                        customUserDetail, authentication.getCredentials(), authentication.getAuthorities());

                // Cập nhật lại Authentication trong SecurityContext
                SecurityContextHolder.getContext().setAuthentication(updatedAuth);

                // Thêm thông báo thành công
                redirectAttributes.addFlashAttribute("success", "User cập nhật thành công");
            } else {
                redirectAttributes.addFlashAttribute("error", "User không tìm thấy.");
            }
        } catch (Exception e) {
            // Xử lý lỗi nếu có
            redirectAttributes.addFlashAttribute("error", "Error updating user: " + e.getMessage());
        }

        // Điều hướng về trang chi tiết người dùng
        return "redirect:/TTAP/user/detail/view";
    }
    @PostMapping("/changePassword")
    public String changePassword(@RequestParam("currentPassword") String currentPasswordInput,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        // Lấy thông tin người dùng hiện tại
        CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
        TaiKhoan user = customUserDetail.getUser();

        // Mật khẩu hiện tại từ database
        String encryptedPasswordFromDb = user.getPassword();

        // Kiểm tra mật khẩu hiện tại có khớp không
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(currentPasswordInput, encryptedPasswordFromDb)) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu hiện tại không đúng.");
            redirectAttributes.addFlashAttribute("openChangePasswordModal", true);
            return "redirect:/TTAP/user/detail/view";
        }

        // Kiểm tra mật khẩu mới và xác nhận mật khẩu có khớp không
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu mới và xác nhận mật khẩu không trùng khớp.");
            redirectAttributes.addFlashAttribute("openChangePasswordModal", true);
            return "redirect:/TTAP/user/detail/view";
        }

        // Mã hóa mật khẩu mới
        String encryptedNewPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encryptedNewPassword);

        // Lưu người dùng vào cơ sở dữ liệu
        serUser.save(user);

        redirectAttributes.addFlashAttribute("success", "Đổi mật khẩu thành công.");
        return "redirect:/TTAP/user/detail/view";
    }

}
