package com.project.ttaptshirt.controller.adminController;

import com.project.ttaptshirt.entity.DiaChi;
import com.project.ttaptshirt.entity.User;
import com.project.ttaptshirt.repository.UserRepo;
import com.project.ttaptshirt.security.CustomUserDetail;
import com.project.ttaptshirt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/nhanvien")
public class NhanVienController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepo userRepo;

    @GetMapping("/view")
    public String getEmployees(
            Authentication authentication, @RequestParam("p") Optional<Integer> page,
            Model model) {

        if (authentication != null) {
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            User user = customUserDetail.getUser();
            model.addAttribute("userLogged", user);
        }
        Pageable pageable = PageRequest.of(page.orElse(0), 5);
        Page<User> employeesPage = userService.findAllNv("ROLE_EMPLOYEE", pageable);

        model.addAttribute("emp", employeesPage);
        return "/admin/nhanvien/index";
    }

    @GetMapping("/register")
    public String register(Authentication authentication, Model model) {
        if (authentication != null) {
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            User user = customUserDetail.getUser();
            model.addAttribute("userLogged", user);
        }
        User user = new User();
        user.setGioiTinh(true);
        model.addAttribute("user", user);
        return "/admin/nhanvien/register";
    }

    @PostMapping("/register")
    public String createNewUser(@ModelAttribute("user") User user,
                                @RequestParam("password") String passwordString,
                                RedirectAttributes redirectAttributes,
                                Model model) {

        // Kiểm tra username đã tồn tại
        if (userRepo.findUserByUsername(user.getUsername()) != null) {
            model.addAttribute("usernameIsInvalid", "Tài khoản đã tồn tại");
            return "/admin/nhanvien/register"; // Đảm bảo trang không redirect mà giữ lại modal
        }

        // Kiểm tra tên có hợp lệ
        if (!user.getHoTen().matches("^[a-zA-ZÀ-ỹ\\s]+$")) {
            model.addAttribute("nameError", "Tên không hợp lệ");
            return "/admin/nhanvien/register";
        }

        // Kiểm tra email đã tồn tại
        if (userRepo.findUserByEmail(user.getEmail()) != null) {
            model.addAttribute("emailIsInvalid", "Email đã tồn tại");
            return "/admin/nhanvien/register";
        }

        // Kiểm tra số điện thoại có hợp lệ
        if (!user.getSoDienthoai().matches("\\d+")) {
            model.addAttribute("ErrorPhoneNumber", "Số điện thoại không hợp lệ");
            return "/admin/nhanvien/register";
        }

        // Kiểm tra trường trống trong controller
        if (user.getUsername().trim().isEmpty()) {
            model.addAttribute("usernameError", "Tên đăng nhập không được để trống");
            return "/admin/nhanvien/register";
        }
        if (user.getHoTen().trim().isEmpty()) {
            model.addAttribute("nameError", "Họ tên không được để trống");
            return "/admin/nhanvien/register";
        }
        if (user.getEmail().trim().isEmpty()) {
            model.addAttribute("emailIsInvalid", "Email không được để trống");
            return "/admin/nhanvien/register";
        }
        if (user.getSoDienthoai().trim().isEmpty()) {
            model.addAttribute("ErrorPhoneNumber", "Số điện thoại không được để trống");
            return "/admin/nhanvien/register";
        }


        // Mã hóa mật khẩu và lưu user
        String encodedPassword = new BCryptPasswordEncoder().encode(passwordString);
        user.setPassword(encodedPassword);
        user.setEnable(true); // Kích hoạt tài khoản
        user.setNgayTao(LocalDate.now());

        // Lưu vào cơ sở dữ liệu
        userService.save(user);

        // Gán quyền mặc định (Role nhân viên)
        userService.insertDefaultNvRole(user.getId());

        // Thông báo thành công
        redirectAttributes.addFlashAttribute("isRegisterSuccess", true);
        return "redirect:/admin/nhanvien/view"; // Trả về trang danh sách nhân viên
    }

    @GetMapping("/deactivate/{id}")
    public String deactivateUser(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findById(id);
            if (user != null) {
                user.setEnable(false); // Đặt trạng thái enable thành false để ngưng hoạt động
                userService.save(user); // Lưu lại người dùng với trạng thái mới
                redirectAttributes.addFlashAttribute("deratisuccess", true);
            } else {
                redirectAttributes.addFlashAttribute("error", "User not found.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deactivating user: " + e.getMessage());
        }
        return "redirect:/admin/nhanvien/view";
    }

    @GetMapping("/activate/{id}")
    public String activateUser(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findById(id);
            if (user != null) {
                user.setEnable(true); // Đặt trạng thái enable thành false để ngưng hoạt động
                userService.save(user); // Lưu lại người dùng với trạng thái mới
                redirectAttributes.addFlashAttribute("success", "User deactivated successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "User not found.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deactivating user: " + e.getMessage());
        }
        return "redirect:/admin/nhanvien/view";
    }

    @GetMapping("/detail/{id}")
    public String showUserDetails(@PathVariable("id") Long id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        return "/admin/nhanvien/update";
    }

    @PostMapping("/reset-password/{idUser}")
    public String resetPassword(@PathVariable("idUser") Long idUser, @RequestParam("newPassword") String newPassword,
                                RedirectAttributes redirectAttributes) {
        String passwordEncode = new BCryptPasswordEncoder().encode(newPassword);
        User user = userService.findById(idUser);
        user.setPassword(passwordEncode);
        userService.save(user);

        // Add a flash attribute to notify of successful password reset
        redirectAttributes.addFlashAttribute("isResetPasswordSuccess", true);

        return "redirect:/admin/nhanvien/view"; // Redirect to the list of employees page
    }


    @PostMapping("/updateUser/{id}")
    public String updateUser(@PathVariable("id") Long id, @ModelAttribute("user") User updatedUser,
                             RedirectAttributes redirectAttributes, Authentication authentication, Model model) {
        try {
            // Kiểm tra người dùng đã đăng nhập
            if (authentication != null) {
                CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
                User userLoggedIn = customUserDetail.getUser();
                redirectAttributes.addFlashAttribute("userLogged", userLoggedIn); // Lưu vào RedirectAttributes
            }

            // Lấy thông tin người dùng từ cơ sở dữ liệu
            User user = userService.findById(id);
            // Kiểm tra username đã tồn tại
            if (userRepo.findUserByUsernameUpdate(updatedUser.getUsername(), id) != null) {
                model.addAttribute("usernameIsInvalid", "Tài khoản đã tồn tại");
                return "/admin/nhanvien/update"; // Đảm bảo trang không redirect mà giữ lại modal
            }

            // Kiểm tra tên có hợp lệ
            if (!user.getHoTen().matches("^[a-zA-ZÀ-ỹ\\s]+$")) {
                model.addAttribute("nameError", "Tên không hợp lệ");
                return "/admin/nhanvien/update";
            }

            // Kiểm tra email đã tồn tại
            if (userRepo.findUserByEmailUpdate(updatedUser.getEmail(), id) != null) {
                model.addAttribute("emailIsInvalid", "Email đã tồn tại");
                return "/admin/nhanvien/update";
            }

            // Kiểm tra số điện thoại có hợp lệ
            if (!user.getSoDienthoai().matches("\\d+")) {
                model.addAttribute("ErrorPhoneNumber", "Số điện thoại không hợp lệ");
                return "/admin/nhanvien/update";
            }

            // Kiểm tra trường trống trong controller
            if (user.getUsername().trim().isEmpty()) {
                model.addAttribute("usernameError", "Tên đăng nhập không được để trống");
                return "/admin/nhanvien/update";
            }
            if (user.getHoTen().trim().isEmpty()) {
                model.addAttribute("nameError", "Họ tên không được để trống");
                return "/admin/nhanvien/update";
            }
            if (user.getEmail().trim().isEmpty()) {
                model.addAttribute("emailIsInvalid", "Email không được để trống");
                return "/admin/nhanvien/update";
            }
            if (user.getSoDienthoai().trim().isEmpty()) {
                model.addAttribute("ErrorPhoneNumber", "Số điện thoại không được để trống");
                return "/admin/nhanvien/update";
            }
            if (user != null) {
                // Cập nhật các trường từ form
                user.setHoTen(updatedUser.getHoTen());
                user.setUsername(updatedUser.getUsername());
                user.setGioiTinh(updatedUser.getGioiTinh());
                user.setSoDienthoai(updatedUser.getSoDienthoai());
                user.setEmail(updatedUser.getEmail());
                user.setUsername(updatedUser.getUsername()); // Kiểm tra username đã có chưa?

                // Lưu lại thay đổi vào cơ sở dữ liệu
                userService.save(user);

                // Thêm thông báo thành công
                redirectAttributes.addFlashAttribute("success", true);
            } else {
                // Nếu không tìm thấy người dùng
                redirectAttributes.addFlashAttribute("error", "User not found.");
            }
        } catch (Exception e) {
            // Thông báo lỗi nếu có ngoại lệ xảy ra
            redirectAttributes.addFlashAttribute("error", "Error updating user: " + e.getMessage());
        }

        // Chuyển hướng về danh sách người dùng sau khi cập nhật
        return "redirect:/admin/nhanvien/view";
    }


}
