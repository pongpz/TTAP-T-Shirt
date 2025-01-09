package com.project.ttaptshirt.controller.adminController;

import com.project.ttaptshirt.entity.NhanVien;
import com.project.ttaptshirt.entity.Role;
import com.project.ttaptshirt.entity.TaiKhoan;
import com.project.ttaptshirt.repository.ChucVuRepo;
import com.project.ttaptshirt.repository.UserRepo;
import com.project.ttaptshirt.security.CustomUserDetail;
import com.project.ttaptshirt.service.NhanVienService;
import com.project.ttaptshirt.service.UserService;
import com.project.ttaptshirt.service.impl.NhanVienServicelmpl;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequestMapping("/admin/nhanvien")
public class NhanVienController {
    @Autowired
    private NhanVienServicelmpl nhanVienServicelmpl;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ChucVuRepo roleRepo;

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
        Page<NhanVien> employeesPage = nhanVienServicelmpl.findAll(pageable);

        model.addAttribute("emp", employeesPage);
        return "/admin/nhanvien/index";
    }

    @GetMapping("/register")
    public String register(Authentication authentication, Model model) {
        if (authentication != null) {
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            TaiKhoan user = customUserDetail.getUser();
            model.addAttribute("userLogged", user);
        }
        TaiKhoan user = new TaiKhoan();
        NhanVien nv = new NhanVien();
        model.addAttribute("user", user);
        model.addAttribute("nv", nv);
        return "/admin/nhanvien/register";
    }

//    @PostMapping("/register")
//    public String createNewUser(TaiKhoan user, @RequestParam("password") String passwordString,
//                                RedirectAttributes redirectAttributes, Model model) {
//
//        if(userRepo.findUserByUsername(user.getUsername()) != null){
//            model.addAttribute("usernameIsInvalid","Tài khoản đã tồn tại");
//            model.addAttribute("user",user);
//            return "user/register";
//        }
//
////        else if (!user.getSoDienthoai().matches("\\d+")){
////            model.addAttribute("ErrorPhoneNumber","Số điện thoại không hợp lệ");
////            model.addAttribute("user",user);
////            return "user/register";
////        }
//        else {
//            String password = new BCryptPasswordEncoder().encode(passwordString);
//            user.setPassword(password);
//            user.setEnable(true);
//            user.setNgayTao(LocalDate.now());
//            Role role = new Role();
//            role.setId(Long.parseLong("2"));
//            user.setRole(role);
//            userService.save(user);
//            redirectAttributes.addFlashAttribute("isRegisterSuccess", true);
//            return "redirect:/login";
//        }
//
//    }

    @PostMapping("/register")
    public String createNewUser(@ModelAttribute("user") TaiKhoan user,
                                @ModelAttribute("nv") NhanVien nv,
                                @ModelAttribute("idChucVu") Long idCV,
                                @RequestParam("password") String passwordString,
                                RedirectAttributes redirectAttributes,
                                Model model) {

        // Kiểm tra username đã tồn tại
        if (userRepo.findUserByUsername(user.getUsername()) != null) {
            model.addAttribute("usernameIsInvalid", "Tài khoản đã tồn tại");
            return "/admin/nhanvien/register"; // Đảm bảo trang không redirect mà giữ lại modal
        }

        // Kiểm tra số điện thoại có hợp lệ
//        if (!user.getSoDienthoai().matches("\\d+")) {
//            model.addAttribute("ErrorPhoneNumber", "Số điện thoại không hợp lệ");
//            return "/admin/nhanvien/register";
//        }

        // Kiểm tra trường trống trong controller
        if (user.getUsername().trim().isEmpty()) {
            model.addAttribute("usernameError", "Tên đăng nhập không được để trống");
            return "/admin/nhanvien/register";
        }


//        if (user.getSoDienthoai().trim().isEmpty()) {
//            model.addAttribute("ErrorPhoneNumber", "Số điện thoại không được để trống");
//            return "/admin/nhanvien/register";
//        }


        // Mã hóa mật khẩu và lưu user
        String encodedPassword = new BCryptPasswordEncoder().encode(passwordString);
        user.setPassword(encodedPassword);
        user.setEnable(true); // Kích hoạt tài khoản
        user.setNgayTao(LocalDate.now());
        Role role = new Role();
        role.setId(idCV);
        user.setRole(role);
        nv.setTaiKhoan(user);
        // Lưu vào cơ sở dữ liệu
        userService.save(user);
        nhanVienServicelmpl.save(nv);

        // Gán quyền mặc định (Role nhân viên)
//        userService.insertDefaultNvRole(user.getId());

        // Thông báo thành công
        redirectAttributes.addFlashAttribute("isRegisterSuccess", true);
        return "redirect:/admin/nhanvien/view"; // Trả về trang danh sách nhân viên
    }

    @GetMapping("/deactivate/{id}")
    public String deactivateUser(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            TaiKhoan user = userService.findById(id);
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
            TaiKhoan user = userService.findById(id);
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
        NhanVien nhanVien = nhanVienServicelmpl.findById(id);
        TaiKhoan tk = nhanVien.getTaiKhoan();
        model.addAttribute("nv", nhanVien);
        model.addAttribute("user", tk);
        return "/admin/nhanvien/update";
    }

    @PostMapping("/reset-password/{idUser}")
    public String resetPassword(@PathVariable("idUser") Long idUser, @RequestParam("newPassword") String newPassword,
                                RedirectAttributes redirectAttributes) {
        String passwordEncode = new BCryptPasswordEncoder().encode(newPassword);
        TaiKhoan user = userService.findById(idUser);
        user.setPassword(passwordEncode);
        userService.save(user);

        // Add a flash attribute to notify of successful password reset
        redirectAttributes.addFlashAttribute("isResetPasswordSuccess", true);

        return "redirect:/admin/nhanvien/view"; // Redirect to the list of employees page
    }


    @Transactional
    @PostMapping("/updateUser/{id}")
    public String updateUser(@PathVariable("id") Long id, @ModelAttribute("nv") NhanVien nv,@ModelAttribute("idChucVu") Long idCV,
                             RedirectAttributes redirectAttributes, Authentication authentication, Model model) {
        try {
            NhanVien existingNv = nhanVienServicelmpl.findById(id);
            if (existingNv != null) {

                // Cập nhật các thông tin còn lại
                existingNv.setHoTen(nv.getHoTen());
                existingNv.setEmail(nv.getEmail());
                existingNv.setSoDienthoai(nv.getSoDienthoai());
                existingNv.setGioiTinh(nv.getGioiTinh());
                Role role = roleRepo.getReferenceById(idCV);
                TaiKhoan taiKhoan = userRepo.findByNhanVienId(id);
                taiKhoan.setRole(role);
                userRepo.save(taiKhoan);
                // Cập nhật người dùng
                nhanVienServicelmpl.save(existingNv);

                redirectAttributes.addFlashAttribute("message", "Cập nhật thành công.");
                return "redirect:/admin/nhanvien/view"; // Quay lại danh sách nhân viên
            } else {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy người dùng.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Cập nhật thất bại: " + e.getMessage());
        }

        // Chuyển hướng về danh sách người dùng sau khi cập nhật
        return "redirect:/admin/nhanvien/view";
    }


}
