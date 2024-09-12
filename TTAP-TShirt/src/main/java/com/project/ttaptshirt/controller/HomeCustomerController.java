package com.project.ttaptshirt.controller;

import com.project.ttaptshirt.entity.ChiTietSanPham;
import com.project.ttaptshirt.entity.HoaDonChiTiet;
import com.project.ttaptshirt.repository.ChiTietSanPhamRepository;
import com.project.ttaptshirt.repository.HoaDonChiTietRepository;
import com.project.ttaptshirt.repository.SanPhamRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/TTAP")
public class HomeCustomerController {

    @Autowired
    SanPhamRepository spr;

    @Autowired
    HoaDonChiTietRepository hdctrp;

    @Autowired
    ChiTietSanPhamRepository ctspr;

    @GetMapping("/trang-chu")
    public String home(HttpServletRequest request, Model model) {
        model.addAttribute("requestURI", request.getRequestURI());
        List<HoaDonChiTiet> ls = hdctrp.findAll();
              List<?> newList = ls.stream()
                .collect(Collectors.groupingBy(hd -> hd.getChiTietSanPham().getId(),Collectors.summingInt(HoaDonChiTiet::getSoLuong)))
                .entrySet()
                .stream()
                .sorted((hd1,hd2) -> Integer.compare(hd2.getValue(),hd1.getValue()))
                .map(entry -> Map.of("id_chi_tiet_sp",entry.getKey(),"so_luong",entry.getValue()))
                .collect(Collectors.toList());
        List<Long> lsIDSPCT = new ArrayList<>();
        newList.forEach(item -> {
            Map<String, Object> map = (Map<String, Object>) item;
            lsIDSPCT.add((Long) map.get("id_chi_tiet_sp"));
            System.out.println("ID Chi Tiet SP: " + map.get("id_chi_tiet_sp") + ", So Luong: " + map.get("so_luong"));
        });
        List<ChiTietSanPham> lsCTSP = new ArrayList<>();
        if (newList.size() < 6){
            for (int i = 0 ; i < newList.size() ; i++){
                lsCTSP.add(ctspr.getReferenceById(lsIDSPCT.get(i)));
            }
            model.addAttribute("lsSPCT",lsCTSP);
            return "user/home/trangchu";
        }else {
            for (int i = 0 ; i < 6 ; i++){
                lsCTSP.add(ctspr.getReferenceById(lsIDSPCT.get(i)));
            }
            model.addAttribute("lsSPCT",lsCTSP);
            return "user/home/trangchu";
        }
    }

    @GetMapping("/san-pham/{idSP}")
    public String detailSP(Model model, @PathVariable Long idSP,
    @RequestParam(required = false) Long idKichCo, @RequestParam(required = false) Long idMauSac){
        model.addAttribute("sanpham",spr.getReferenceById(idSP));
        ChiTietSanPham ctsp = ctspr.findByIDSanPham(idSP,idKichCo,idMauSac).get(0);
        model.addAttribute("spct",ctsp);
        return "user/home/sanphamdetail";
    }

    @GetMapping("/chinh-sach-van-chuyen")
    public String chinhSachVanChuyen(){
        return "user/chinhsach/chinh-sach-van-chuyen";
    }

    @GetMapping("/chinh-sach-bao-mat")
    public String chinhSachBaoMat(){
        return "user/chinhsach/chinh-sach-bao-mat";
    }

    @GetMapping("/chinh-sach-doi-tra")
    public String chinhSachDoiTra(){
        return "user/chinhsach/chinh-sach-doi-tra";
    }

    @GetMapping("/chinh-sach-xu-ly-khieu-lai")
    public String chinhSachXuLyKhieuLai(){
        return "user/chinhsach/chinh-sach-xu-ly-khieu-lai";
    }

    @GetMapping("/huong-dan-mua-hang")
    public String huongDanMuaHang(){
        return "user/chinhsach/huong-dan-mua-hang";
    }
    @GetMapping("/hinh-thuc-thanh-toan")
    public String hinhThucThanhToan(){
        return "user/chinhsach/hinh-thuc-thanh-toan";
    }
}
