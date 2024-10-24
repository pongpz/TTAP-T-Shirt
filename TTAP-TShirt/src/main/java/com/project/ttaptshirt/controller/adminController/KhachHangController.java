package com.project.ttaptshirt.controller.adminController;

import com.project.ttaptshirt.entity.DiaChi;
import com.project.ttaptshirt.entity.User;
import com.project.ttaptshirt.security.CustomUserDetail;
import com.project.ttaptshirt.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/users")
public class KhachHangController {
    @Autowired
    private UserServiceImpl serUser;


    @GetMapping("/home")
    public String home(Model mol){
        List<User> cusLst = serUser.findAll();
        System.out.println("danh sanh kh: " + cusLst);
        mol.addAttribute("cusLst",cusLst);
        return "user/khachhang/index";
    }

    @GetMapping("/view")
    public String viewChatLieu(Model mol, Authentication authentication, @RequestParam("p") Optional<Integer> page){
        if (authentication != null) {
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            User user = customUserDetail.getUser();
            mol.addAttribute("userLogged", user);
        }
        Pageable pageable = PageRequest.of(page.orElse(0),5);
        Page<User> userPage = serUser.findAll(pageable);
        mol.addAttribute("cus",userPage);
        return "user/khachhang/index";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "user/khachhang/register";
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
            model.addAttribute("user", user);
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
        return "redirect:/users";
    }

    @PostMapping("/update}")
    public String updateUser(@PathVariable("id") Long id, @ModelAttribute("user") User updatedUser,
                             RedirectAttributes redirectAttributes) {
        try {
            User user = serUser.findById(id);
            if (user != null) {
                // Cập nhật thông tin từ form
                user.setHoTen(updatedUser.getHoTen());
                user.setNgaySinh(updatedUser.getNgaySinh());
                user.setGioiTinh(updatedUser.getGioiTinh());
                user.setSoDienthoai(updatedUser.getSoDienthoai());
                user.setEmail(updatedUser.getEmail());
                user.setUsername(updatedUser.getUsername());
                user.setEnable(updatedUser.getEnable()); // Cập nhật trạng thái nếu cần

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
    public String getupdateDiachi(@PathVariable("id") Long id, @ModelAttribute DiaChi diaChi, Model mol){
        serUser.updateDiachi(id,diaChi);
        return "redirect:/admin/users/detail/"+ id;
    }

}
