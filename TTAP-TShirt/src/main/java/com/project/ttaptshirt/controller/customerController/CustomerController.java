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
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/view")
    String view(Model model, RedirectAttributes redirectAttributes,
                Authentication authentication) {
        if (authentication != null) {
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            TaiKhoan user = customUserDetail.getUser();
            model.addAttribute("userLogged", user);

            KhachHang khachHang = serKhachHang.findById(user.getKhachHang().getId()); // Lấy lại từ DB
            if (khachHang != null) {
                model.addAttribute("khachHang", khachHang);
            } else {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy khách hàng.");
                return "redirect:/login"; // Redirect nếu không tìm thấy khách hàng
            }


            return "/user/home/customer";
        }return "redirect:/login";

    }

    @PostMapping("/updateUser")
    public String updateUser(@ModelAttribute("khachHang") KhachHang khachHang,
                             RedirectAttributes redirectAttributes, Authentication authentication, Model model) {
        try {
            // Lấy thông tin KhachHang hiện tại từ cơ sở dữ liệu
            KhachHang customer = serKhachHang.findById(khachHang.getId());
            if (customer != null) {
                // Cập nhật thông tin của KhachHang (chỉ cập nhật các trường khách hàng)
                customer.setHoTen(khachHang.getHoTen());
                customer.setSoDienThoai(khachHang.getSoDienThoai());
                customer.setEmail(khachHang.getEmail());
                customer.setGioiTinh(khachHang.getGioiTinh());
                customer.setNgayTao(LocalDate.now());
                System.out.println("sex" + khachHang.getGioiTinh());
                // Lưu lại thông tin KhachHang đã cập nhật
                serKhachHang.save(customer);


                CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
                TaiKhoan user = customUserDetail.getUser();
                model.addAttribute("userLogged", user);

                KhachHang updatedKhachHang = user.getKhachHang();
                model.addAttribute("khachHang", updatedKhachHang);

                // Thêm thông báo thành công
                redirectAttributes.addFlashAttribute("success", "Khách hàng cập nhật thành công.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy khách hàng.");
            }
        } catch (Exception e) {
            // Xử lý lỗi nếu có
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật khách hàng: " + e.getMessage());
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
