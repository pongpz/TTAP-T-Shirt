package com.project.ttaptshirt.controller.adminController;

import com.project.ttaptshirt.dto.NumberUtils;
import com.project.ttaptshirt.entity.HoaDon;
import com.project.ttaptshirt.entity.HoaDonChiTiet;
import com.project.ttaptshirt.repository.HoaDonRepository;
import com.project.ttaptshirt.service.HoaDonChiTietService;
import com.project.ttaptshirt.service.HoaDonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin/hoa-don")
public class HoaDonController {

    @Autowired
    HoaDonRepository hr;

    @Autowired
    HoaDonService hoaDonService;
    @Autowired
    HoaDonChiTietService hoaDonChiTietService;

    @GetMapping("/hien-thi")
    public String hienThi(Model model, @RequestParam(defaultValue = "0") Integer page) {
        Pageable pageab = PageRequest.of(page, 5);
        model.addAttribute("listHD", hr.getAllHD(pageab));
        model.addAttribute("page", page);
        if (hr.getAllHD(pageab).size() == 0) {
            model.addAttribute("nullhd", "Không có hóa đơn nào");
        }
        NumberUtils numberUtils = new NumberUtils();
        model.addAttribute("numberUtils",numberUtils);
        return "admin/hoadon/hoa-don";
    }

//    @GetMapping("/hien-thi/online")
//    public String hienThiOnline(Model model, @RequestParam(defaultValue = "0") Integer page) {
//        Pageable pageab = PageRequest.of(page, 5);
//        model.addAttribute("listHD", hr.getAllHDOnline(pageab));
//        model.addAttribute("page", page);
//        if (hr.getAllHDOnline(pageab).size() == 0) {
//            model.addAttribute("nullhd", "Không có hóa đơn nào");
//        }
//        NumberUtils numberUtils = new NumberUtils();
//        model.addAttribute("numberUtils",numberUtils);
//        return "admin/hoadon/hoa-don-online";
//    }

//    @GetMapping("/chi-tiet-hoa-don-online/{idhd}")
//    public String hienThiHDCTOnline(@PathVariable("idhd") Long idhd, Model model){
//        HoaDon hoaDon = hoaDonService.findById(idhd);
//        model.addAttribute("hoaDon",hoaDon);
//        List<HoaDonChiTiet> listSPOrder = hoaDonChiTietService.getListHdctByIdHd(idhd);
//        model.addAttribute("listSPOrder",listSPOrder);
//        NumberUtils numberUtils = new NumberUtils();
//        model.addAttribute("numberUtils",numberUtils);
//        return "admin/hoadon/chi-tiet-hoa-don-online";
//    }
    @GetMapping("/xac-nhan-hoa-don/{idHD}")
    public String xacNhanHD(@PathVariable("idHD") Long idHD, RedirectAttributes redirectAttributes){
        hoaDonService.xacNhanHoaDon(idHD);

        // Add a flash attribute for success message
        redirectAttributes.addFlashAttribute("successMessage", "Đơn hàng đã được xác nhận!");

        return "redirect:/admin/hoa-don/chi-tiet-hoa-don-online/" + idHD;
    }

    @GetMapping("/hoa-don-cho-giao-hang/{idHD}")
    public String hdChoGiaoHang(@PathVariable("idHD") Long idHD, RedirectAttributes redirectAttributes){
        hoaDonService.hdChoGiaoHang(idHD);

        // Add a flash attribute for success message
        redirectAttributes.addFlashAttribute("successMessage", "Đơn hàng đã chuyển sang trạng thái chờ giao hàng!");

        return "redirect:/admin/hoa-don/chi-tiet-hoa-don-online/" + idHD;

    }

    @GetMapping("/xac-nhan-dang-giao-hang/{idHD}")
    public String xacNhanDangGiaoHang(@PathVariable("idHD") Long idHD, RedirectAttributes redirectAttributes){
        hoaDonService.xacNhanDangGiaoHang(idHD);

        // Add a flash attribute for success message
        redirectAttributes.addFlashAttribute("successMessage", "Đơn hàng đang giao!");

        return "redirect:/admin/hoa-don/chi-tiet-hoa-don-online/" + idHD;
    }




    @GetMapping("/hoan-thanh-hoa-don/{idHD}")
    public String hoanThanhHD(@PathVariable("idHD") Long idHD, RedirectAttributes redirectAttributes){
        hoaDonService.hoanThanhHoaDon(idHD);
        // Add a flash attribute for success message
        redirectAttributes.addFlashAttribute("successMessage", "Đơn hàng đã hoàn thành!");
        return "redirect:/admin/hoa-don/chi-tiet-hoa-don-online/" + idHD;
    }


    @GetMapping("/huy-hoa-don-online/{idHD}")
    public String huyHDOnline(@PathVariable("idHD") Long idHD){
        hoaDonService.huyHoaDonOnline(idHD);
        return "redirect:/admin/hoa-don/hien-thi/online";
    }


    @GetMapping("/tim-kiem")
    public String timKiem(
                          @RequestParam(required = false, value = "ma") String ma,
                          @RequestParam(required = false, value = "keyword") String keyword,
//                          @RequestParam(required = false, value = "tennv") String tennv,
//                          @RequestParam(required = false, value = "tenkh") String tenkh,
                          @RequestParam(required = false, value = "loaiDon") Integer loaiDon,
                          @RequestParam(required = false, value = "trangThai") Integer trangThai,
                          @RequestParam(value = "ngayThanhToan", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate ngayThanhToan,
                          @RequestParam(defaultValue = "0") Integer page,
                          Model model) {
        Pageable pageab = PageRequest.of(page, 5);
//        List<HoaDon> lsSearch = hr.search("", "", "", "", null, null, pageab);
        List<HoaDon> lsSearch = new ArrayList<>();
        if(keyword.trim().isEmpty()){
            lsSearch = hr.search2(ma.trim(),trangThai,ngayThanhToan,loaiDon,pageab);
        }else {
            lsSearch = hr.search(ma.trim(),keyword.trim(),trangThai,ngayThanhToan,loaiDon,pageab);
        }
        NumberUtils numberUtils = new NumberUtils();
        model.addAttribute("numberUtils",numberUtils);
//        System.out.println(lsSearch);
        model.addAttribute("listHD", lsSearch);
        model.addAttribute("ma", ma);
//        model.addAttribute("tennv", tennv);
//        model.addAttribute("tenkh", tenkh);
//        model.addAttribute("sdt", sdt);
        model.addAttribute("keyword", keyword.trim());
        model.addAttribute("ngayThanhToan", ngayThanhToan);
//        model.addAttribute("loaiDon", loaiDon);
        model.addAttribute("trangThai", trangThai);
        model.addAttribute("page", page);
        if (lsSearch.size() == 0) {
            model.addAttribute("nullhd", "Không có hóa đơn nào");
        }
        return "admin/hoadon/hoa-don-tim-kiem";
    }

    @GetMapping("/tim-kiem/online")
    public String timKiemOnline(
            @RequestParam(required = false, value = "ma") String ma,
            @RequestParam(required = false, value = "keyword") String keyword,
//                          @RequestParam(required = false, value = "tennv") String tennv,
//                          @RequestParam(required = false, value = "tenkh") String tenkh,
//                          @RequestParam(required = false, value = "loaiDon") Integer loaiDon,
            @RequestParam(required = false, value = "trangThai") Integer trangThai,
            @RequestParam(value = "ngayThanhToan", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate ngayThanhToan,
            @RequestParam(defaultValue = "0") Integer page,
            Model model) {
        Pageable pageab = PageRequest.of(page, 5);
//        List<HoaDon> lsSearch = hr.search("", "", "", "", null, null, pageab);
        List<HoaDon> lsSearch = new ArrayList<>();
        if(keyword.trim().isEmpty()){
            lsSearch = hr.search2(ma.trim(),trangThai,ngayThanhToan,0,pageab);
        }else {
            lsSearch = hr.search(ma.trim(),keyword.trim(),trangThai,ngayThanhToan,0,pageab);
        }
        NumberUtils numberUtils = new NumberUtils();
        model.addAttribute("numberUtils",numberUtils);
//        System.out.println(lsSearch);
        model.addAttribute("listHD", lsSearch);
        model.addAttribute("ma", ma);
//        model.addAttribute("tennv", tennv);
//        model.addAttribute("tenkh", tenkh);
//        model.addAttribute("sdt", sdt);
        model.addAttribute("keyword", keyword.trim());
        model.addAttribute("ngayThanhToan", ngayThanhToan);
//        model.addAttribute("loaiDon", loaiDon);
        model.addAttribute("trangThai", trangThai);
        model.addAttribute("page", page);
        if (lsSearch.size() == 0) {
            model.addAttribute("nullhd", "Không có hóa đơn nào");
        }
        return "admin/hoadon/hoa-don-online-tim-kiem";
    }
}
