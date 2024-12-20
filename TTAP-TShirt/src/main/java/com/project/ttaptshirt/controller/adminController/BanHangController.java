package com.project.ttaptshirt.controller.adminController;

import com.project.ttaptshirt.dto.NumberUtils;
import com.project.ttaptshirt.entity.*;
import com.project.ttaptshirt.exception.ResourceNotFoundException;
import com.project.ttaptshirt.repository.ChiTietSanPhamRepository;
import com.project.ttaptshirt.repository.HoaDonChiTietRepository;
import com.project.ttaptshirt.repository.HoaDonRepository;
import com.project.ttaptshirt.repository.MaGiamGiaRepo;
import com.project.ttaptshirt.security.CustomUserDetail;
import com.project.ttaptshirt.service.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;

import java.io.IOException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

import java.util.List;

@Controller
@RequestMapping("/admin/ban-hang")
public class BanHangController {
    private final PayOS payOS;

    public BanHangController(PayOS payOS) {
        super();
        this.payOS = payOS;
    }

    @Autowired
    HoaDonService hoaDonService;

    @Autowired
    ChiTietSanPhamService chiTietSanPhamService;

    @Autowired
    HoaDonChiTietService hoaDonChiTietService;

    @Autowired
    HoaDonChiTietRepository hoaDonChiTietRepository;

    @Autowired
    ChiTietSanPhamRepository chiTietSanPhamRepository;

    @Autowired
    MaGiamGiaRepo maGiamGiaRepo;

    @Autowired
    KhachHangService khachHangService;

    @Autowired
    HoaDonRepository hoaDonRepository;


    @GetMapping("")
    public String openBanHangPage(Model model, Authentication authentication) {

        if (authentication != null) {
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            TaiKhoan user = customUserDetail.getUser();
            model.addAttribute("userLogged", user);
        }
        List<HoaDon> listHoaDon = hoaDonService.getListHDChuaThanhToan();
        model.addAttribute("listHoaDon", listHoaDon);
        return "admin/banhangtaiquay/banhang";
    }


    @GetMapping("/hoa-don/chi-tiet")
    public String viewHDCT(@RequestParam("hoadonId") Long idHoaDon, Model model, Authentication authentication, @RequestParam(value="tenSP",required = false) String tenSP, @RequestParam(value="mgg",required = false) String mgg) {
        if (authentication != null) {
            // Lấy thông tin người dùng đã đăng nhập
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            TaiKhoan user = customUserDetail.getUser();
            model.addAttribute("userLogged", user); // Thêm thông tin người dùng vào model
        }

//        // Cập nhật tổng tiền hóa đơn
//        List<HoaDonChiTiet> listHdct = hoaDonChiTietService.getListHdctByIdHd(idHoaDon);
//        Double tongTien = 0.0;
//        for (HoaDonChiTiet hdct : listHdct) {
//            tongTien += hdct.getChiTietSanPham().getGiaBan() * hdct.getSoLuong();
//        }
//        HoaDon hoaDon1 = hoaDonService.findById(idHoaDon);
//        hoaDon1.setTongTien(tongTien);
//        hoaDon1.setTienThu(tongTien);
//        hoaDonService.save(hoaDon1);
        model.addAttribute("numberUtils", new NumberUtils());
        // Lấy danh sách tất cả các chi tiết sản phẩm
        List<ChiTietSanPham> listCTSP = chiTietSanPhamRepository.findByTenSanPham(tenSP);
        if (tenSP!=null){
            model.addAttribute("showModalsp",true);
            model.addAttribute("tenSP",tenSP);
        }
        if (mgg!=null){
            model.addAttribute("showModalvc",true);
            model.addAttribute("mgg",mgg);
        }
        // Lấy danh sách tất cả mã giảm giá
        List<MaGiamGia> listKM = maGiamGiaRepo.getMaGiamGiaByTrangThaiKeyword(true,mgg);
        // Lấy danh sách khách hàng sắp xếp theo ngày tạo
        List<KhachHang> listkh = khachHangService.findAllOrderByNgayTao();
        model.addAttribute("listKh", listkh); // Thêm danh sách khách hàng vào model
        model.addAttribute("listCTSP", listCTSP); // Thêm danh sách chi tiết sản phẩm vào model

        // Lấy danh sách chi tiết hóa đơn dựa trên ID hóa đơn
        List<HoaDonChiTiet> listHDCT = hoaDonChiTietService.getHDCTByIdHD(idHoaDon);

        model.addAttribute("listHDCT", listHDCT);
//        model.addAttribute("idHD", idHoaDon);

        // Tìm hóa đơn dựa trên ID, nếu không có thì ném ra ngoại lệ
        HoaDon hoadon = hoaDonRepository.findById(idHoaDon)
                .orElseThrow(() -> new ResourceNotFoundException("Hóa đơn không tồn tại với ID: " + idHoaDon));

        // Lấy hóa đơn từ dịch vụ
        HoaDon hoaDon = hoaDonService.findById(idHoaDon);

        // Tính tổng tiền trước khi áp dụng giảm giá
        double totalMoneyBefore = listHDCT.stream()
                .mapToDouble(hdct -> {
                    int soLuong = (hdct.getSoLuong() != null) ? hdct.getSoLuong() : 0; // Lấy số lượng, nếu null thì gán 0
                    double giaBan = (hdct.getChiTietSanPham() != null && hdct.getChiTietSanPham().getGiaBan() != null)
                            ? hdct.getChiTietSanPham().getGiaBan() : 0.0; // Lấy giá bán, nếu null thì gán 0
                    return soLuong * giaBan; // Tính tiền từng sản phẩm
                })
                .sum(); // Tổng tiền

        hoaDon.setTongTien(totalMoneyBefore);
        // Xử lý mã giảm giá (lấy tất cả những mã thỏa mãn giá trị tối thiểu)
        MaGiamGia voucher = hoaDon.getMaGiamGia();
//        Double giaTriToiThieu = voucher.getGiaTriToiThieu();
        List<MaGiamGia> listMaGiamGiaValid = new ArrayList<>();
        for (MaGiamGia maGiamGia:listKM) {
            if (maGiamGia.getGiaTriToiThieu()<=totalMoneyBefore && maGiamGia.isValid()){
                listMaGiamGiaValid.add(maGiamGia);
            }
        }
        model.addAttribute("listVoucherValid", listMaGiamGiaValid); // Thêm danh sách mã giảm giá hợp lệ vào model

        // Xử lý lấy giá trị giảm
        double discount = 0.0;

        if (voucher != null) {
            if (voucher.getGiaTriToiThieu()>totalMoneyBefore){
                hoadon.setSoTienGiamGia(0.0);
                hoaDon.setMaGiamGia(null);
            } else{
                // Nếu hình thức giảm giá là % (false)
                if (voucher.getHinhThuc().equals(false)) {
                    discount = (voucher.getGiaTriGiam() / 100.0) * totalMoneyBefore; // Tính giảm giá theo %
                    if (discount > voucher.getGiaTriToiDa()) {
                        discount = voucher.getGiaTriToiDa(); // Giới hạn mức giảm giá tối đa
                    }
                    // Nếu hình thức giảm giá là số tiền cụ thể (true)
                } else if (voucher.getHinhThuc().equals(true)) {
                    discount = voucher.getGiaTriGiam();
                }
            }
        }
        hoaDon.setSoTienGiamGia(discount);
        // Tính tổng tiền sau khi áp dụng giảm giá
        double totalMoneyAfter = totalMoneyBefore - discount;
        totalMoneyAfter = Math.max(totalMoneyAfter, 0); // Đảm bảo tổng tiền không âm

        hoaDon.setTienThu(totalMoneyAfter);
        hoaDonService.save(hoaDon);
        model.addAttribute("hoadon", hoadon); // Thêm hóa đơn vào model

        // Tạo link QR code thanh toán với thông tin từ hóa đơn
        model.addAttribute("linkqr",
                "https://api.vietqr.io/image/970407-1938170304-compact2.jpg?accountName=DUONGTRUNGANH&amount="
                        +(long) totalMoneyAfter + "&addInfo=" + hoaDon.getMa());
        // Trả về trang chi tiết hóa đơn
        return "admin/banhangtaiquay/chiTietHoaDon";
    }

    private String getBaseUrl(HttpServletRequest request) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String contextPath = request.getContextPath();

        String url = scheme + "://" + serverName;
        if ((scheme.equals("http") && serverPort != 80) || (scheme.equals("https") && serverPort != 443)) {
            url += ":" + serverPort;
        }
        url += contextPath;
        return url;
    }

    @Transactional
    @RequestMapping(method = RequestMethod.POST, value = "/hoa-don/create-payment-link", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public void checkout(HttpServletRequest request, HttpServletResponse httpServletResponse,@RequestParam("idhd") Long idHD,RedirectAttributes redirectAttributes,Model model,Authentication authentication) throws IOException, ServletException {

        if (authentication != null) {
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            TaiKhoan user = customUserDetail.getUser();
            model.addAttribute("userLogged", user); // Gửi thông tin người dùng vào model
        }

        // Tìm hóa đơn theo ID
        HoaDon hoaDon = hoaDonService.findById(idHD);
        // Lấy danh sách chi tiết hóa đơn theo ID hóa đơn
        List<HoaDonChiTiet> listHDCT = hoaDonChiTietRepository.getHoaDonChiTietByIdHd(idHD);

        if (listHDCT.size() == 0) {
            System.out.println("Hóa đơn trống");
            httpServletResponse.sendRedirect("/admin/ban-hang/hoa-don/chi-tiet?hoadonId=" + idHD);
            return;
        }

        // Nếu hóa đơn không có chi tiết, chuyển hướng về trang chi tiết hóa đơn với thông báo lỗi
//        if (listHDCT== null) {
//            System.out.println("Hóa đơn trống");
//            redirectAttributes.addFlashAttribute("isInvoiceEmptyCheckout", true);
//            return "redirect:/admin/ban-hang/hoa-don/chi-tiet?hoadonId=" + idHD;
//        }

        for (int i = 0 ; i< listHDCT.size() ; i ++){
            ChiTietSanPham chiTietSanPham = chiTietSanPhamRepository.getReferenceById(listHDCT.get(i).getChiTietSanPham().getId());
            HoaDonChiTiet hdct = new HoaDonChiTiet();
            hdct = listHDCT.get(i);
            hdct.setDonGia(chiTietSanPham.getGiaBan().floatValue());
            hoaDonChiTietRepository.save(hdct);
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

        try {
            final String baseUrl = getBaseUrl(request);
//            final String productName = "Sản phẩm của TTAP";
            final String description = "Thanh toan "+hoaDon.getMa();
            final String returnUrl = baseUrl + "/admin/ban-hang/hoa-don/xac-nhan-chuyen-khoan?idhd="+hoaDon.getId();
            final String cancelUrl = baseUrl + "/admin/ban-hang/hoa-don/thanh-toan-that-bai?hoadonId="+hoaDon.getId();
            final int price = (int) totalMoneyAfter;

            if (price < 2000 || price >= 1000000000){
                System.out.println("Giá trị đơn hàng phải từ 2000 đến 1 tỷ");
                redirectAttributes.addFlashAttribute("isInvoiceEmptyCheckout", true);
                httpServletResponse.sendRedirect("/admin/ban-hang/hoa-don/chi-tiet?hoadonId=" + idHD);
                return;
            }

            // Gen order code
            List<ItemData> items = new ArrayList<>();
            for (int i = 0 ; i< listHDCT.size() ; i ++){
                HoaDonChiTiet hdct = new HoaDonChiTiet();
                hdct = listHDCT.get(i);
                String tenSP = hdct.getChiTietSanPham().getSanPham().getTen();
                String mauSac = hdct.getChiTietSanPham().getMauSac().getTen();
                String kichCo = hdct.getChiTietSanPham().getKichCo().getTen();
                String productName = tenSP+" + "+mauSac+" + "+kichCo;
                int soLuong = hdct.getSoLuong();
                int priceItem = Math.round(hdct.getDonGia()*hdct.getSoLuong());
                ItemData item = ItemData.builder().name(productName).quantity(soLuong).price(priceItem).build();
                items.add(item);
            }
            String currentTimeString = String.valueOf(new Date().getTime());
            long orderCode = Long.parseLong(currentTimeString.substring(currentTimeString.length() - 6));
//            ItemData item = ItemData.builder().name(productName).quantity(1).price(price).build();
            PaymentData paymentData = PaymentData.builder().orderCode(orderCode).amount(price).description(description)
                    .returnUrl(returnUrl).cancelUrl(cancelUrl).items(items).build();
            CheckoutResponseData data = payOS.createPaymentLink(paymentData);

            String checkoutUrl = data.getCheckoutUrl();

            httpServletResponse.setHeader("Location", checkoutUrl);
            httpServletResponse.setStatus(302);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/hoa-don/thanh-toan-that-bai")
    public String thatBai(RedirectAttributes redirectAttributes,@RequestParam("hoadonId") Long idHD){
        redirectAttributes.addFlashAttribute("checkoutFail", true);
        return "redirect:/admin/ban-hang/hoa-don/chi-tiet?hoadonId="+idHD;
    }

    @Transactional
    @GetMapping("/hoa-don/xac-nhan-chuyen-khoan")
    public String xacNhanChuyenKhoan(
            @RequestParam("idhd") Long idHD, Model model, Authentication authentication, RedirectAttributes redirectAttributes) {

        // Kiểm tra người dùng đã đăng nhập hay chưa
        if (authentication != null) {
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            TaiKhoan user = customUserDetail.getUser();
            model.addAttribute("userLogged", user); // Gửi thông tin người dùng vào model
        }

        // Tìm hóa đơn theo ID
        HoaDon hoaDon = hoaDonService.findById(idHD);
        // Lấy danh sách chi tiết hóa đơn theo ID hóa đơn
        List<HoaDonChiTiet> listHDCT = hoaDonChiTietRepository.getHoaDonChiTietByIdHd(idHD);

        // Nếu hóa đơn không có chi tiết, chuyển hướng về trang chi tiết hóa đơn với thông báo lỗi
        if (listHDCT== null) {
            System.out.println("Hóa đơn trống");
            redirectAttributes.addFlashAttribute("isInvoiceEmptyCheckout", true);
            return "redirect:/admin/ban-hang/hoa-don/chi-tiet?hoadonId=" + idHD;
        }

        for (int i = 0 ; i< listHDCT.size() ; i ++){
            ChiTietSanPham chiTietSanPham = chiTietSanPhamRepository.getReferenceById(listHDCT.get(i).getChiTietSanPham().getId());
            HoaDonChiTiet hdct = new HoaDonChiTiet();
            hdct = listHDCT.get(i);
            hdct.setDonGia(chiTietSanPham.getGiaBan().floatValue());
            hoaDonChiTietRepository.save(hdct);
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
        hoaDon.setTienThu(totalMoneyAfter);
        // Cập nhật thông tin giảm giá, tổng tiền và trạng thái cho hóa đơn
        hoaDon.setSoTienGiamGia((Double) discount);
        hoaDon.setTongTien((Double) totalMoneyBefore);
        hoaDon.setTrangThai(1); // Đặt trạng thái hóa đơn đã thanh toán
        hoaDonService.save(hoaDon);

        if (voucher!=null) {
            Integer soLuongVoucherCu = voucher.getSoLuong();
            voucher.setSoLuong(soLuongVoucherCu-1);
            maGiamGiaRepo.save(voucher);
        }


        // Thêm thông báo thành công trước khi chuyển hướng
        redirectAttributes.addFlashAttribute("checkoutSuccess", true);

        // Chuyển hướng về trang /admin/ban-hang
        return "redirect:/admin/ban-hang";
    }

    @Transactional
    @GetMapping("/hoa-don/xac-nhan-thanh-toan")
    public ResponseEntity<?> xacNhanThanhToan(
            @RequestParam("idhd") Long idHD,
            Authentication authentication) {
        try {
            // Kiểm tra người dùng đã đăng nhập hay chưa
            if (authentication == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Người dùng chưa đăng nhập."); // Thông báo lỗi nếu chưa đăng nhập
            }
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            TaiKhoan user = customUserDetail.getUser();

            // Tìm hóa đơn theo ID
            HoaDon hoaDon = hoaDonService.findById(idHD);
            if (hoaDon == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Không tìm thấy hóa đơn."); // Thông báo nếu hóa đơn không tồn tại
            }

            // Lấy danh sách chi tiết hóa đơn
            List<HoaDonChiTiet> listHDCT = hoaDonChiTietRepository.getHoaDonChiTietByIdHd(idHD);
            if (listHDCT == null || listHDCT.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Hóa đơn không có sản phẩm."); // Thông báo nếu hóa đơn rỗng
            }

            // Cập nhật chi tiết hóa đơn và tính tổng tiền trước giảm giá
            for (HoaDonChiTiet hdct : listHDCT) {
                ChiTietSanPham chiTietSanPham = chiTietSanPhamRepository.getReferenceById(hdct.getChiTietSanPham().getId());
                hdct.setDonGia(chiTietSanPham.getGiaBan().floatValue());
                hoaDonChiTietRepository.save(hdct);
            }

            double totalMoneyBefore = listHDCT.stream()
                    .mapToDouble(hdct -> {
                        int soLuong = (hdct.getSoLuong() != null) ? hdct.getSoLuong() : 0;
                        double giaBan = (hdct.getChiTietSanPham() != null && hdct.getChiTietSanPham().getGiaBan() != null)
                                ? hdct.getChiTietSanPham().getGiaBan() : 0.0;
                        return soLuong * giaBan;
                    })
                    .sum();

            // Xử lý mã giảm giá
            MaGiamGia voucher = hoaDon.getMaGiamGia();
            double discount = 0.0;
            if (voucher != null) {
                if (!voucher.isValid()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Mã giảm giá đã hết hạn, vui lòng chọn lại."); // Thông báo nếu mã giảm giá không hợp lệ
                }
                if (voucher.getHinhThuc().equals(false)) { // Giảm giá theo %
                    discount = (voucher.getGiaTriGiam() / 100.0) * totalMoneyBefore;
                    discount = Math.min(discount, voucher.getGiaTriToiDa());
                } else if (voucher.getHinhThuc().equals(true)) { // Giảm giá cố định
                    discount = voucher.getGiaTriGiam();
                }

                if (voucher.getSoLuong() <= 0) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Mã giảm giá đã hết số lượng."); // Thông báo nếu mã giảm giá đã hết
                }
            }

            // Tính tổng tiền sau giảm giá
            double totalMoneyAfter = Math.max(totalMoneyBefore - discount, 0);
            hoaDon.setTienThu(totalMoneyAfter);
            hoaDon.setSoTienGiamGia(discount);
            hoaDon.setTongTien(totalMoneyBefore);
            hoaDon.setTrangThai(1); // Đánh dấu hóa đơn đã thanh toán
            hoaDonService.save(hoaDon);

            // Cập nhật số lượng mã giảm giá
            if (voucher != null) {
                voucher.setSoLuong(voucher.getSoLuong() - 1);
                maGiamGiaRepo.save(voucher);
            }

            // Trả về phản hồi thành công
            return ResponseEntity.ok("Thanh toán thành công!"); // Thông báo nếu thanh toán thành công
        } catch (Exception e) {
            // Xử lý các lỗi không mong muốn
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Đã xảy ra lỗi trong quá trình thanh toán: " + e.getMessage()); // Thông báo lỗi chung
        }
    }




    @GetMapping("/huy-hoa-don-tai-quay")
    public String huyHD(@RequestParam("hoadonId") Long idhd, Model model, Authentication authentication, RedirectAttributes redirectAttributes) {
        if (authentication != null) {
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            TaiKhoan user = customUserDetail.getUser();
            model.addAttribute("userLogged", user);
        }
        List<HoaDonChiTiet> listHoaDonChiTiet = hoaDonChiTietService.getListHdctByIdHd(idhd);
        for (HoaDonChiTiet hoaDonChiTiet : listHoaDonChiTiet) {
            Integer soLuongHoi = hoaDonChiTiet.getSoLuong();
            ChiTietSanPham chiTietSanPham = hoaDonChiTiet.getChiTietSanPham();
            Integer soLuongCu = chiTietSanPham.getSoLuong();
            chiTietSanPham.setTrangThai(0);
            chiTietSanPham.setSoLuong(soLuongCu+soLuongHoi);
            chiTietSanPhamRepository.save(chiTietSanPham);
        }
        hoaDonService.updateTrangThaiHD(2, idhd);
        redirectAttributes.addFlashAttribute("isCancelInvoice", true);
        return "redirect:/admin/ban-hang";
    }


    @GetMapping("/xoa/{id}")
    public String xoaHD(@PathVariable("id") Long idhd, Model model, Authentication authentication) {
        if (authentication != null) {
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            TaiKhoan user = customUserDetail.getUser();
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
            TaiKhoan user = customUserDetail.getUser();
            hoaDon.setNhanVien(user.getNhanVien());
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

        // Check if 'soLuong' is null or invalid
        if (soLuongMua == null || soLuongMua < 1) {
            redirectAttributes.addFlashAttribute("error", "Số lượng không hợp lệ.");
            return "redirect:/admin/ban-hang/hoa-don/chi-tiet?hoadonId=" + idhd;
        }


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

                    HoaDon hoaDon = hoaDonService.findById(idhd);
                    //Xử lí nếu hóa đơn đã có mã giảm giá
                    if (hoaDon.getMaGiamGia()!=null){
                        if (hoaDon.getMaGiamGia().getGiaTriToiThieu()>tongTien) {
                            hoaDon.setSoTienGiamGia(0.0);
                            hoaDon.setTienThu(tongTien);
                            hoaDon.setMaGiamGia(null);
                            hoaDonService.save(hoaDon);
                            System.out.println(234);
                        } else {

                            MaGiamGia voucher1 = hoaDon.getMaGiamGia();
                            double discount = 0.0;

                            // Nếu có mã giảm giá, tính số tiền giảm
                            if (voucher1 != null) {
                                if (Boolean.FALSE.equals(voucher1.getHinhThuc())) { // Giảm giá theo phần trăm
                                    discount = (voucher1.getGiaTriGiam() / 100.0) * tongTien; // Tính phần trăm giảm giá
                                    if (discount > voucher1.getGiaTriToiDa()) {
                                        discount = voucher1.getGiaTriToiDa(); // Giới hạn giảm giá tối đa
                                    }
                                } else { // Giảm giá cố định
                                    discount = voucher1.getGiaTriGiam();
                                }
                            }

                            hoaDon.setTienThu(tongTien-discount);
                            hoaDon.setSoTienGiamGia(discount);
                            hoaDonService.save(hoaDon);
                        }
                    } else {
                        hoaDon.setTienThu(tongTien);
                        System.out.println(123);
                        hoaDonService.save(hoaDon);
                    }

                    // Thông báo thêm thành công
                    redirectAttributes.addFlashAttribute("addSuccess", true);
                    return "redirect:/admin/ban-hang/hoa-don/chi-tiet?hoadonId=" + idhd;
                }
            }
        } else {
            // Nếu sản phẩm chưa tồn tại trong HDCT, tạo mới một HDCT

            // Lấy thông tin sản phẩm chi tiết (CTSP)
            ChiTietSanPham chiTietSanPham1 = chiTietSanPhamService.findById(idctsp);

            // Kiểm tra số lượng tồn kho có đủ không
            if (soLuongMua > chiTietSanPham1.getSoLuong()) {
                redirectAttributes.addFlashAttribute("isQuantityNotEnough", true);
                redirectAttributes.addFlashAttribute("messageQuantityNotEnough", "Số lượng không đủ, chỉ còn " + chiTietSanPham1.getSoLuong() + " sản phẩm");
                return "redirect:/admin/ban-hang/hoa-don/chi-tiet?hoadonId=" + idhd;
            }
            HoaDonChiTiet hoaDonChiTiet = new HoaDonChiTiet();
            HoaDon hoaDon = hoaDonService.findById(idhd);
            hoaDon.setId(idhd);
            ChiTietSanPham chiTietSanPham = new ChiTietSanPham();
            chiTietSanPham.setId(idctsp);
            hoaDonChiTiet.setHoaDon(hoaDon);
            hoaDonChiTiet.setChiTietSanPham(chiTietSanPham);
            hoaDonChiTiet.setSoLuong(soLuongMua);

            // Lưu HDCT mới vào cơ sở dữ liệu
            hoaDonChiTietService.save(hoaDonChiTiet);

            // Lấy thông tin sản phẩm chi tiết (CTSP)
            int soLuongSauUpdate;

            // Kiểm tra số lượng tồn kho có đủ không
            if (chiTietSanPham1.getSoLuong() < soLuongMua) {
                soLuongSauUpdate = chiTietSanPham1.getSoLuong();
            } else {
                soLuongSauUpdate = chiTietSanPham1.getSoLuong() - soLuongMua;
            }

            // Cập nhật lại số lượng tồn kho
            chiTietSanPham1.setSoLuong(soLuongSauUpdate);
            if (soLuongSauUpdate == 0){
                chiTietSanPham1.setTrangThai(1);
            }
            chiTietSanPhamService.save(chiTietSanPham1);

            // Cập nhật tổng tiền hóa đơn
            List<HoaDonChiTiet> listHdct = hoaDonChiTietService.getListHdctByIdHd(idhd);
            Double tongTien = 0.0;
            for (HoaDonChiTiet hdct : listHdct) {
                tongTien += hdct.getChiTietSanPham().getGiaBan() * hdct.getSoLuong();
            }
            hoaDonService.updateTongTien(idhd, tongTien);

            HoaDon hoaDon1 = hoaDonService.findById(idhd);
            //Xử lí nếu hóa đơn đã có mã giảm giá
            if (hoaDon1.getMaGiamGia()!=null){
                if (hoaDon1.getMaGiamGia().getGiaTriToiThieu()>tongTien) {
                    hoaDon1.setSoTienGiamGia(0.0);
                    hoaDon1.setTienThu(tongTien);
                    hoaDon1.setMaGiamGia(null);
                    hoaDonService.save(hoaDon1);
                } else {
                    MaGiamGia voucher1 = hoaDon.getMaGiamGia();
                    double discount = 0.0;
                    // Nếu có mã giảm giá, tính số tiền giảm
                    if (voucher1 != null) {
                        if (Boolean.FALSE.equals(voucher1.getHinhThuc())) { // Giảm giá theo phần trăm
                            discount = (voucher1.getGiaTriGiam() / 100.0) * tongTien; // Tính phần trăm giảm giá
                            if (discount > voucher1.getGiaTriToiDa()) {
                                discount = voucher1.getGiaTriToiDa(); // Giới hạn giảm giá tối đa
                            }
                        } else { // Giảm giá cố định
                            discount = voucher1.getGiaTriGiam();
                        }
                    }
                    hoaDon.setTongTien(tongTien);
                    hoaDon.setTienThu(tongTien-discount);
                    hoaDon.setSoTienGiamGia(discount);
                    hoaDonService.save(hoaDon);
                }
            } else {
                System.out.println(345);
                hoaDon1.setTienThu(tongTien);
                hoaDonService.save(hoaDon1);
            }

            // Thông báo thêm thành công
            redirectAttributes.addFlashAttribute("addSuccess",true);
        }
        return "redirect:/admin/ban-hang/hoa-don/chi-tiet?hoadonId=" + idhd;
    }


    @PostMapping("/chon-khach-hang")
    public String chonKhachHang(@RequestParam("idhd") Long idhd,
                                @RequestParam("idkh") Long idkh,
                                RedirectAttributes redirectAttributes) {
        System.out.println(idhd);
        HoaDon existingHoaDon = hoaDonRepository.findById(idhd).orElseThrow(() -> new ResourceNotFoundException("Hóa đơn không tồn tại với ID: " + idhd));
        KhachHang khachHang = new KhachHang();
        khachHang.setId(idkh);
        existingHoaDon.setKhachHang(khachHang);
        hoaDonService.save(existingHoaDon);
        redirectAttributes.addFlashAttribute("addCustomerToInvoiceSuccess",true);
        return "redirect:/admin/ban-hang/hoa-don/chi-tiet?hoadonId=" + idhd;
    }

    @Transactional
    @GetMapping("/huy-khach-hang")
    public String huyKhachHang(@RequestParam("hoadonId") Long idhd, RedirectAttributes redirectAttributes) {
        HoaDon hoaDon = hoaDonRepository.getReferenceById(idhd);
        if (hoaDon.getKhachHang()==null){
            redirectAttributes.addFlashAttribute("nullKH",true);
            return "redirect:/admin/ban-hang/hoa-don/chi-tiet?hoadonId=" + idhd;
        }
        hoaDon.setKhachHang(null);
        redirectAttributes.addFlashAttribute("cancelKH",true);
        hoaDonRepository.save(hoaDon);
        return "redirect:/admin/ban-hang/hoa-don/chi-tiet?hoadonId=" + idhd;
    }

    @Transactional
    @GetMapping("/huy-ma-giam-gia")
    public String huyMgg(@RequestParam("hoadonId") Long idhd, RedirectAttributes redirectAttributes) {
        HoaDon hoaDon = hoaDonRepository.getReferenceById(idhd);
        if (hoaDon.getMaGiamGia()==null){
            redirectAttributes.addFlashAttribute("nullMGG",true);
            return "redirect:/admin/ban-hang/hoa-don/chi-tiet?hoadonId=" + idhd;
        }else {
            try {
                List<HoaDonChiTiet> listHDCT = hoaDonChiTietService.getHDCTByIdHD(idhd);
                double totalMoneyBefore = listHDCT.stream()
                        .mapToDouble(hdct -> {
                            int soLuong = (hdct.getSoLuong() != null) ? hdct.getSoLuong() : 0; // Kiểm tra số lượng
                            double giaBan = (hdct.getChiTietSanPham() != null && hdct.getChiTietSanPham().getGiaBan() != null)
                                    ? hdct.getChiTietSanPham().getGiaBan() : 0.0; // Kiểm tra giá bán
                            return soLuong * giaBan;
                        })
                        .sum();
                hoaDon.setMaGiamGia(null);
                hoaDon.setSoTienGiamGia(0.0);
                hoaDon.setTienThu(totalMoneyBefore);
                redirectAttributes.addFlashAttribute("cancelMGG",true);
                hoaDonRepository.save(hoaDon);
            }catch (Exception e){
                e.printStackTrace();
            }
            return "redirect:/admin/ban-hang/hoa-don/chi-tiet?hoadonId=" + idhd;
        }
    }

    @PostMapping("/chon-khuyen-mai")
    public String chonKhuyenMai(@RequestParam("idhd") Long idhd,
                                @RequestParam("idkm") Long idkm,
                                RedirectAttributes redirectAttributes) {

        try {
            // Tìm hóa đơn theo ID
            HoaDon existingHoaDon = hoaDonRepository.findById(idhd)
                    .orElseThrow(() -> new ResourceNotFoundException("Hóa đơn không tồn tại với ID: " + idhd));

            // Thiết lập mã giảm giá cho hóa đơn
            MaGiamGia voucher = new MaGiamGia();
            voucher.setId(idkm);
            existingHoaDon.setMaGiamGia(voucher);

            // Lưu hóa đơn sau khi thêm mã giảm giá
            HoaDon hoaDon1 = hoaDonRepository.save(existingHoaDon);

            // Tính toán tổng tiền trước giảm giá
            List<HoaDonChiTiet> listHDCT = hoaDonChiTietService.getListHdctByIdHd(idhd);
            double totalMoneyBefore = listHDCT.stream()
                    .mapToDouble(hdct -> {
                        int soLuong = (hdct.getSoLuong() != null) ? hdct.getSoLuong() : 0; // Lấy số lượng, mặc định là 0 nếu null
                        double giaBan = (hdct.getChiTietSanPham() != null && hdct.getChiTietSanPham().getGiaBan() != null)
                                ? hdct.getChiTietSanPham().getGiaBan() : 0.0; // Lấy giá bán, mặc định là 0.0 nếu null
                        return soLuong * giaBan; // Tính tổng tiền từng chi tiết hóa đơn
                    })
                    .sum();

            // Lấy thông tin mã giảm giá đã áp dụng
            MaGiamGia voucher1 = hoaDon1.getMaGiamGia();
            double discount = 0.0;

            // Nếu có mã giảm giá, tính số tiền giảm
            if (voucher1 != null) {
                if (Boolean.FALSE.equals(voucher1.getHinhThuc())) { // Giảm giá theo phần trăm
                    discount = (voucher1.getGiaTriGiam() / 100.0) * totalMoneyBefore; // Tính phần trăm giảm giá
                    if (discount > voucher1.getGiaTriToiDa()) {
                        discount = voucher1.getGiaTriToiDa(); // Giới hạn giảm giá tối đa
                    }
                } else { // Giảm giá cố định
                    discount = voucher1.getGiaTriGiam();
                }
            }

            // Tính tổng tiền sau khi giảm giá, đảm bảo không âm
            double totalMoneyAfter = Math.max(totalMoneyBefore - discount, 0);

            // Cập nhật lại hóa đơn với các thông tin mới
            hoaDon1.setTongTien(totalMoneyBefore); // Tổng tiền trước giảm giá
            hoaDon1.setSoTienGiamGia(discount); // Số tiền giảm giá
            hoaDon1.setTienThu(totalMoneyAfter); // Tổng tiền sau giảm giá

            // Lưu hóa đơn sau khi cập nhật
            hoaDonService.save(hoaDon1);

            // Thêm thông báo thành công
            redirectAttributes.addFlashAttribute("addVoucherSuccess", true);
            redirectAttributes.addFlashAttribute("messageAddVoucher", "Áp dụng mã giảm giá thành công!");

        } catch (Exception e) {
            // Thêm thông báo lỗi nếu có ngoại lệ xảy ra
            redirectAttributes.addFlashAttribute("addVoucherFailed", true);
            redirectAttributes.addFlashAttribute("messageAddVoucher", "Lỗi khi áp dụng mã giảm giá: " + e.getMessage());
            System.out.println(e.getMessage()); // Log lỗi để kiểm tra
        }

        // Chuyển hướng về trang chi tiết hóa đơn
        return "redirect:/admin/ban-hang/hoa-don/chi-tiet?hoadonId=" + idhd;
    }



    @GetMapping("/hoa-don/xoa-sp")
    public String deleteSpctInHoaDon(@RequestParam("idHdct") Long idHdct, RedirectAttributes redirectAttributes) {
        // Lấy thông tin Chi Tiết Hóa Đơn từ idHdct
        HoaDonChiTiet hoaDonChiTiet = hoaDonChiTietService.findById(idHdct);

        // Lấy ID của hóa đơn (để sau này cập nhật lại thông tin)
        Long idhd = hoaDonChiTiet.getHoaDon().getId();
        HoaDon hoaDon = hoaDonService.findById(idhd);

        // Lấy số lượng của sản phẩm chi tiết trong hóa đơn
        Integer soLuong = hoaDonChiTiet.getSoLuong();

        // Lấy thông tin chi tiết sản phẩm
        ChiTietSanPham chiTietSanPham = hoaDonChiTiet.getChiTietSanPham();

        // Tính toán số lượng cập nhật sau khi xóa sản phẩm khỏi hóa đơn
        // (tăng số lượng sản phẩm trong kho lên số lượng đã xóa khỏi hóa đơn)
        Integer soLuongUpdate = chiTietSanPhamService.findById(chiTietSanPham.getId()).getSoLuong() + soLuong;
        chiTietSanPham.setTrangThai(0);
        chiTietSanPham.setSoLuong(soLuongUpdate);

        // Xóa sản phẩm chi tiết khỏi hóa đơn
        hoaDonChiTietService.deleteById(idHdct);

        // Cập nhật lại số lượng sản phẩm trong kho
//        chiTietSanPhamService.updateSoLuongCtsp(soLuongUpdate, chiTietSanPham.getId());

        chiTietSanPhamRepository.save(chiTietSanPham);

        // Thêm một thuộc tính flash vào model để thông báo việc xóa thành công
        redirectAttributes.addFlashAttribute("deleteSuccess", true);

        // Lấy lại danh sách các sản phẩm chi tiết trong hóa đơn (sau khi xóa)
        List<HoaDonChiTiet> listHdct = hoaDonChiTietService.getListHdctByIdHd(idhd);

        // Tính toán tổng tiền của hóa đơn sau khi xóa sản phẩm
        Double tongTien = 0.0;
        for (HoaDonChiTiet hdct : listHdct) {
            tongTien += hdct.getChiTietSanPham().getGiaBan() * hdct.getSoLuong();
        }
        //Xử lí nếu hóa đơn đã có mã giảm giá
        if (hoaDon.getMaGiamGia()!=null){
            if (hoaDon.getMaGiamGia().getGiaTriToiThieu()>tongTien) {
                hoaDon.setSoTienGiamGia(0.0);
                hoaDon.setTienThu(tongTien);
                hoaDon.setMaGiamGia(null);
                hoaDonService.save(hoaDon);
            } else {
                MaGiamGia voucher1 = hoaDon.getMaGiamGia();
                double discount = 0.0;

                // Nếu có mã giảm giá, tính số tiền giảm
                if (voucher1 != null) {
                    if (Boolean.FALSE.equals(voucher1.getHinhThuc())) { // Giảm giá theo phần trăm
                        discount = (voucher1.getGiaTriGiam() / 100.0) * tongTien; // Tính phần trăm giảm giá
                        if (discount > voucher1.getGiaTriToiDa()) {
                            discount = voucher1.getGiaTriToiDa(); // Giới hạn giảm giá tối đa
                        }
                    } else { // Giảm giá cố định
                        discount = voucher1.getGiaTriGiam();
                    }
                }

                hoaDon.setTienThu(tongTien-discount);
                hoaDon.setSoTienGiamGia(discount);
                hoaDonService.save(hoaDon);
            }
        } else {
            hoaDon.setTienThu(tongTien);
            hoaDonService.save(hoaDon);
        }

        // Cập nhật lại tổng tiền của hóa đơn
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
            Integer soLuongCu = chiTietSanPham.getSoLuong();
//            chiTietSanPhamService.updateSoLuongCtsp((chiTietSanPham.getSoLuong() + soLuongHoi), chiTietSanPham.getId());
            chiTietSanPham.setSoLuong(soLuongCu+soLuongHoi);
            chiTietSanPham.setTrangThai(0);
            chiTietSanPhamRepository.save(chiTietSanPham);
            hoaDonChiTietService.deleteById(hoaDonChiTiet.getId());
        }
        HoaDon hoaDon = hoaDonService.findById(idHD);
        hoaDon.setTongTien(0.0);
        hoaDon.setMaGiamGia(null);
        hoaDon.setSoTienGiamGia(0.0);
        hoaDon.setTienThu(0.0);
        hoaDonService.save(hoaDon);
        redirectAttributes.addFlashAttribute("deleteAllSuccess", true);
        return "redirect:/admin/ban-hang/hoa-don/chi-tiet?hoadonId=" + idHD;
    }

    @PostMapping("/hoa-don/sua-so-luong")
    public String updateSoLuongSua(@RequestParam("soLuongSua") Integer soLuongSua,
                                   @RequestParam("idHdctSua") Long idHdct,
                                   RedirectAttributes redirectAttributes) {
        // Lấy chi tiết hóa đơn dựa vào idHdct
        HoaDonChiTiet hoaDonChiTiet = hoaDonChiTietService.findById(idHdct);
        Long idhd = hoaDonChiTiet.getHoaDon().getId(); // Lấy ID hóa đơn từ chi tiết hóa đơn
        Integer soLuongHienTai = hoaDonChiTiet.getSoLuong(); // Lấy số lượng hiện tại từ chi tiết hóa đơn

        // Lấy chi tiết sản phẩm từ chi tiết hóa đơn
        ChiTietSanPham chiTietSanPham = hoaDonChiTiet.getChiTietSanPham();
        Integer soLuongKhaDung = chiTietSanPham.getSoLuong() + soLuongHienTai; // Tính số lượng khả dụng

        // Kiểm tra nếu số lượng sửa vượt quá số lượng khả dụng
        if (soLuongSua > soLuongKhaDung) {
            redirectAttributes.addFlashAttribute("QuantityUpdateError", true); // Thêm thông báo lỗi
            return "redirect:/admin/ban-hang/hoa-don/chi-tiet?hoadonId=" + idhd; // Quay lại trang chi tiết hóa đơn
        }

        // Cập nhật số lượng trong chi tiết hóa đơn
        hoaDonChiTietService.updateSoLuongHdct(soLuongSua, idHdct);

        // Tính chênh lệch số lượng và cập nhật số lượng kho
        Integer chenhLechSl = soLuongSua - soLuongHienTai;
        Integer soLuongKho = hoaDonChiTiet.getChiTietSanPham().getSoLuong() - chenhLechSl;
        chiTietSanPhamService.updateSoLuongCtsp(soLuongKho, hoaDonChiTiet.getChiTietSanPham().getId());
        System.out.println(soLuongKho);
        if (soLuongKho>0) {
            chiTietSanPhamRepository.updateTrangThai(0,chiTietSanPham.getId());
        }

        // Tính tổng tiền của hóa đơn sau khi cập nhật
        List<HoaDonChiTiet> listHdct = hoaDonChiTietService.getListHdctByIdHd(idhd);
        Double tongTien = 0.0;
        for (HoaDonChiTiet hdct : listHdct) {
            tongTien += hdct.getChiTietSanPham().getGiaBan() * hdct.getSoLuong();
        }
        hoaDonService.updateTongTien(idhd, tongTien); // Cập nhật tổng tiền cho hóa đơn

        // Xử lý mã giảm giá (nếu có)
        HoaDon hoaDon = hoaDonService.findById(idhd);
        if (hoaDon.getMaGiamGia() != null) {
            if (hoaDon.getMaGiamGia().getGiaTriToiThieu() > tongTien) {
                // Nếu không đủ điều kiện áp dụng mã giảm giá
                hoaDon.setSoTienGiamGia(0.0);
                hoaDon.setTienThu(tongTien);
                hoaDon.setMaGiamGia(null); // Xóa mã giảm giá
                hoaDonService.save(hoaDon);
            } else {
                // Tính toán giảm giá
                MaGiamGia voucher1 = hoaDon.getMaGiamGia();
                double discount = 0.0;
                if (Boolean.FALSE.equals(voucher1.getHinhThuc())) { // Giảm giá theo phần trăm
                    discount = (voucher1.getGiaTriGiam() / 100.0) * tongTien;
                    if (discount > voucher1.getGiaTriToiDa()) {
                        discount = voucher1.getGiaTriToiDa(); // Giới hạn giảm giá tối đa
                    }
                } else { // Giảm giá cố định
                    discount = voucher1.getGiaTriGiam();
                }

                hoaDon.setTienThu(tongTien - discount); // Cập nhật tiền thu
                hoaDon.setSoTienGiamGia(discount); // Cập nhật số tiền giảm giá
                hoaDonService.save(hoaDon);
            }
        } else {
            // Không có mã giảm giá, chỉ cập nhật tiền thu
            hoaDon.setTienThu(tongTien);
            hoaDonService.save(hoaDon);
        }

        // Thêm thông báo thành công
        redirectAttributes.addFlashAttribute("updateSuccess", true);
        return "redirect:/admin/ban-hang/hoa-don/chi-tiet?hoadonId=" + idhd; // Quay lại trang chi tiết hóa đơn
    }


}
