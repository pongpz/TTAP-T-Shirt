package com.project.ttaptshirt.controller;

import com.project.ttaptshirt.entity.ChiTietSanPham;
import com.project.ttaptshirt.entity.HoaDon;
import com.project.ttaptshirt.entity.HoaDonChiTiet;
import com.project.ttaptshirt.entity.User;
import com.project.ttaptshirt.entity.Voucher;
import com.project.ttaptshirt.exception.ResourceNotFoundException;
import com.project.ttaptshirt.repository.HoaDonRepository;
import com.project.ttaptshirt.repository.UserRepo;
import com.project.ttaptshirt.repository.VoucherRepo;
import com.project.ttaptshirt.service.ChiTietSanPhamService;
import com.project.ttaptshirt.service.HoaDonChiTietService;
import com.project.ttaptshirt.service.HoaDonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/admin/ban-hang")
public class BanHangController {

    @Autowired
    HoaDonService hoaDonService;

    @Autowired
    ChiTietSanPhamService chiTietSanPhamService;

    @Autowired
    HoaDonChiTietService hoaDonChiTietService;

    @Autowired
    VoucherRepo voucherRepo;

    @Autowired
    UserRepo userRepo;

    @Autowired
    HoaDonRepository hoaDonRepository;


    @GetMapping("")
    public String openBanHangPage(Model model) {
        List<HoaDon> listHoaDon = hoaDonService.getListHDChuaThanhToan();
        List<ChiTietSanPham> listCTSP = chiTietSanPhamService.findAll();
        List<Voucher> listKM = voucherRepo.findAll();
        List<User> listKH = userRepo.findAll();
        model.addAttribute("listKH", listKH);
        model.addAttribute("listKM", listKM);
        model.addAttribute("listHoaDon", listHoaDon);
        model.addAttribute("listHD", listHoaDon);
        model.addAttribute("listCTSP", listCTSP);

        return "admin/banhangtaiquay/banhang";
    }

    @GetMapping("/index")
    public String openBanHangPageindex(Model model) {
        List<HoaDon> listHoaDon = hoaDonService.getListHDChuaThanhToan();
        List<ChiTietSanPham> listCTSP = chiTietSanPhamService.findAll();
        List<Voucher> listKM = voucherRepo.findAll();
        List<User> listKH = userRepo.findAll();
        model.addAttribute("listKH", listKH);
        model.addAttribute("listKM", listKM);
        model.addAttribute("listHoaDon", listHoaDon);
        model.addAttribute("listCTSP", listCTSP);

        return "admin/banhangtaiquay/index";
    }

    @GetMapping("hoa-don/chi-tiet")
    public String viewHDCT(@RequestParam("hoadonId") Long id, Model model) {
        List<HoaDon> listHoaDon = hoaDonService.getListHDChuaThanhToan();
        List<ChiTietSanPham> listCTSP = chiTietSanPhamService.findAll();
        List<Voucher> listKM = voucherRepo.findAll();
        List<User> listKH = userRepo.findAll();
        model.addAttribute("listKH", listKH);
        model.addAttribute("listKM", listKM);
        model.addAttribute("listHoaDon", listHoaDon);
        model.addAttribute("listCTSP", listCTSP);
        List<HoaDonChiTiet> listHDCT = hoaDonChiTietService.getHDCTByIdHD(id);
        model.addAttribute("listHDCT", listHDCT);

        double totalMoneyBefore = listHDCT.stream()
                .mapToDouble(hdct -> {
                    int soLuong = (hdct.getSoLuong() != null) ? hdct.getSoLuong() : 0;
                    double giaBan = (hdct.getChiTietSanPham() != null && hdct.getChiTietSanPham().getGiaBan() != null) ? hdct.getChiTietSanPham().getGiaBan() : 0.0;
                    return soLuong * giaBan;
                })
                .sum();

        HoaDon hoaDon = hoaDonRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Hóa đơn không tồn tại với ID: " + id));
        Voucher voucher = hoaDon.getKhuyenMai();

        double discount = 0.0;

        if (voucher != null) {
            if (voucher.getHinhThuc() == 1) {
                discount = (voucher.getGiaTriGiam() / 100.0) * totalMoneyBefore;
            }
            else if (voucher.getHinhThuc() == 0) {
                discount = voucher.getGiaTriGiam();
            }
        }

        double totalMoneyAfter = totalMoneyBefore - discount;

        totalMoneyAfter = Math.max(totalMoneyAfter, 0);

        double moneyVoucher = totalMoneyBefore - totalMoneyAfter;

        NumberFormat currencyFormatter = NumberFormat.getNumberInstance(Locale.US);
        String formattedTotalMoneyBefore = currencyFormatter.format(totalMoneyBefore);
        String formattedTotalMoneyAfter = currencyFormatter.format(totalMoneyAfter);
        String moneyVoucher1 = currencyFormatter.format(moneyVoucher);
        model.addAttribute("totalMoneyBefore", formattedTotalMoneyBefore);
        model.addAttribute("totalMoneyAfter", formattedTotalMoneyAfter);
        model.addAttribute("moneyVoucher", moneyVoucher1);

        HoaDon hoadon = hoaDonRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Hóa đơn không tồn tại với ID: " + id));
        model.addAttribute("hoadon1", hoadon);
        return "admin/banhangtaiquay/banhang";
    }

    @GetMapping("/hoa-don/xac-nhan-thanh-toan")
    public String xacNhanThanhToan(@RequestParam("idhd") Long id) {
        HoaDon hoaDon = hoaDonService.findById(id);

        List<HoaDonChiTiet> listHDCT = hoaDonChiTietService.getHDCTByIdHD(id);
        double totalMoneyBefore = listHDCT.stream()
                .mapToDouble(hdct -> {
                    int soLuong = (hdct.getSoLuong() != null) ? hdct.getSoLuong() : 0;
                    double giaBan = (hdct.getChiTietSanPham() != null && hdct.getChiTietSanPham().getGiaBan() != null) ? hdct.getChiTietSanPham().getGiaBan() : 0.0;
                    return soLuong * giaBan;
                })
                .sum();

        Voucher voucher = hoaDon.getKhuyenMai();
        double discount = 0.0;

        if (voucher!= null){
            if (voucher.getHinhThuc() == 1) {
                discount = (voucher.getGiaTriGiam() / 100.0) * totalMoneyBefore;
            }
            else if (voucher.getHinhThuc() == 0) {
                discount = voucher.getGiaTriGiam();
            }
        }

        double totalMoneyAfter = totalMoneyBefore - discount;
        totalMoneyAfter = Math.max(totalMoneyAfter, 0);


        hoaDon.setTongTien((float) totalMoneyAfter);
        hoaDon.setTrangThai(1);
        hoaDonService.save(hoaDon);
        return "redirect:/admin/ban-hang";
    }

    @GetMapping("/huy")
    public String huyHD(@RequestParam("hoadonId") Long idhd, Model model) {
        List<HoaDon> listHoaDon = hoaDonService.getListHDChuaThanhToan();
        List<ChiTietSanPham> listCTSP = chiTietSanPhamService.findAll();
        model.addAttribute("listHoaDon", listHoaDon);
        model.addAttribute("listCTSP", listCTSP);
        hoaDonService.updateTrangThaiHD(1, idhd);
        return "redirect:/admin/ttap-tshirt";
    }


    @PostMapping("/newHoaDon")
    public String newHoaDon() {
        HoaDon hoaDon = new HoaDon();
        hoaDon.setMa("HD" + (int) (Math.random() * 1000000));
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
        hoaDonChiTietService.save(hoaDonChiTiet);
        ChiTietSanPham chiTietSanPham1 = chiTietSanPhamService.findById(idctsp);
        int soLuongSauUpdate;
        if (chiTietSanPham1.getSoLuong() < soLuong) {
            soLuongSauUpdate = chiTietSanPham1.getSoLuong();
        } else {
            soLuongSauUpdate = chiTietSanPham1.getSoLuong() - soLuong;
        }
        chiTietSanPham1.setSoLuong(soLuongSauUpdate);
        chiTietSanPhamService.save(chiTietSanPham1);
        return "redirect:/admin/ttap-tshirt";

    }


    @PostMapping("/chon-khach-hang")
    public String chonKhachHang(@RequestParam("idhd") Long idhd,
                                @RequestParam("idkh") Long idkh) {
        HoaDon existingHoaDon = hoaDonRepository.findById(idhd).orElseThrow(() -> new ResourceNotFoundException("Hóa đơn không tồn tại với ID: " + idhd));
        User user = new User();
        user.setId(idkh);
        existingHoaDon.setKhachHang(user);
        hoaDonService.save(existingHoaDon);
        return "redirect:/admin/ban-hang";
    }

    @PostMapping("/chon-khuyen-mai")
    public String chonKhuyenMai(@RequestParam("idhd") Long idhd,
                                @RequestParam("idkm") Long idkm) {
        HoaDon existingHoaDon = hoaDonRepository.findById(idhd).orElseThrow(() -> new ResourceNotFoundException("Hóa đơn không tồn tại với ID: " + idhd));
        Voucher voucher = new Voucher();
        voucher.setId(idkm);
        existingHoaDon.setKhuyenMai(voucher);
        hoaDonService.save(existingHoaDon);
        return "redirect:/admin/ban-hang";
    }



    @GetMapping("/hoa-don/xoa-sp")
    public String deleteSpctInHoaDon(@RequestParam("idHdct") Long idHdct) {
        HoaDonChiTiet hoaDonChiTiet = hoaDonChiTietService.findById(idHdct);
        Integer soLuong = hoaDonChiTiet.getSoLuong();
        ChiTietSanPham chiTietSanPham = hoaDonChiTiet.getChiTietSanPham();
        Integer soLuongUpdate = chiTietSanPhamService.findById(chiTietSanPham.getId()).getSoLuong() + soLuong;
        hoaDonChiTietService.deleteById(idHdct);
        chiTietSanPhamService.updateSoLuongCtsp(soLuongUpdate, chiTietSanPham.getId());
        return "redirect:/admin/ban-hang";
    }

    @PostMapping("/hoa-don/sua-so-luong")
    public String updateSoLuongSua(@RequestParam("soLuongSua")Integer soLuongSua,@RequestParam("idHdctSua") Long idHdct){
        System.out.println(soLuongSua);
        System.out.println(idHdct);
        HoaDonChiTiet hoaDonChiTiet = hoaDonChiTietService.findById(idHdct);
        Integer soLuongHienTai = hoaDonChiTiet.getSoLuong();
        hoaDonChiTietService.updateSoLuongHdct(soLuongSua,idHdct);
        Integer chenhLechSl = soLuongSua - soLuongHienTai;
        Integer soLuongKho = hoaDonChiTiet.getChiTietSanPham().getSoLuong() - chenhLechSl;
        chiTietSanPhamService.updateSoLuongCtsp(soLuongKho,hoaDonChiTiet.getChiTietSanPham().getId());
        return "redirect:/admin/ttap-tshirt";

    }
}
