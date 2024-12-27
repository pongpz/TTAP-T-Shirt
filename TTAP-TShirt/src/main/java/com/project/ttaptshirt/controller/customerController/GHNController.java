package com.project.ttaptshirt.controller.customerController;

import com.project.ttaptshirt.dto.DiaChiDTO;
import com.project.ttaptshirt.entity.DiaChi;
import com.project.ttaptshirt.service.DiaChiService;
import com.project.ttaptshirt.service.GHNService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/shipping")
public class GHNController {

    @Autowired
    private GHNService ghnService;

    // Phương thức tính phí vận chuyển và chuyển kết quả về trang cart2
    @PostMapping("/calculate")
    public String calculateShippingFee(@ModelAttribute DiaChi diaChi, Model model) {
        try {
            // Gọi service để tính phí vận chuyển
            Integer shippingFee = ghnService.calculateShippingFee(diaChi);

            if (shippingFee > 0) {
                // Thêm phí vận chuyển vào model
                model.addAttribute("shippingFee", shippingFee);
            } else {
                model.addAttribute("shippingFee", "Không thể tính phí vận chuyển.");
            }

        } catch (Exception e) {
            model.addAttribute("shippingFee", "Lỗi khi tính toán phí vận chuyển.");
        }

        // Chuyển hướng đến trang /user/home/cart2
        return "user/home/cart2";  // Trả về trang cart2 để hiển thị kết quả
    }


    @Autowired
    private DiaChiService diaChiService;

    @GetMapping("/api/address/{id}")
    @ResponseBody
    public DiaChiDTO getAddress(@PathVariable("id") Long id) {
        DiaChi diaChi = diaChiService.findById(id);
        if (diaChi == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy địa chỉ");
        }
        return new DiaChiDTO(diaChi);
    }

    // Method tính phí ship
    @PostMapping("/api/shipping/calculate")
    @ResponseBody
    public Map<String, Object> calculateShipping(@RequestBody DiaChi diaChi) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Gọi service tính phí ship
            Integer shippingFee = ghnService.calculateShippingFee(diaChi);
            response.put("status", "success");
            response.put("shippingFee", shippingFee);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Lỗi tính phí ship");
        }
        return response;
    }
}
