package com.project.ttaptshirt.controller.adminController;

import com.project.ttaptshirt.dto.NumberUtils;
import com.project.ttaptshirt.entity.*;
import com.project.ttaptshirt.repository.ChiTietSanPhamRepository;
import com.project.ttaptshirt.repository.HinhAnhRepository;
import com.project.ttaptshirt.repository.HoaDonChiTietRepository;
import com.project.ttaptshirt.repository.HoaDonRepository;
import com.project.ttaptshirt.security.CustomUserDetail;
import com.project.ttaptshirt.service.ChiTietSanPhamService;
import com.project.ttaptshirt.service.HoaDonChiTietService;
import com.project.ttaptshirt.service.HoaDonLogService;
import com.project.ttaptshirt.service.HoaDonService;
import com.project.ttaptshirt.service.impl.MaGiamGiaServicelmpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/hoa-don")
public class HoaDonController {

    @Autowired
    HoaDonRepository hr;

    @Autowired
    HoaDonService hoaDonService;
    @Autowired
    HoaDonChiTietService hoaDonChiTietService;

    @Autowired
    HoaDonChiTietRepository hoaDonChiTietRepository;

    @Autowired
    ChiTietSanPhamService chiTietSanPhamService;

    @Autowired
    ChiTietSanPhamRepository chiTietSanPhamRepo;

    @Autowired
    HoaDonLogService hoaDonLogService;

    @Autowired
    private HinhAnhRepository hinhAnhRepository;
    @Autowired
    private MaGiamGiaServicelmpl maGiamGiaServicelmpl;

    @GetMapping("/hien-thi")
    public String hienThi(Model model, @RequestParam(defaultValue = "0") Integer page,
                          @RequestParam(required = false, value = "id") Long id, Authentication authentication) {
        if (authentication != null) {
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            TaiKhoan user = customUserDetail.getUser();
            model.addAttribute("userLogged", user);
        }
        Pageable pageab = PageRequest.of(page, 5);
        model.addAttribute("listHDCT", hoaDonChiTietRepository.getHoaDonChiTietByHoaDonId(id));
        model.addAttribute("listSPOrder", hoaDonChiTietRepository.getHoaDonChiTietByHoaDonId(id));
        model.addAttribute("listHD", hr.getAllHD(pageab));
        model.addAttribute("page", page);
        model.addAttribute("id", id);
        if (hr.getAllHD(pageab).size() == 0) {
            model.addAttribute("nullhd", "Không có hóa đơn nào");
        }
        NumberUtils numberUtils = new NumberUtils();
        model.addAttribute("numberUtils", numberUtils);
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

    @GetMapping("/chi-tiet-hoa-don/{idhd}")
    public String hienThiHDCT(@PathVariable("idhd") Long idhd, Model model, Authentication authentication) {
        if (authentication != null) {
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            TaiKhoan user = customUserDetail.getUser();
            model.addAttribute("userLogged", user);
        }
        HoaDon hoaDon = hoaDonService.findById(idhd);
        model.addAttribute("hoaDon", hoaDon);
        List<HoaDonChiTiet> listSPOrder = hoaDonChiTietService.getListHdctByIdHd(idhd);
        model.addAttribute("listSPOrder", listSPOrder);
        NumberUtils numberUtils = new NumberUtils();
        model.addAttribute("numberUtils", numberUtils);
        List<HoaDonLog> listHoaDonLog = hoaDonLogService.getListHoaDonLogByIdHd(idhd);
        for (HoaDonLog hdlog : listHoaDonLog) {
            System.out.println(hdlog.getGhiChu());
        }
        model.addAttribute("listHoaDonLog", listHoaDonLog);
        // Lấy hình ảnh đầu tiên cho mỗi sản phẩm
        Map<Long, String> productImages = new HashMap<>();
        for (HoaDonChiTiet hdct : listSPOrder) {
            Long productId = hdct.getChiTietSanPham().getSanPham().getId();

            // Lấy hình ảnh đầu tiên của sản phẩm
            List<String> images = hinhAnhRepository.findBySanPhamId(productId);
            String firstImage = images.isEmpty() ? "/default-image.jpg" : images.get(0);
            productImages.put(productId, firstImage);
        }
        model.addAttribute("productImages", productImages);
        return "admin/hoadon/chi-tiet-hoa-don-tai-quay";
    }

    @GetMapping("/chi-tiet-hoa-don-online/{idhd}")
    public String hienThiHDCTOnline(@PathVariable("idhd") Long idhd, Model model, Authentication authentication) {
        if (authentication != null) {
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            TaiKhoan user = customUserDetail.getUser();
            model.addAttribute("userLogged", user);
        }
        HoaDon hoaDon = hoaDonService.findById(idhd);
        model.addAttribute("hoaDon", hoaDon);
        List<HoaDonChiTiet> listSPOrder = hoaDonChiTietService.getListHdctByIdHd(idhd);
        model.addAttribute("listSPOrder", listSPOrder);
        NumberUtils numberUtils = new NumberUtils();
        model.addAttribute("numberUtils", numberUtils);
        List<HoaDonLog> listHoaDonLog = hoaDonLogService.getListHoaDonLogByIdHd(idhd);
        for (HoaDonLog hdlog : listHoaDonLog) {
            System.out.println(hdlog.getGhiChu());
        }
        model.addAttribute("listHoaDonLog", listHoaDonLog);
        // Lấy hình ảnh đầu tiên cho mỗi sản phẩm
        Map<Long, String> productImages = new HashMap<>();
        for (HoaDonChiTiet hdct : listSPOrder) {
            Long productId = hdct.getChiTietSanPham().getSanPham().getId();

            // Lấy hình ảnh đầu tiên của sản phẩm
            List<String> images = hinhAnhRepository.findBySanPhamId(productId);
            String firstImage = images.isEmpty() ? "/default-image.jpg" : images.get(0);
            productImages.put(productId, firstImage);
        }
        model.addAttribute("productImages", productImages);
        return "admin/hoadon/chi-tiet-hoa-don-online";
    }

    @GetMapping("/xac-nhan-hoa-don/{idHD}")
    public String xacNhanHD(@PathVariable("idHD") Long idHD, RedirectAttributes redirectAttributes, Authentication authentication) {
        CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
        TaiKhoan user = customUserDetail.getUser();
        List<HoaDonChiTiet> listSanPham = hoaDonChiTietService.getListHdctByIdHd(idHD);
        for (HoaDonChiTiet hdct : listSanPham) {

            ChiTietSanPham chiTietSanPham = hdct.getChiTietSanPham();
            if (chiTietSanPham.getSoLuong() < hdct.getSoLuong() || chiTietSanPham.getTrangThai() != 0) {
                redirectAttributes.addFlashAttribute("confirmErrorMessage", true);
                HoaDonLog hoaDonLog = new HoaDonLog();
                hoaDonLog.setHoaDon(hdct.getHoaDon());
                hoaDonLog.setHanhDong("Xác nhận");
                hoaDonLog.setThoiGian(LocalDateTime.now());
                hoaDonLog.setNguoiThucHien(user.getUsername());
                if (chiTietSanPham.getSoLuong() < hdct.getSoLuong()) {
                    hoaDonLog.setGhiChu("xác nhận(số lượng sản phẩm không đủ)");
                } else {
                    hoaDonLog.setGhiChu("xác nhận(Sản phẩm đã hết hàng hoặc ngừng bán !)");
                }
                hoaDonLog.setTrangThai(1);
                hoaDonLogService.save(hoaDonLog);
                return "redirect:/admin/hoa-don/chi-tiet-hoa-don-online/" + idHD;
            }


            chiTietSanPham.setSoLuong(chiTietSanPham.getSoLuong() - hdct.getSoLuong());
            chiTietSanPhamService.save(chiTietSanPham);

            MaGiamGia maGiamGia = hdct.getHoaDon().getMaGiamGia();
            if (maGiamGia != null) {
                if (maGiamGia.getSoLuong() > 0) { // Chỉ giảm số lượng nếu còn > 0
                    maGiamGia.setSoLuong(maGiamGia.getSoLuong() - 1);
                    maGiamGiaServicelmpl.save(maGiamGia);
                    break;
                } else {
                    // Log lỗi nếu số lượng mã giảm giá đã hết
                    redirectAttributes.addFlashAttribute("confirmErrorMessage", true);

                    HoaDonLog hoaDonLog = new HoaDonLog();
                    hoaDonLog.setHoaDon(hdct.getHoaDon());
                    hoaDonLog.setHanhDong("Xác nhận");
                    hoaDonLog.setThoiGian(LocalDateTime.now());
                    hoaDonLog.setNguoiThucHien(user.getUsername());
                    hoaDonLog.setGhiChu("Xác nhận thất bại: mã giảm giá đã hết số lượng");
                    hoaDonLog.setTrangThai(1);
                    hoaDonLogService.save(hoaDonLog);

                    // Dừng xử lý và quay lại trang chi tiết hóa đơn
                    return "redirect:/admin/hoa-don/chi-tiet-hoa-don-online/" + idHD;
                }
            }
        }
        HoaDonLog hoaDonLog = new HoaDonLog();
        HoaDon hoaDon2 = hoaDonService.findById(idHD);
        hoaDonLog.setHoaDon(hoaDon2);
        hoaDonLog.setHanhDong("Xác nhận");
        hoaDonLog.setThoiGian(LocalDateTime.now());
        hoaDonLog.setNguoiThucHien(user.getUsername());

        hoaDonLog.setGhiChu("đã thực hiện xác nhận đơn hàng online");
        hoaDonLog.setTrangThai(0);
        hoaDonLogService.save(hoaDonLog);
        hoaDonService.xacNhanHoaDon(idHD);
        // Add a flash attribute for success message
        redirectAttributes.addFlashAttribute("successMessage", "Đơn hàng đã được xác nhận!");
        return "redirect:/admin/hoa-don/chi-tiet-hoa-don-online/" + idHD;
    }

    @GetMapping("/hoa-don-chuan-bi-hang/{idHD}")
    public String hdChuanBiHang(@PathVariable("idHD") Long idHD, RedirectAttributes redirectAttributes, Authentication authentication) {
        CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
        TaiKhoan user = customUserDetail.getUser();
        hoaDonService.hdChuanBiHang(idHD);

        // Add a flash attribute for success message
        redirectAttributes.addFlashAttribute("successMessage", "Đơn hàng đã chuyển sang trạng thái chuẩn bị hàng!");
        HoaDon hoaDon = hoaDonService.findById(idHD);
        HoaDonLog hoaDonLog = new HoaDonLog();
        hoaDonLog.setHoaDon(hoaDon);
        hoaDonLog.setHanhDong("Chuẩn bị hàng");
        hoaDonLog.setThoiGian(LocalDateTime.now());
        hoaDonLog.setNguoiThucHien(user.getUsername());

        hoaDonLog.setGhiChu("Đã xác nhận chuẩn bị hàng để gửi cho khách hàng");
        hoaDonLog.setTrangThai(0);
        hoaDonLogService.save(hoaDonLog);
        return "redirect:/admin/hoa-don/chi-tiet-hoa-don-online/" + idHD;
    }


    @GetMapping("/hoa-don-cho-giao-hang/{idHD}")
    public String hdChoGiaoHang(@PathVariable("idHD") Long idHD, RedirectAttributes redirectAttributes, Authentication authentication) {
        CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
        TaiKhoan user = customUserDetail.getUser();
        hoaDonService.hdChoGiaoHang(idHD);

        // Add a flash attribute for success message
        redirectAttributes.addFlashAttribute("successMessage", "Đơn hàng đã chuyển sang trạng thái chờ giao hàng!");
        HoaDon hoaDon = hoaDonService.findById(idHD);
        HoaDonLog hoaDonLog = new HoaDonLog();
        hoaDonLog.setHoaDon(hoaDon);
        hoaDonLog.setHanhDong("Chờ giao hàng");
        hoaDonLog.setThoiGian(LocalDateTime.now());
        hoaDonLog.setNguoiThucHien(user.getUsername());

        hoaDonLog.setGhiChu("đã thực hiện đóng gói đơn hàng và chờ đơn vị vận chuyển");
        hoaDonLog.setTrangThai(0);
        hoaDonLogService.save(hoaDonLog);
        return "redirect:/admin/hoa-don/chi-tiet-hoa-don-online/" + idHD;

    }

    @GetMapping("/xac-nhan-dang-giao-hang/{idHD}")
    public String xacNhanDangGiaoHang(@PathVariable("idHD") Long idHD, RedirectAttributes redirectAttributes, Authentication authentication) {
        CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
        TaiKhoan user = customUserDetail.getUser();
        hoaDonService.xacNhanDangGiaoHang(idHD);
        HoaDon hoaDon = hoaDonService.findById(idHD);
        HoaDonLog hoaDonLog = new HoaDonLog();
        hoaDonLog.setHoaDon(hoaDon);
        hoaDonLog.setHanhDong("Đang giao hàng");
        hoaDonLog.setThoiGian(LocalDateTime.now());
        hoaDonLog.setNguoiThucHien(user.getUsername());

        hoaDonLog.setGhiChu("đã giao đơn hàng cho đơn vị vận chuyển");
        hoaDonLog.setTrangThai(0);
        hoaDonLogService.save(hoaDonLog);
        // Add a flash attribute for success message
        redirectAttributes.addFlashAttribute("successMessage", "Đơn hàng đang giao!");

        return "redirect:/admin/hoa-don/chi-tiet-hoa-don-online/" + idHD;
    }

    @GetMapping("/hoa-don-da-giao-hang/{idHD}")
    public String hdDaGiaoHang(@PathVariable("idHD") Long idHD, RedirectAttributes redirectAttributes, Authentication authentication) {
        CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
        TaiKhoan user = customUserDetail.getUser();
        hoaDonService.hdDaGiaoHang(idHD);

        // Add a flash attribute for success message
        redirectAttributes.addFlashAttribute("successMessage", "Đơn hàng đã chuyển sang trạng thái đã giao hàng!");
        HoaDon hoaDon = hoaDonService.findById(idHD);
        HoaDonLog hoaDonLog = new HoaDonLog();
        hoaDonLog.setHoaDon(hoaDon);
        hoaDonLog.setHanhDong("Đã giao hàng");
        hoaDonLog.setThoiGian(LocalDateTime.now());
        hoaDonLog.setNguoiThucHien(user.getUsername());

        hoaDonLog.setGhiChu("Đơn hàng đã được giao thành công đến khách hàng");
        hoaDonLog.setTrangThai(0);
        hoaDonLogService.save(hoaDonLog);
        return "redirect:/admin/hoa-don/chi-tiet-hoa-don-online/" + idHD;
    }


    @GetMapping("/hoan-thanh-hoa-don/{idHD}")
    public String hoanThanhHD(@PathVariable("idHD") Long idHD, RedirectAttributes redirectAttributes, Authentication authentication) {
        CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
        TaiKhoan user = customUserDetail.getUser();
        hoaDonService.hoanThanhHoaDon(idHD);
        HoaDon hoaDon = hoaDonService.findById(idHD);
        HoaDonLog hoaDonLog = new HoaDonLog();
        hoaDonLog.setHoaDon(hoaDon);
        hoaDonLog.setHanhDong("Hoàn Thành đơn hàng");
        hoaDonLog.setThoiGian(LocalDateTime.now());
        hoaDonLog.setNguoiThucHien(user.getUsername());

        hoaDonLog.setGhiChu("đã giao đơn hàng cho khách và nhận được tiền hàng");
        hoaDonLog.setTrangThai(0);
        hoaDonLogService.save(hoaDonLog);
        // Add a flash attribute for success message
        redirectAttributes.addFlashAttribute("successMessage", "Đơn hàng đã hoàn thành!");
        return "redirect:/admin/hoa-don/chi-tiet-hoa-don-online/" + idHD;
    }


    @GetMapping("/huy-hoa-don-online/{idHD}")
    public String huyHDOnline(@PathVariable("idHD") Long idHD, RedirectAttributes redirectAttributes
            , Authentication authentication, @RequestParam("reason") String reason) {
        CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
        TaiKhoan user = customUserDetail.getUser();
        HoaDon hoaDon = hoaDonService.findById(idHD);
        HoaDonLog hoaDonLog = new HoaDonLog();
        hoaDonLog.setHoaDon(hoaDon);
        hoaDonLog.setHanhDong("Hủy đơn hàng");
        hoaDonLog.setThoiGian(LocalDateTime.now());
        hoaDonLog.setNguoiThucHien(user.getUsername());

        hoaDonLog.setGhiChu("Hủy đơn hàng (Lý do: " + reason + " )");
        hoaDonLog.setTrangThai(0);
        hoaDonLogService.save(hoaDonLog);
        hoaDonService.huyHoaDonOnline(idHD, reason);
        redirectAttributes.addFlashAttribute("successMessage", "Hủy hóa đơn thành công!");
        return "redirect:/admin/hoa-don/chi-tiet-hoa-don-online/" + idHD;
    }

    @GetMapping("/giao-hang-that-bai/{idHD}")
    public String giaoHangThatBai(@PathVariable("idHD") Long idHD,
                                  @RequestParam(value = "ctspLoiId", required = false) String idSPCTLoi,
                                  @RequestParam(value = "soLuongHoan", required = false) String soLuongHoan,
                                  @RequestParam(value = "lyDo", required = false) String lyDo,
                                  RedirectAttributes redirectAttributes, Model model,
                                  Authentication authentication
//                                  @RequestParam("lyDo") Integer lyDo
    ) {
        CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
        TaiKhoan user = customUserDetail.getUser();
        HoaDon hoaDon = hoaDonService.findById(idHD);
        HoaDonLog hoaDonLog = new HoaDonLog();
        hoaDonLog.setHoaDon(hoaDon);
        hoaDonLog.setHanhDong("Giao hàng thất bại");
        hoaDonLog.setThoiGian(LocalDateTime.now());
        hoaDonLog.setNguoiThucHien(user.getUsername());
//        if (lyDo == 1){
        hoaDonLog.setGhiChu("Giao hàng thất bại(Lý do: " + lyDo + " )");
        List<HoaDonChiTiet> listSanPham = hoaDonChiTietService.getListHdctByIdHd(idHD);
        for (HoaDonChiTiet hdct : listSanPham) {
            ChiTietSanPham chiTietSanPham = hdct.getChiTietSanPham();
            chiTietSanPham.setSoLuong(chiTietSanPham.getSoLuong() + hdct.getSoLuong());
            chiTietSanPham.setTrangThai(0);
            chiTietSanPhamService.save(chiTietSanPham);
        }
        hoaDon.setGhiChu(lyDo);
//        }else {
//            List<Long> listIdProductIdUsnelected = new ArrayList<>();
//            List<HoaDonChiTiet> listSanPham = hoaDonChiTietService.getListHdctByIdHd(idHD);
//            for (HoaDonChiTiet hdct : listSanPham) {
//                listIdProductIdUsnelected.add(hdct.getChiTietSanPham().getId());
//            }
//            List<Long> selectedProductIds = Arrays.stream(idSPCTLoi.split(","))
//                    .filter(id -> id != null && !id.trim().isEmpty())
//                    .map(Long::parseLong)
//                    .collect(Collectors.toList());
//            List<Integer> listSoLuongHoan = Arrays.stream(soLuongHoan.split(","))
//                    .filter(soLuong -> soLuong != null && !soLuong.trim().isEmpty())
//                    .map(Integer::parseInt)
//                    .collect(Collectors.toList());
//
//            System.out.println("list spct id chon: "+ selectedProductIds);
//            if (selectedProductIds.size() > 0){
//                listIdProductIdUsnelected.removeAll(selectedProductIds);
//                System.out.println("list spct id khong chon: "+ listIdProductIdUsnelected);
//                for (HoaDonChiTiet hdct : listSanPham) {
//                    for (int i = 0 ; i < selectedProductIds.size() ; i++){
//                        if (hdct.getChiTietSanPham().getId()==selectedProductIds.get(i)){
//                            System.out.println("id: "+selectedProductIds.get(i));
//                            System.out.println("So luong hoan: "+listSoLuongHoan.get(i));
//                            ChiTietSanPham ctsp = hdct.getChiTietSanPham();
//                            System.out.println("So luong cu: "+ctsp.getSoLuong());
//                            System.out.println("So luong moi: "+(ctsp.getSoLuong()+listSoLuongHoan.get(i)));
//                            ctsp.setSoLuong(ctsp.getSoLuong()+listSoLuongHoan.get(i));
//                            ctsp.setTrangThai(0);
//                            chiTietSanPhamService.save(ctsp);
//                        }
//                    }
//                    for (int i = 0 ; i < listIdProductIdUsnelected.size() ; i++){
//                        if (hdct.getChiTietSanPham().getId()==listIdProductIdUsnelected.get(i)){
//                            System.out.println("id: "+listIdProductIdUsnelected.get(i));
//                            System.out.println("So luong hoan: "+hdct.getSoLuong());
//                            ChiTietSanPham ctsp = hdct.getChiTietSanPham();
//                            System.out.println("So luong cu: "+ctsp.getSoLuong());
//                            System.out.println("So luong moi: "+(ctsp.getSoLuong()+hdct.getSoLuong()));
//                            ctsp.setSoLuong(ctsp.getSoLuong()+hdct.getSoLuong());
//                            ctsp.setTrangThai(0);
//                            chiTietSanPhamService.save(ctsp);
//                        }
//                    }
//                }
//            }else {
//                model.addAttribute("modalGiaoThatBai",true);
//                return "/admin/hoa-don/chi-tiet-hoa-don-online/"+idHD;
//            }
//            hoaDonLog.setGhiChu("Giao hàng Thất bại(Lý do: Sản phẩm có lỗi)");
//            hoaDon.setGhiChu("Sản phẩm có lỗi");
//        }
        hoaDonLog.setTrangThai(0);
        hoaDon.setTrangThai(-1);
        hoaDonLogService.save(hoaDonLog);
        hoaDonService.save(hoaDon);
        redirectAttributes.addFlashAttribute("successMessage", "Xác nhận thành công!");
        return "redirect:/admin/hoa-don/chi-tiet-hoa-don-online/" + idHD;
    }

    @GetMapping("/tim-kiem")
    public String timKiem(
            @RequestParam(required = false, value = "ma") String ma,
            @RequestParam(required = false, value = "keyword") String keyword,
//                          @RequestParam(required = false, value = "tennv") String tennv,
//                          @RequestParam(required = false, value = "tenkh") String tenkh,
            @RequestParam(required = false, value = "loaiDon") Integer loaiDon,
            @RequestParam(required = false, value = "trangThai") Integer trangThai,
            @RequestParam(required = false, value = "id") Long id,
            @RequestParam(value = "ngayThanhToan", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate ngayThanhToan,
            @RequestParam(defaultValue = "0") Integer page,

            Model model, Authentication authentication) {
        if (authentication != null) {
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            TaiKhoan user = customUserDetail.getUser();
            model.addAttribute("userLogged", user);
        }
        Pageable pageab = PageRequest.of(page, 5);
//        List<HoaDon> lsSearch = hr.search("", "", "", "", null, null, pageab);
        List<HoaDon> lsSearch = new ArrayList<>();
        if (keyword.trim().isEmpty()) {
            lsSearch = hr.search2(ma.trim(), trangThai, ngayThanhToan, loaiDon, pageab);
        } else {
            lsSearch = hr.search(ma.trim(), keyword.trim(), trangThai, ngayThanhToan, loaiDon, pageab);
        }
        model.addAttribute("listHDCT", hoaDonChiTietRepository.getHoaDonChiTietByHoaDonId(id));
        model.addAttribute("listSPOrder", hoaDonChiTietRepository.getHoaDonChiTietByHoaDonId(id));
        NumberUtils numberUtils = new NumberUtils();
        model.addAttribute("numberUtils", numberUtils);
        model.addAttribute("listHD", lsSearch);
        model.addAttribute("ma", ma);
        model.addAttribute("keyword", keyword.trim());
        model.addAttribute("id", id);
        model.addAttribute("ngayThanhToan", ngayThanhToan);
        model.addAttribute("loaiDon", loaiDon);
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
        if (keyword.trim().isEmpty()) {
            lsSearch = hr.search2(ma.trim(), trangThai, ngayThanhToan, 0, pageab);
        } else {
//            lsSearch = hr.search(ma.trim(), keyword.trim(), trangThai, ngayThanhToan, 0, pageab);
        }
        NumberUtils numberUtils = new NumberUtils();
        model.addAttribute("numberUtils", numberUtils);
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
