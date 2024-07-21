package com.project.ttaptshirt.controller;

import com.project.ttaptshirt.entity.ChiTietSanPham;
import com.project.ttaptshirt.entity.HoaDon;
import com.project.ttaptshirt.entity.HoaDonChiTiet;
import com.project.ttaptshirt.service.ChiTietSanPhamService;
import com.project.ttaptshirt.service.HoaDonChiTietService;
import com.project.ttaptshirt.service.HoaDonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/admin/ban-hang")
public class BanHangController {

    @Autowired
    HoaDonService hoaDonService;

    @Autowired
    ChiTietSanPhamService chiTietSanPhamService;

    @Autowired
    HoaDonChiTietService hoaDonChiTietService;



    @GetMapping("")
    public String openBanHangPage(Model model){
        List<HoaDon> listHoaDon = hoaDonService.getListHDChuaThanhToan();
        List<ChiTietSanPham> listCTSP = chiTietSanPhamService.findAll();
        model.addAttribute("listHoaDon",listHoaDon);
        model.addAttribute("listCTSP", listCTSP);

        return "admin/banhangtaiquay/banhang";
    }

    @GetMapping("hoa-don/chi-tiet")
    public String viewHDCT (@RequestParam("hoadonId") Long id,Model model){
        List<HoaDon> listHoaDon = hoaDonService.getListHDChuaThanhToan();
        List<ChiTietSanPham> listCTSP = chiTietSanPhamService.findAll();
        model.addAttribute("listHoaDon",listHoaDon);
        model.addAttribute("listCTSP", listCTSP);
        List<HoaDonChiTiet> listHDCT = hoaDonChiTietService.getHDCTByIdHD(id);
        model.addAttribute("listHDCT",listHDCT);
        return  "/admin/banhangtaiquay/banhang";
    }

    @GetMapping("/huy")
    public String huyHD(@RequestParam("hoadonId") Long idhd,Model model){
        List<HoaDon> listHoaDon = hoaDonService.getListHDChuaThanhToan();
        List<ChiTietSanPham> listCTSP = chiTietSanPhamService.findAll();
        model.addAttribute("listHoaDon",listHoaDon);
        model.addAttribute("listCTSP", listCTSP);
        hoaDonService.updateTrangThaiHD(1,idhd);
        return  "redirect:/admin/ban-hang";
    }

    @PostMapping("/newHoaDon")
    public String newHoaDon(){
        HoaDon hoaDon = new HoaDon();
        hoaDon.setMa("HD"+(int)(Math.random() * 1000000));
        hoaDon.setNgayTao(new java.sql.Date(new Date().getTime()));
        hoaDon.setLoaiDon(0);
        hoaDon.setTrangThai(0);
        hoaDonService.save(hoaDon);
        return "redirect:/admin/ttap-tshirt";
    }

    @PostMapping("/add-ctsp-to-hoadon")
    public String addCtspToHoaDon(@RequestParam("idctsp") Long idctsp,
                                  @RequestParam("idhd") Long idhd,
                                  @RequestParam("soLuong") Integer soLuong) {

        HoaDonChiTiet hoaDonChiTiet = new HoaDonChiTiet();
        HoaDon hoaDon = new HoaDon();
        hoaDon.setId(idhd);
        ChiTietSanPham chiTietSanPham = new ChiTietSanPham();
        chiTietSanPham.setId(idctsp);
        hoaDonChiTiet.setHoaDon(hoaDon);
        hoaDonChiTiet.setChiTietSanPham(chiTietSanPham);
        hoaDonChiTiet.setSoLuong(soLuong);
        hoaDonChiTiet.setNgayTao(new java.sql.Date(new Date().getTime()));
        hoaDonChiTietService.save(hoaDonChiTiet);
        ChiTietSanPham chiTietSanPham1 = chiTietSanPhamService.findById(idctsp);
        int soLuongSauUpdate;
        if (chiTietSanPham1.getSoLuong()<soLuong){
            soLuongSauUpdate = chiTietSanPham1.getSoLuong();
        } else {
            soLuongSauUpdate = chiTietSanPham1.getSoLuong()-soLuong;
        }
        chiTietSanPham1.setSoLuong(soLuongSauUpdate);
        chiTietSanPhamService.save(chiTietSanPham1);
        return "redirect:/admin/ttap-tshirt";
    }


}
