package com.project.ttaptshirt.controller.customerController;


import com.project.ttaptshirt.entity.DiaChi;
import com.project.ttaptshirt.entity.KhachHang;
import com.project.ttaptshirt.entity.TaiKhoan;
import com.project.ttaptshirt.security.CustomUserDetail;
import com.project.ttaptshirt.service.DiaChiService;
import com.project.ttaptshirt.service.KhachHangService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/TTAP/user")

public class DiaChiController {

    @Autowired
    private DiaChiService serDc;

    @Autowired
    private KhachHangService khachHangService;

    @GetMapping("home")
    public String home(Model mol){
        List<DiaChi> dcList = serDc.findAll();
        mol.addAttribute("dcLst",dcList);
        return"/user/diachi/index";
    }

    @GetMapping("new")
    public String add(Model mol ){
        mol.addAttribute("diachi", new DiaChi());
        return "/user/diachi/dangky";
    }

    @PostMapping("save")
    public String createUser(@Valid @ModelAttribute DiaChi dc, BindingResult result, Model mol) {
        if (result.hasErrors()) {
            return "/user/diachi/dangky";
        }
        serDc.save(dc);
        return "redirect:home";
    }


    @GetMapping("/addresses")
    public String getUserAddresses(Model model, Authentication authentication) {
        if (authentication != null) {
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            TaiKhoan user = customUserDetail.getUser();

            List<DiaChi> addresses = serDc.findAddressesByUser(user.getId());
            model.addAttribute("addresses", addresses);

            return "user/address-list"; // Trang hiển thị danh sách địa chỉ
        }
        return "redirect:/login";
    }

    @PostMapping("/address")
    public String createAddress(@ModelAttribute DiaChi address, Authentication authentication,
                                RedirectAttributes redirectAttributes, Model model) {
        if (authentication == null) {
            return "redirect:/login"; // If not logged in, redirect to login
        }

        CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
        TaiKhoan user = customUserDetail.getUser();

        if (user == null || user.getId() == null) {
            redirectAttributes.addFlashAttribute("failAddress", true);
            System.out.println("log ra user failed !");
            return "redirect:/TTAP/cart/view";
        }

        System.out.println("User ID: " + user.getId());

        if (address.getSoNha() == null || "0".equals(address.getTenDuong()) ||
                "0".equals(address.getTenQuanhuyen()) || "0".equals(address.getTenThanhpho()) ||
                address.getHoTen() == null || address.getSoDienThoai() == null) {
            redirectAttributes.addFlashAttribute("failAddress", true);
            System.out.println("log ra user failed2 !");

            return "redirect:/TTAP/cart/view";
        }

        address.setTaiKhoan(user); // Associate the address with the current user
        try {

            serDc.save(address); // Save the address to the database
            redirectAttributes.addFlashAttribute("successAddress", true);
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("failAddress", true);
            System.out.println("log ra user failed3 !");

        }

        return "redirect:/TTAP/cart/view"; // Redirect to the address list after saving
    }


    @GetMapping("/address/update/{id}")
    public String addressUpdate(@PathVariable Long id,Authentication authentication,
                                Model model,RedirectAttributes redirectAttributes) {
        if (authentication != null) {
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            TaiKhoan user = customUserDetail.getUser();
            KhachHang khachHang = khachHangService.findById(user.getKhachHang().getId()); // Lấy lại từ DB
            if (khachHang != null) {
                model.addAttribute("khachHang", khachHang);
            } else {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy khách hàng.");
                return "redirect:/login"; // Redirect nếu không tìm thấy khách hàng
            }
            DiaChi address = serDc.findById(id);
            model.addAttribute("address", address);
            model.addAttribute("userLogged", user);
            return "/user/home/diachidetail"; // Sau khi lưu, chuyển hướng đến danh sách địa chỉ
        }
        return "redirect:/login";
    }

    @PostMapping("/address/update/{id}")
    public String updateAddress(@PathVariable Long id,
                                @ModelAttribute @Valid DiaChi updatedAddress,
                                BindingResult bindingResult,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (authentication == null) {
            return "redirect:/login"; // Nếu chưa đăng nhập, chuyển hướng đến trang login
        }

        try {
            // Lấy thông tin người dùng hiện tại
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            TaiKhoan currentUser = customUserDetail.getUser();

            // Tìm địa chỉ cần cập nhật
            Optional<DiaChi> optionalAddress = Optional.ofNullable(serDc.findById(id));

            if (optionalAddress.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Address not found.");
                return "redirect:/TTAP/cart/view";
            }

            DiaChi existingAddress = optionalAddress.get();

            // Kiểm tra quyền: Địa chỉ này có thuộc về người dùng hiện tại không?
            if (!existingAddress.getTaiKhoan().getId().equals(currentUser.getId())) {
                redirectAttributes.addFlashAttribute("error", "You are not authorized to update this address.");
                return "redirect:/TTAP/cart/view";
            }

            // Kiểm tra validate dữ liệu
            if (bindingResult.hasErrors()) {
                redirectAttributes.addFlashAttribute("error", "Invalid address data. Please check your input.");
                return "redirect:/TTAP/cart/view";
            }

            // Cập nhật thông tin địa chỉ
            existingAddress.setSoNha(updatedAddress.getSoNha());
            existingAddress.setTenDuong(updatedAddress.getTenDuong());
            existingAddress.setTenQuanhuyen(updatedAddress.getTenQuanhuyen());
            existingAddress.setTenThanhpho(updatedAddress.getTenThanhpho());


            serDc.save(existingAddress); // Lưu địa chỉ sau khi cập nhật
            redirectAttributes.addFlashAttribute("selectedAddress", existingAddress);
            redirectAttributes.addFlashAttribute("success", "Địa chỉ đã sửa thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Sửa địa chỉ lỗi");
        }

        return "redirect:/TTAP/cart/view";
    }


}
