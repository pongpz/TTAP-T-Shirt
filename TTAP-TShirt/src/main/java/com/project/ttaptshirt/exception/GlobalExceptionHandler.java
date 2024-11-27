package com.project.ttaptshirt.exception;

import com.project.ttaptshirt.service.impl.GioHangService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

//    @ExceptionHandler(GioHangService.ProductNotFoundException.class)
//    public String handleProductNotFound(GioHangService.ProductNotFoundException ex, RedirectAttributes redirectAttributes) {
//        // Thêm thông báo lỗi vào RedirectAttributes
//        redirectAttributes.addFlashAttribute("error", ex.getMessage());
//        return "redirect:/san-pham-detail"; // Quay lại trang chi tiết sản phẩm hoặc trang phù hợp
//    }
//
//    @ExceptionHandler(GioHangService.InsufficientStockException.class)
//    public String handleInsufficientStock(GioHangService.InsufficientStockException ex, RedirectAttributes redirectAttributes) {
//        // Thêm thông báo lỗi vào RedirectAttributes
//        redirectAttributes.addFlashAttribute("error", ex.getMessage());
//        return "redirect:/san-pham-detail"; // Quay lại trang chi tiết sản phẩm
//    }
//
//    // Xử lý lỗi chung
//    @ExceptionHandler(Exception.class)
//    public String handleGeneralException(Exception ex, RedirectAttributes redirectAttributes) {
//        // Ghi log lỗi (tùy chọn)
//        redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + ex.getMessage());
//        return "redirect:/san-pham-detail"; // Quay lại trang chi tiết sản phẩm
//    }

}
