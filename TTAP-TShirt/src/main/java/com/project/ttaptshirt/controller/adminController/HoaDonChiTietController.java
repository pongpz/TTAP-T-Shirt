package com.project.ttaptshirt.controller.adminController;

import com.project.ttaptshirt.dto.NumberUtils;
import com.project.ttaptshirt.entity.HoaDon;
import com.project.ttaptshirt.entity.HoaDonChiTiet;
import com.project.ttaptshirt.entity.HoaDonLog;
import com.project.ttaptshirt.repository.HoaDonChiTietRepository;
import com.project.ttaptshirt.repository.HoaDonRepository;
import com.project.ttaptshirt.service.HoaDonChiTietService;
import com.project.ttaptshirt.service.HoaDonLogService;
import com.project.ttaptshirt.service.HoaDonService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HoaDonChiTietController {

    @Autowired
    HoaDonChiTietRepository hdctr;

    @Autowired
    HoaDonRepository hdr;

    @Autowired
    HoaDonService hoaDonService;

    @Autowired
    HoaDonChiTietService hoaDonChiTietService;

    @Autowired
    HoaDonLogService hoaDonLogService;

    @Transactional
    @GetMapping("/admin/hoa-don-chi-tiet/hien-thi")
    public String hienThi(@RequestParam Long id, Model model, @RequestParam(defaultValue = "0") Integer page){
        Pageable pageab = PageRequest.of(page, 5);
        NumberUtils numberUtils = new NumberUtils();
        HoaDon hoaDon = hoaDonService.findById(id);
        model.addAttribute("hoaDon",hoaDon);
        model.addAttribute("numberUtils",numberUtils);
        model.addAttribute("listHDCT",hdctr.getHoaDonChiTietByHoaDonId(id));
        model.addAttribute("listSPOrder",hdctr.getHoaDonChiTietByHoaDonId(id));
        model.addAttribute("listHD", hdr.getAllHD(pageab));
        model.addAttribute("page",page);
        if (hdr.getReferenceById(id).getLoaiDon() == 1){
            return "admin/hoadon/hoa-don";
        }else {
            List<HoaDonLog> listHoaDonLog = hoaDonLogService.getListHoaDonLogByIdHd(id);
            model.addAttribute("listHoaDonLog",listHoaDonLog);
            return "admin/hoadon/chi-tiet-hoa-don-online";
        }
    }


//    @GetMapping("/admin/hoa-don-chi-tiet/hien-thi/online")
//    public String hienThiOnline(@RequestParam Long id, Model model, @RequestParam(defaultValue = "0") Integer page){
//        Pageable pageab = PageRequest.of(page, 5);
//        NumberUtils numberUtils = new NumberUtils();
//        model.addAttribute("numberUtils",numberUtils);
//        model.addAttribute("listHDCT",hdctr.getHoaDonChiTietByHoaDonId(id));
//        model.addAttribute("listHD", hdr.getAllHD(pageab));
//        model.addAttribute("page",page);
//        return "admin/hoadon/hoa-don-online";
//    }
}
