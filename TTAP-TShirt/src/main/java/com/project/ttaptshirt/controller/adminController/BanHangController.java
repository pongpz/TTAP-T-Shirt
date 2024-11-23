package com.project.ttaptshirt.controller.adminController;

import com.project.ttaptshirt.config.Config;
import com.project.ttaptshirt.entity.*;
import com.project.ttaptshirt.exception.ResourceNotFoundException;
import com.project.ttaptshirt.repository.HoaDonRepository;
import com.project.ttaptshirt.repository.UserRepo;
import com.project.ttaptshirt.repository.VoucherRepo;
import com.project.ttaptshirt.security.CustomUserDetail;
import com.project.ttaptshirt.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import java.util.List;
import java.util.Map;
import java.util.TimeZone;

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
    KhachHangService khachHangService;

    @Autowired
    HoaDonRepository hoaDonRepository;


    @GetMapping("")
    public String openBanHangPage(Model model, Authentication authentication) {

        if (authentication != null) {
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            User user = customUserDetail.getUser();
            model.addAttribute("userLogged", user);
        }
        List<HoaDon> listHoaDon = hoaDonService.getListHDChuaThanhToan();
        List<HoaDon> listHd = hoaDonService.getListHDDaThanhToan();
        List<ChiTietSanPham> listCTSP = chiTietSanPhamService.findAll();
        List<MaGiamGia> listKM = voucherRepo.findAll();
        model.addAttribute("listKM", listKM);
        model.addAttribute("listHoaDon", listHoaDon);
        model.addAttribute("listLs", listHd);
        model.addAttribute("listHD", listHoaDon);
        model.addAttribute("listCTSP", listCTSP);

        return "admin/banhangtaiquay/banhang";
    }


    @GetMapping("/hoa-don/chi-tiet")
    public String viewHDCT(@RequestParam("hoadonId") Long idHoaDon, Model model, Authentication authentication) {
        if (authentication != null) {
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            User user = customUserDetail.getUser();
            model.addAttribute("userLogged", user);
        }

        List<ChiTietSanPham> listCTSP = chiTietSanPhamService.findAll();
        List<MaGiamGia> listKM = voucherRepo.findAll();
        List<KhachHang> listkh = khachHangService.findAll();
        model.addAttribute("listKh", listkh);
        model.addAttribute("listKM", listKM);
        model.addAttribute("listCTSP", listCTSP);
        List<HoaDonChiTiet> listHDCT = hoaDonChiTietService.getHDCTByIdHD(idHoaDon);
        model.addAttribute("listHDCT", listHDCT);

//

        HoaDon hoadon = hoaDonRepository.findById(idHoaDon).orElseThrow(() -> new ResourceNotFoundException("Hóa đơn không tồn tại với ID: " + idHoaDon));
        model.addAttribute("hoadon", hoadon);
        HoaDon hoaDon = hoaDonService.findById(idHoaDon);
        double totalMoneyBefore = listHDCT.stream()
                .mapToDouble(hdct -> {
                    int soLuong = (hdct.getSoLuong() != null) ? hdct.getSoLuong() : 0;
                    double giaBan = (hdct.getChiTietSanPham() != null && hdct.getChiTietSanPham().getGiaBan() != null) ? hdct.getChiTietSanPham().getGiaBan() : 0.0;
                    return soLuong * giaBan;
                })
                .sum();
        MaGiamGia voucher = hoaDon.getMaGiamGia();
        double discount = 0.0;

        if (voucher != null) {
            if (voucher.getHinhThuc().equals(false)) {
                discount = (voucher.getGiaTriGiam() / 100.0) * totalMoneyBefore;
                if (discount > voucher.getGiaTriToiDa()) {
                    discount = voucher.getGiaTriToiDa();
                }
            } else if (voucher.getHinhThuc().equals(true)) {
                discount = voucher.getGiaTriGiam();
            }
        }

        double totalMoneyAfter = totalMoneyBefore - discount;
        totalMoneyAfter = Math.max(totalMoneyAfter, 0);
        model.addAttribute("linkqr", "https://api.vietqr.io/image/970407-1938170304-oJ6IfGN.jpg?accountName=DUONGTRUNGANH&amount=" + (int) totalMoneyAfter + "&addInfo=" + hoaDon.getMa());

        return "admin/banhangtaiquay/chiTietHoaDon";
    }


    @PostMapping("/hoa-don/xac-nhan-thanh-toan")
    public String xacNhanThanhToan(
            @RequestParam("idhd") Long idHD, Model model, Authentication authentication, RedirectAttributes redirectAttributes) {

        // Kiểm tra người dùng đã đăng nhập hay chưa
        if (authentication != null) {
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            User user = customUserDetail.getUser();
            model.addAttribute("userLogged", user); // Gửi thông tin người dùng vào model
        }

        // Tìm hóa đơn theo ID
        HoaDon hoaDon = hoaDonService.findById(idHD);
        // Lấy danh sách chi tiết hóa đơn theo ID hóa đơn
        List<HoaDonChiTiet> listHDCT = hoaDonChiTietService.getHDCTByIdHD(idHD);

        // Nếu hóa đơn không có chi tiết, chuyển hướng về trang chi tiết hóa đơn với thông báo lỗi
        if (listHDCT.isEmpty()) {
            System.out.println("Hóa đơn trống");
            redirectAttributes.addFlashAttribute("isInvoiceEmptyCheckout", true);
            return "redirect:/admin/ban-hang/hoa-don/chi-tiet?hoadonId=" + idHD;
        }

        // Tính tổng tiền trước giảm giá
        double totalMoneyBefore = listHDCT.stream()
                .mapToDouble(hdct -> {
                    int soLuong = (hdct.getSoLuong() != null) ? hdct.getSoLuong() : 0; // Kiểm tra số lượng
                    double giaBan = (hdct.getChiTietSanPham() != null && hdct.getChiTietSanPham().getGiaBan() != null)
                            ? hdct.getChiTietSanPham().getGiaBan() : 0.0; // Kiểm tra giá bán
                    return soLuong * giaBan;
                })
                .sum();

        // Lấy thông tin mã giảm giá từ hóa đơn
        MaGiamGia voucher = hoaDon.getMaGiamGia();
        double discount = 0.0;

        // Nếu có mã giảm giá, tính tiền giảm
        if (voucher != null) {
            // Trường hợp giảm giá theo %
            if (voucher.getHinhThuc().equals(false)) {
                discount = (voucher.getGiaTriGiam() / 100.0) * totalMoneyBefore; // Tính tiền giảm
                if (discount > voucher.getGiaTriToiDa()) {
                    discount = voucher.getGiaTriToiDa(); // Áp dụng giới hạn tối đa nếu có
                }
            }
            // Trường hợp giảm giá cố định
            else if (voucher.getHinhThuc().equals(true)) {
                discount = voucher.getGiaTriGiam();
            }
        }

        // Tính tổng tiền sau khi giảm giá, đảm bảo không âm
        double totalMoneyAfter = totalMoneyBefore - discount;
        totalMoneyAfter = Math.max(totalMoneyAfter, 0);

        // Cập nhật thông tin giảm giá, tổng tiền và trạng thái cho hóa đơn
        hoaDon.setSoTienGiamGia((float) discount);
        hoaDon.setTongTien((float) totalMoneyAfter);
        hoaDon.setTrangThai(1); // Đặt trạng thái hóa đơn đã thanh toán
        hoaDonService.save(hoaDon);

        // Thêm thông báo thành công trước khi chuyển hướng
        redirectAttributes.addFlashAttribute("checkoutSuccess", true);

        // Chuyển hướng về trang /admin/ban-hang
        return "redirect:/admin/ban-hang";
    }

    @GetMapping("/hoa-don/thanh-toan-vnpay")
    public String createPayment(@RequestParam("idhd") Long id, Authentication authentication, RedirectAttributes redirectAttributes, Model model, HttpServletRequest req, HttpServletResponse resp) throws IOException {

        if (authentication != null) {
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            User user = customUserDetail.getUser();
            model.addAttribute("userLogged", user);
        }

        HoaDon hoaDon = hoaDonService.findById(id);
        List<HoaDonChiTiet> listHDCT = hoaDonChiTietService.getHDCTByIdHD(id);
        double totalMoneyBefore = listHDCT.stream()
                .mapToDouble(hdct -> {
                    int soLuong = (hdct.getSoLuong() != null) ? hdct.getSoLuong() : 0;
                    double giaBan = (hdct.getChiTietSanPham() != null && hdct.getChiTietSanPham().getGiaBan() != null) ? hdct.getChiTietSanPham().getGiaBan() : 0.0;
                    return soLuong * giaBan;
                })
                .sum();
        MaGiamGia voucher = hoaDon.getMaGiamGia();
        double discount = 0.0;

        if (voucher != null) {
            if (voucher.getHinhThuc().equals(false)) {
                discount = (voucher.getGiaTriGiam() / 100.0) * totalMoneyBefore;
                if (discount > voucher.getGiaTriToiDa()) {
                    discount = voucher.getGiaTriToiDa();
                }
            } else if (voucher.getHinhThuc().equals(true)) {
                discount = voucher.getGiaTriGiam();
            }
        }

        double totalMoneyAfter = totalMoneyBefore - discount;
        totalMoneyAfter = Math.max(totalMoneyAfter, 0);
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String orderType = "other";
        long amount = (int) totalMoneyAfter * 100;
        String vnp_IpAddr = Config.getIpAddress(req);

        String vnp_TmnCode = Config.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_TxnRef", hoaDon.getMa());
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_BankCode", "");
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + hoaDon.getMa());
        vnp_Params.put("vnp_OrderType", orderType);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", Config.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
//        vnp_Params.put("id_hoa_don", id.toString());

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = Config.hmacSHA512(Config.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = Config.vnp_PayUrl + "?" + queryUrl;
//        com.google.gson.JsonObject job = new JsonObject();
//        job.addProperty("code", "00");
//        job.addProperty("message", "success");
//        job.addProperty("data", paymentUrl);
//        Gson gson = new Gson();
//        resp.getWriter().write(gson.toJson(job));

//        PaymentResDTO paymentResDTO = new PaymentResDTO();
//        paymentResDTO.setStatus("Ok");
//        paymentResDTO.setMessage("Successfully");
//        paymentResDTO.setUrl(paymentUrl);

        return "redirect:" + paymentUrl;
    }

    @GetMapping("/hoa-don/xac-nhan-thanh-toan_vnp")
    public String xacNhanThanhToanVNP(Model model,
                                      Authentication authentication,
                                      RedirectAttributes redirectAttributes,
                                      @RequestParam("vnp_ResponseCode") String respCode,
                                      @RequestParam("vnp_TxnRef") String mahd) {

        Long idhd = hoaDonRepository.getHDByMa(mahd).get(0).getId();
        if (!respCode.equals("00")) {
            redirectAttributes.addFlashAttribute("checkoutFail", true);
            return "redirect:/admin/ban-hang/hoa-don/chi-tiet?hoadonId=" + idhd;
        } else {
            if (authentication != null) {
                CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
                User user = customUserDetail.getUser();
                model.addAttribute("userLogged", user);
            }

            HoaDon hoaDon = hoaDonService.findById(idhd);
            List<HoaDonChiTiet> listHDCT = hoaDonChiTietService.getHDCTByIdHD(idhd);

            // Calculating the total money before discount and applying the discount
            double totalMoneyBefore = listHDCT.stream()
                    .mapToDouble(hdct -> {
                        int soLuong = (hdct.getSoLuong() != null) ? hdct.getSoLuong() : 0;
                        double giaBan = (hdct.getChiTietSanPham() != null && hdct.getChiTietSanPham().getGiaBan() != null) ? hdct.getChiTietSanPham().getGiaBan() : 0.0;
                        return soLuong * giaBan;
                    })
                    .sum();

            MaGiamGia voucher = hoaDon.getMaGiamGia();
            double discount = 0.0;

            if (voucher != null) {
                if (voucher.getHinhThuc().equals(false)) {
                    discount = (voucher.getGiaTriGiam() / 100.0) * totalMoneyBefore;
                    if (discount > voucher.getGiaTriToiDa()) {
                        discount = voucher.getGiaTriToiDa();
                    }
                } else if (voucher.getHinhThuc().equals(true)) {
                    discount = voucher.getGiaTriGiam();
                }
            }

            double totalMoneyAfter = totalMoneyBefore - discount;
            totalMoneyAfter = Math.max(totalMoneyAfter, 0);

            hoaDon.setSoTienGiamGia((float) discount);
            hoaDon.setTongTien((float) totalMoneyAfter);
            hoaDon.setTrangThai(1);
            hoaDonService.save(hoaDon);

            // Set flash attribute before redirecting
            redirectAttributes.addFlashAttribute("checkoutSuccess", true);

            return "redirect:/admin/ban-hang";
        }
    }

    @GetMapping("/huy-hoa-don-tai-quay")
    public String huyHD(@RequestParam("hoadonId") Long idhd, Model model, Authentication authentication, RedirectAttributes redirectAttributes) {
        if (authentication != null) {
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            User user = customUserDetail.getUser();
            model.addAttribute("userLogged", user);
        }
        List<HoaDonChiTiet> listHoaDonChiTiet = hoaDonChiTietService.getListHdctByIdHd(idhd);
        for (HoaDonChiTiet hoaDonChiTiet : listHoaDonChiTiet) {
            Integer soLuongHoi = hoaDonChiTiet.getSoLuong();
            ChiTietSanPham chiTietSanPham = hoaDonChiTiet.getChiTietSanPham();
            chiTietSanPhamService.updateSoLuongCtsp((chiTietSanPham.getSoLuong() + soLuongHoi), chiTietSanPham.getId());
            hoaDonChiTietService.deleteById(hoaDonChiTiet.getId());
        }
        hoaDonService.updateTongTien(idhd, 0.0);
        hoaDonService.updateTrangThaiHD(2, idhd);
        redirectAttributes.addFlashAttribute("isCancelInvoice", true);
        return "redirect:/admin/ban-hang";
    }


    @GetMapping("/xoa/{id}")
    public String xoaHD(@PathVariable("id") Long idhd, Model model, Authentication authentication) {
        if (authentication != null) {
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            User user = customUserDetail.getUser();
            model.addAttribute("userLogged", user);
        }
        List<HoaDon> listHoaDon = hoaDonService.getListHDDaThanhToan();
        List<ChiTietSanPham> listCTSP = chiTietSanPhamService.findAll();
        model.addAttribute("listHoaDon", listHoaDon);
        model.addAttribute("listCTSP", listCTSP);
        hoaDonService.deleteById(idhd);

        return "redirect:/admin/ban-hang";
    }


    @PostMapping("/newHoaDon")
    public String newHoaDon(Authentication authentication, RedirectAttributes redirectAttributes) {
        HoaDon hoaDon = new HoaDon();
        if (authentication != null) {
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            User user = customUserDetail.getUser();
            hoaDon.setNhanVien(user);
        }
        hoaDon.setMa("HD" + (int) (Math.random() * 1000000));
        hoaDon.setLoaiDon(1);
        hoaDon.setNgayTao(LocalDateTime.now());
        hoaDon.setTrangThai(0);
        hoaDonService.save(hoaDon);

        // Add flash attribute to indicate success
        redirectAttributes.addFlashAttribute("createSuccess", true);

        return "redirect:/admin/ban-hang";
    }


    @PostMapping("/add-ctsp-to-hoadon")
    public String addCtspToHoaDon(@RequestParam("idctsp") Long idctsp,
                                  @RequestParam("idhd") Long idhd,
                                  @RequestParam("soLuong") Integer soLuongMua,
                                  RedirectAttributes redirectAttributes) {

        // Lấy danh sách chi tiết hóa đơn (HDCT) dựa trên ID hóa đơn (idhd)
        List<HoaDonChiTiet> hoaDonChiTietList = hoaDonChiTietService.getListHdctByIdHd(idhd);
        Boolean isHdctExist = false;

        // Kiểm tra xem sản phẩm đã tồn tại trong danh sách HDCT chưa
        for (HoaDonChiTiet hdChiTiet : hoaDonChiTietList) {
            if (hdChiTiet.getChiTietSanPham().getId() == idctsp) {
                isHdctExist = true;
            }
        }

        // Nếu sản phẩm đã tồn tại trong HDCT
        if (isHdctExist) {
            for (HoaDonChiTiet hdChiTiet : hoaDonChiTietList) {
                if (hdChiTiet.getChiTietSanPham().getId() == idctsp) {
                    // Lấy số lượng hiện tại trong HDCT và tính số lượng mới
                    Integer soLuongHienTaiTrongHDCT = hdChiTiet.getSoLuong();
                    Integer soLuongMoi = soLuongHienTaiTrongHDCT + soLuongMua;

                    // Lấy thông tin sản phẩm chi tiết (CTSP)
                    ChiTietSanPham chiTietSanPham1 = chiTietSanPhamService.findById(idctsp);

                    // Kiểm tra số lượng tồn kho có đủ không
                    if (soLuongMua > chiTietSanPham1.getSoLuong()) {
                        redirectAttributes.addFlashAttribute("isQuantityNotEnough", true);
                        redirectAttributes.addFlashAttribute("messageQuantityNotEnough", "Số lượng không đủ, chỉ còn " + chiTietSanPham1.getSoLuong() + " sản phẩm");
                        return "redirect:/admin/ban-hang/hoa-don/chi-tiet?hoadonId=" + idhd;
                    }

                    // Cập nhật số lượng mới vào HDCT
                    hoaDonChiTietService.updateSoLuongHdct(soLuongMoi, hdChiTiet.getId());

                    // Cập nhật lại số lượng tồn kho
                    Integer soLuongConLaiCTSP = chiTietSanPham1.getSoLuong() - soLuongMua;
                    chiTietSanPhamService.updateSoLuongCtsp(soLuongConLaiCTSP, idctsp);

                    // Cập nhật tổng tiền hóa đơn
                    List<HoaDonChiTiet> listHdct = hoaDonChiTietService.getListHdctByIdHd(idhd);
                    Double tongTien = 0.0;
                    for (HoaDonChiTiet hdct : listHdct) {
                        tongTien += hdct.getChiTietSanPham().getGiaBan() * hdct.getSoLuong();
                    }
                    hoaDonService.updateTongTien(idhd, tongTien);

                    // Thông báo thêm thành công
                    redirectAttributes.addFlashAttribute("addSuccess", true);
                    return "redirect:/admin/ban-hang/hoa-don/chi-tiet?hoadonId=" + idhd;
                }
            }
        } else {
            // Nếu sản phẩm chưa tồn tại trong HDCT, tạo mới một HDCT
            HoaDonChiTiet hoaDonChiTiet = new HoaDonChiTiet();
            HoaDon hoaDon = new HoaDon();
            hoaDon.setId(idhd);
            ChiTietSanPham chiTietSanPham = new ChiTietSanPham();
            chiTietSanPham.setId(idctsp);
            hoaDonChiTiet.setHoaDon(hoaDon);
            hoaDonChiTiet.setChiTietSanPham(chiTietSanPham);
            hoaDonChiTiet.setSoLuong(soLuongMua);

            // Lưu HDCT mới vào cơ sở dữ liệu
            hoaDonChiTietService.save(hoaDonChiTiet);

            // Lấy thông tin sản phẩm chi tiết (CTSP)
            ChiTietSanPham chiTietSanPham1 = chiTietSanPhamService.findById(idctsp);
            int soLuongSauUpdate;

            // Kiểm tra số lượng tồn kho có đủ không
            if (chiTietSanPham1.getSoLuong() < soLuongMua) {
                soLuongSauUpdate = chiTietSanPham1.getSoLuong();
            } else {
                soLuongSauUpdate = chiTietSanPham1.getSoLuong() - soLuongMua;
            }

            // Cập nhật lại số lượng tồn kho
            chiTietSanPham1.setSoLuong(soLuongSauUpdate);
            chiTietSanPhamService.save(chiTietSanPham1);

            // Cập nhật tổng tiền hóa đơn
            List<HoaDonChiTiet> listHdct = hoaDonChiTietService.getListHdctByIdHd(idhd);
            Double tongTien = 0.0;
            for (HoaDonChiTiet hdct : listHdct) {
                tongTien += hdct.getChiTietSanPham().getGiaBan() * hdct.getSoLuong();
            }
            hoaDonService.updateTongTien(idhd, tongTien);

            // Thông báo thêm thành công
            redirectAttributes.addFlashAttribute("addSuccess", true);
        }
        return "redirect:/admin/ban-hang/hoa-don/chi-tiet?hoadonId=" + idhd;
    }


    @PostMapping("/chon-khach-hang")
    public String chonKhachHang(@RequestParam("idhd") Long idhd,
                                @RequestParam("idkh") Long idkh) {
        HoaDon existingHoaDon = hoaDonRepository.findById(idhd).orElseThrow(() -> new ResourceNotFoundException("Hóa đơn không tồn tại với ID: " + idhd));
        KhachHang khachHang = new KhachHang();
        khachHang.setId(idkh);
        existingHoaDon.setKhachHang(khachHang);
        hoaDonService.save(existingHoaDon);
        return "redirect:/admin/ban-hang/hoa-don/chi-tiet?hoadonId=" + idhd;
    }

    @Transactional
    @GetMapping("/huy-khach-hang")
    public String huyKhachHang(@RequestParam("hoadonId") Long idhd) {
        HoaDon hoaDon = hoaDonRepository.getReferenceById(idhd);
        hoaDon.setKhachHang(null);
        hoaDonRepository.save(hoaDon);
        return "redirect:/admin/ban-hang/hoa-don/chi-tiet?hoadonId="+idhd;
    }

    @PostMapping("/chon-khuyen-mai")
    public String chonKhuyenMai(@RequestParam("idhd") Long idhd,
                                @RequestParam("idkm") Long idkm) {
        HoaDon existingHoaDon = hoaDonRepository.findById(idhd).orElseThrow(() -> new ResourceNotFoundException("Hóa đơn không tồn tại với ID: " + idhd));
        MaGiamGia voucher = new MaGiamGia();
        voucher.setId(idkm);
        existingHoaDon.setMaGiamGia(voucher);
        hoaDonService.save(existingHoaDon);
        return "redirect:/admin/ban-hang";
    }


    @GetMapping("/hoa-don/xoa-sp")
    public String deleteSpctInHoaDon(@RequestParam("idHdct") Long idHdct, RedirectAttributes redirectAttributes) {
        HoaDonChiTiet hoaDonChiTiet = hoaDonChiTietService.findById(idHdct);
        Long idhd = hoaDonChiTiet.getHoaDon().getId();
        Integer soLuong = hoaDonChiTiet.getSoLuong();
        ChiTietSanPham chiTietSanPham = hoaDonChiTiet.getChiTietSanPham();
        Integer soLuongUpdate = chiTietSanPhamService.findById(chiTietSanPham.getId()).getSoLuong() + soLuong;

        // Delete the item and update quantity
        hoaDonChiTietService.deleteById(idHdct);
        chiTietSanPhamService.updateSoLuongCtsp(soLuongUpdate, chiTietSanPham.getId());

        // Add a flash attribute indicating success
        redirectAttributes.addFlashAttribute("deleteSuccess", true);
        List<HoaDonChiTiet> listHdct = hoaDonChiTietService.getListHdctByIdHd(idhd);
        Double tongTien = 0.0;
        for (HoaDonChiTiet hdct : listHdct) {
            tongTien += hdct.getChiTietSanPham().getGiaBan() * hdct.getSoLuong();
        }
        hoaDonService.updateTongTien(idhd, tongTien);
        return "redirect:/admin/ban-hang/hoa-don/chi-tiet?hoadonId=" + idhd;
    }

    @PostMapping("/hoa-don/xoa-het-ctsp/{idHD}")
    public String xoaHetCtspKhoiHD(@PathVariable("idHD") Long idHD,
                                   RedirectAttributes redirectAttributes) {
        List<HoaDonChiTiet> listHoaDonChiTiet = hoaDonChiTietService.getListHdctByIdHd(idHD);
        for (HoaDonChiTiet hoaDonChiTiet : listHoaDonChiTiet) {
            Integer soLuongHoi = hoaDonChiTiet.getSoLuong();
            ChiTietSanPham chiTietSanPham = hoaDonChiTiet.getChiTietSanPham();
            chiTietSanPhamService.updateSoLuongCtsp((chiTietSanPham.getSoLuong() + soLuongHoi), chiTietSanPham.getId());
            hoaDonChiTietService.deleteById(hoaDonChiTiet.getId());
        }
        hoaDonService.updateTongTien(idHD, 0.0);
        redirectAttributes.addFlashAttribute("deleteAllSuccess", true);
        return "redirect:/admin/ban-hang/hoa-don/chi-tiet?hoadonId=" + idHD;
    }

    @PostMapping("/hoa-don/sua-so-luong")
    public String updateSoLuongSua(@RequestParam("soLuongSua") Integer soLuongSua,
                                   @RequestParam("idHdctSua") Long idHdct,
                                   RedirectAttributes redirectAttributes) {
        System.out.println(soLuongSua);
        System.out.println(idHdct);

        HoaDonChiTiet hoaDonChiTiet = hoaDonChiTietService.findById(idHdct);
        Long idhd = hoaDonChiTiet.getHoaDon().getId();
        Integer soLuongHienTai = hoaDonChiTiet.getSoLuong();

        ChiTietSanPham chiTietSanPham = hoaDonChiTiet.getChiTietSanPham();
        Integer soLuongKhaDung = chiTietSanPham.getSoLuong();
        if (soLuongSua > soLuongKhaDung) {
            redirectAttributes.addFlashAttribute("QuantityUpdateError", true);
            return "redirect:/admin/ban-hang/hoa-don/chi-tiet?hoadonId=" + idhd;

        }
        hoaDonChiTietService.updateSoLuongHdct(soLuongSua, idHdct);

        Integer chenhLechSl = soLuongSua - soLuongHienTai;
        Integer soLuongKho = hoaDonChiTiet.getChiTietSanPham().getSoLuong() - chenhLechSl;

        chiTietSanPhamService.updateSoLuongCtsp(soLuongKho, hoaDonChiTiet.getChiTietSanPham().getId());

        List<HoaDonChiTiet> listHdct = hoaDonChiTietService.getListHdctByIdHd(idhd);
        Double tongTien = 0.0;
        for (HoaDonChiTiet hdct : listHdct) {
            tongTien += hdct.getChiTietSanPham().getGiaBan() * hdct.getSoLuong();
        }
        hoaDonService.updateTongTien(idhd, tongTien);

        System.out.println("dung controller roi");
        // Add flash attribute to indicate success
        redirectAttributes.addFlashAttribute("updateSuccess", true);
        return "redirect:/admin/ban-hang/hoa-don/chi-tiet?hoadonId=" + idhd;
    }

}
