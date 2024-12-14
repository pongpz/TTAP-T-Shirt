package com.project.ttaptshirt.controller.customerController;

import com.project.ttaptshirt.entity.User;
import com.project.ttaptshirt.security.CustomUserDetail;
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

@Controller
@RequestMapping("/TTAP/user/detail")
public class CustomerController {
    @Autowired
    private UserServiceImpl serUser;
    @Autowired
    private UserServiceImpl userServiceImpl;

    @GetMapping("/view")
    String view(Model model, RedirectAttributes redirectAttributes,
                Authentication authentication) {
        if (authentication != null) {
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            User user = customUserDetail.getUser();
            model.addAttribute("userLogged", user);
            return "/user/home/customer";
        }return "redirect:/login";

    }

    @PostMapping("/updateUser")
    public String updateUser(@ModelAttribute("userLogged") User updatedUser, @RequestParam("password") String passwordString,
                             RedirectAttributes redirectAttributes, Authentication authentication) {
        try {
            // Lấy thông tin người dùng đã đăng nhập từ session hoặc authentication
            User user = serUser.findById(updatedUser.getId());

            if (user != null) {
                // Mã hóa mật khẩu
                String password = new BCryptPasswordEncoder().encode(passwordString);

                // Cập nhật thông tin người dùng
                user.setHoTen(updatedUser.getHoTen());
                user.setUsername(updatedUser.getUsername());
                user.setSoDienthoai(updatedUser.getSoDienthoai());
                user.setEmail(updatedUser.getEmail());
                user.setPassword(password); // Cập nhật mật khẩu đã mã hóa

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
}
