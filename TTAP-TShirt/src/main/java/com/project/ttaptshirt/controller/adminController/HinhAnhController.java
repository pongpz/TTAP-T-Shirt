package com.project.ttaptshirt.controller.adminController;


import com.project.ttaptshirt.entity.HinhAnh;
import com.project.ttaptshirt.repository.HinhAnhRepository;
import com.project.ttaptshirt.service.HinhAnhService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("")
public class HinhAnhController {

    @Autowired
    HinhAnhService hinhAnhService;
    @Autowired
    private HinhAnhRepository hinhAnhRepository;


    @GetMapping("/hinhanh")
    public String themAnh(){

        return "/admin/sanpham/anh";
    }

    @PostMapping("/images/add")
    public String uploadMultipleImagesToModal(@RequestParam("files") MultipartFile[] files,
                                              RedirectAttributes redirectAttributes) {
        try {
            for (MultipartFile file : files) {
                String imageUrl = hinhAnhService.uploadFile(file);

                HinhAnh hinhAnh = new HinhAnh();
                hinhAnh.setPath(imageUrl);
                hinhAnh.setTrangThai(1);
                hinhAnh.setSanPham(null);
                hinhAnhRepository.save(hinhAnh);
            }
            // Thêm thông báo flash
            redirectAttributes.addFlashAttribute("uploadSuccess", true);
            return "redirect:/admin/sanpham/them-san-pham";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể tải ảnh lên: " + e.getMessage());
            return "redirect:/admin/san-pham/them-san-pham";
        }
    }
}
