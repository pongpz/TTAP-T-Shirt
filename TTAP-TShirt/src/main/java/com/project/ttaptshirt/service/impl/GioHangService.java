package com.project.ttaptshirt.service.impl;

import com.project.ttaptshirt.dto.AddToCartRequest;
import com.project.ttaptshirt.dto.CartItemDTO;
import com.project.ttaptshirt.entity.*;
import com.project.ttaptshirt.repository.*;
import com.project.ttaptshirt.service.GHNService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GioHangService {
    @Autowired
    private GioHangRepository gioHangRepository;

    @Autowired
    private GioHangChiTietRepository gioHangChiTietRepository;

    @Autowired
    private ChiTietSanPhamRepository chiTietSanPhamRepository;

    @Autowired
    UserRepo userRepo;
    @Autowired
    private HoaDonRepository hoaDonRepository;
    @Autowired
    private HoaDonChiTietRepository hoaDonChiTietRepository;
    @Autowired
    private GHNService ghnService;
    @Autowired
    private MaGiamGiaServicelmpl giamGiaServicelmpl;
    @Autowired
    private KhachHangVoucherRepository khachHangVoucherRepository;

    public GioHang taoDon(String userName, List<CartItemDTO> cartItems ) {
        TaiKhoan user = userRepo.findByUsername(userName);

        GioHang gioHang = new GioHang();
        gioHang.setUser(user);

        Double tongTien = cartItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        gioHang.setTongTien(tongTien);
        gioHangRepository.save(gioHang);

        List<GioHangChiTiet> hangChiTiets = new ArrayList<>();
        for(CartItemDTO cartItem : cartItems) {
            ChiTietSanPham chiTietSanPham = chiTietSanPhamRepository.findById(cartItem.getIdItem())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
            GioHangChiTiet gioHangChiTiet = new GioHangChiTiet();
            gioHangChiTiet.setGioHang(gioHang);
            gioHangChiTiet.setChiTietSanPham(chiTietSanPham);
            gioHangChiTiet.setSoLuong(cartItem.getQuantity());
            gioHangChiTiet.setGia(cartItem.getPrice());
            hangChiTiets.add(gioHangChiTiet);
        }
        gioHangChiTietRepository.saveAll(hangChiTiets);
        gioHang.setItems(hangChiTiets);
        return gioHang;
    }

    public GioHang getOrCreateCart(TaiKhoan user) {
        return gioHangRepository.findByUserAndStatus(user, true)
                .orElseGet(() -> {
                    GioHang newCart = new GioHang();
                    newCart.setUser(user);
                    newCart.setTongTien(BigDecimal.ZERO.doubleValue());
                    newCart.setItems(new ArrayList<>());
                    return gioHangRepository.save(newCart);
                });
    }

    // Cập nhật số lượng của sản phẩm trong giỏ hàng
    public void updateProductQuantity(GioHang cart, Long productId, int newQuantity) {
        // Tìm sản phẩm trong giỏ hàng
        Optional<GioHangChiTiet> cartItemOpt = cart.getItems().stream()
                .filter(item -> item.getId().equals(productId))
                .findFirst();

        if (cartItemOpt.isPresent()) {
            GioHangChiTiet cartItem = cartItemOpt.get();
            cartItem.setSoLuong(newQuantity);
            gioHangRepository.save(cart); // Lưu lại giỏ hàng sau khi cập nhật số lượng
        } else {
            // Nếu sản phẩm không có trong giỏ hàng, có thể tạo mới hoặc thông báo lỗi
            throw new IllegalArgumentException("Sản phẩm không có trong giỏ hàng.");
        }
    }



    // Thêm sản phẩm vào giỏ
    public void addItemToCart(TaiKhoan user, Long productId, int quantity) {
        GioHang cart = getOrCreateCart(user);

        // Kiểm tra sản phẩm có trong giỏ chưa
        Optional<GioHangChiTiet> existingItem = cart.getItems().stream()
                .filter(item -> item.getChiTietSanPham().getId().equals(productId))
                .findFirst();

        ChiTietSanPham product = chiTietSanPhamRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

        if (existingItem.isPresent()) {
            GioHangChiTiet item = existingItem.get();
            item.setSoLuong(item.getSoLuong() + quantity);
            gioHangChiTietRepository.save(item);
        } else {
            GioHangChiTiet newItem = new GioHangChiTiet();
            newItem.setGioHang(cart);
            newItem.setChiTietSanPham(product);
            newItem.setSoLuong(quantity);
            newItem.setGia(product.getGiaBan());
            cart.getItems().add(newItem);
            gioHangChiTietRepository.save(newItem);
        }

        updateTotalPrice(cart);
    }

    public double calculateSubtotal(GioHang cart) {
        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            return 0.0;
        }

        double subtotal = cart.getItems().stream()
                .mapToDouble(item -> item.getGia() * item.getSoLuong())
                .sum();

        return subtotal;
    }

    // Cập nhật tổng giá trị giỏ hàng
    private void updateTotalPrice(GioHang cart) {
        BigDecimal total = cart.getItems() != null
                ? cart.getItems().stream()
                .map(item -> BigDecimal.valueOf(item.getGia() * item.getSoLuong()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                : BigDecimal.ZERO;

        cart.setTongTien(total.doubleValue());
    }

    public GioHang createOrderFromCart(TaiKhoan user, List<CartItemDTO> selectedItems) {
        if (selectedItems.isEmpty()) {
            throw new RuntimeException("Không có sản phẩm nào được chọn");
        }

        GioHang cart = getOrCreateCart(user);

        // Nếu giỏ hàng rỗng, ném lỗi
        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống, không thể tạo đơn hàng");
        }

        // Tạo đơn hàng mới từ giỏ hàng
        GioHang order = new GioHang();
        order.setUser(user);
        order.setTongTien(cart.getTongTien());
        gioHangRepository.save(order);

        // Lưu các chi tiết đơn hàng
        List<GioHangChiTiet> orderDetails = cart.getItems().stream().map(item -> {
            GioHangChiTiet orderDetail = new GioHangChiTiet();
            orderDetail.setGioHang(order);
            orderDetail.setChiTietSanPham(item.getChiTietSanPham());
            orderDetail.setSoLuong(item.getSoLuong());
            orderDetail.setGia(item.getGia());
            return orderDetail;
        }).collect(Collectors.toList());

        gioHangChiTietRepository.saveAll(orderDetails);

        return order;
    }

    public void clearCart(Long gioHangId) {
        // Xóa tất cả sản phẩm trong giỏ hàng
        gioHangChiTietRepository.deleteByGioHang_Id(gioHangId);
    }

    // Xóa sản phẩm khỏi giỏ hàng
    public void removeProductFromCart(TaiKhoan user, Long productId) {
        // Lấy giỏ hàng hiện tại
        GioHang cart = getOrCreateCart(user);

        // Tìm mục cần xóa trong giỏ hàng
        Optional<GioHangChiTiet> itemToRemove = cart.getItems().stream()
                .filter(item ->  item.getId().equals(productId))
                .findFirst();

        if (itemToRemove.isEmpty()) {
            System.out.println("Sản phẩm không tìm thấy trong giỏ hàng");
            return;
        }

        // Xóa sản phẩm nếu tìm thấy
        itemToRemove.ifPresent(item -> {
            cart.getItems().remove(item); // Xóa khỏi danh sách giỏ hàng
            gioHangChiTietRepository.delete(item); // Xóa khỏi cơ sở dữ liệu
            gioHangRepository.save(cart); // Đồng bộ giỏ hàng
            updateTotalPrice(cart); // Cập nhật tổng giá trị
        });
    }

    public void updateDiscount(GioHang cart, double discount) {
        if (cart == null) {
            throw new IllegalArgumentException("Cart không được null");
        }
        cart.setGiamGia(discount); // Cập nhật giá trị giảm giá
        gioHangRepository.save(cart); // Lưu giỏ hàng
    }

    @Transactional
    public HoaDon checkoutCart(TaiKhoan user, List<Long> selectedProductIds, String diaChi,
                               String nguoiNhan, String soDienThoai, Long discountId) {

        GioHang cart = getOrCreateCart(user);
        KhachHang khachHang = user.getKhachHang();
        if (khachHang == null) {
            throw new RuntimeException("Tài khoản không được liên kết với khách hàng.");
        }
        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống, không thể tạo hóa đơn.");
        }

        // Tạo hóa đơn mới
        HoaDon hoaDon = new HoaDon();
        hoaDon.setMa("HD" + (int) (Math.random() * 1000000));
        hoaDon.setKhachHang(khachHang);
        hoaDon.setNgayTao(LocalDateTime.now());
        hoaDon.setTenNguoiNhan(nguoiNhan);
        hoaDon.setSdtNguoiNhan(soDienThoai);
        hoaDon.setDiaChiGiaoHang(diaChi);
        hoaDon.setLoaiDon(0);
        hoaDon.setTrangThai(3);
        hoaDonRepository.save(hoaDon);

        // Tính tổng tiền sản phẩm
        double totalAmount = cart.getItems().stream()
                .filter(item -> selectedProductIds.contains(item.getChiTietSanPham().getId()))
                .mapToDouble(item -> item.getGia().doubleValue() * item.getSoLuong())
                .sum();

        double discountAmount = 0.0;
        if (discountId != null && discountId > 0) {
            MaGiamGia discountVoucher = giamGiaServicelmpl.findById(discountId);
            if (discountVoucher != null) {
                discountAmount = (discountVoucher.getGiaTriGiam() / 100) * totalAmount;
                if (discountAmount > totalAmount) {
                    discountAmount = totalAmount; // Giới hạn giảm giá không vượt quá tổng tiền
                }
                hoaDon.setSoTienGiamGia(discountAmount);
                KhachHangVoucher khachHangVoucher = khachHangVoucherRepository.findByKhachHangAndMaGiamGia(khachHang, discountVoucher);
                if (khachHangVoucher != null && khachHangVoucher.getSoLuong() > 0) {
                    khachHangVoucher.setSoLuong(khachHangVoucher.getSoLuong() - 1); // Giảm số lượng
                    khachHangVoucherRepository.save(khachHangVoucher); // Lưu lại thay đổi
                } else {
                    throw new RuntimeException("Mã giảm giá đã hết lượt sử dụng.");
                }
            } else {
                throw new RuntimeException("Mã giảm giá không hợp lệ.");
            }
        }

        // Tính phí vận chuyển
        double shippingFee = ghnService.calculateShippingFee(diaChi);
        hoaDon.setTienShip(shippingFee);

        // Tính tổng tiền cuối cùng
        double finalAmount = totalAmount + shippingFee - discountAmount;
        hoaDon.setTongTien(totalAmount); // Tổng tiền trước khi áp dụng giảm giá
        hoaDon.setTienThu(finalAmount); // Tổng tiền sau giảm giá và phí ship
        hoaDonRepository.save(hoaDon);

        // Xử lý sản phẩm đã chọn
        List<GioHangChiTiet> itemsToMove = cart.getItems().stream()
                .filter(item -> selectedProductIds.contains(item.getChiTietSanPham().getId()))
                .collect(Collectors.toList());

        if (itemsToMove.isEmpty()) {
            throw new RuntimeException("Không có sản phẩm nào được chọn để chuyển sang hóa đơn.");
        }

        List<HoaDonChiTiet> hoaDonChiTietList = itemsToMove.stream().map(gioHangChiTiet -> {
            HoaDonChiTiet hoaDonChiTiet = new HoaDonChiTiet();
            hoaDonChiTiet.setHoaDon(hoaDon);
            hoaDonChiTiet.setChiTietSanPham(gioHangChiTiet.getChiTietSanPham());
            hoaDonChiTiet.setSoLuong(gioHangChiTiet.getSoLuong());
            hoaDonChiTiet.setDonGia(gioHangChiTiet.getGia().floatValue());
            return hoaDonChiTiet;
        }).collect(Collectors.toList());
        hoaDonChiTietRepository.saveAll(hoaDonChiTietList);

        // Xóa sản phẩm khỏi giỏ hàng
        cart.getItems().removeAll(itemsToMove);
        gioHangChiTietRepository.deleteAll(itemsToMove);
        updateTotalPrice(cart);

        return hoaDon;
    }

    @Transactional
    public void addToCart(TaiKhoan user, AddToCartRequest request) {
        // Tìm sản phẩm chi tiết
        ChiTietSanPham chiTietSanPham = chiTietSanPhamRepository
                .findChiTietSanPham(request.getProductId(), request.getSize(), request.getColor())
                .orElseThrow(() -> new ProductNotFoundException("Không tìm thấy sản phẩm chi tiết phù hợp!"));

        // Kiểm tra tồn kho
        synchronized (this) {
            if (chiTietSanPham.getSoLuong() < request.getQuantity()) {
                throw new InsufficientStockException("Số lượng sản phẩm không đủ!");
            }
//            chiTietSanPham.setSoLuong(chiTietSanPham.getSoLuong() - request.getQuantity());
            chiTietSanPhamRepository.save(chiTietSanPham);
        }

        // Lấy giỏ hàng
        GioHang gioHang = getOrCreateCart(user);

        Optional<GioHangChiTiet> existingItem = gioHang.getItems().stream()
                .filter(item -> item.getChiTietSanPham().getSanPham().getId().equals(request.getProductId()) &&
                        item.getChiTietSanPham().getKichCo().getId().equals(request.getSize()) &&
                        item.getChiTietSanPham().getMauSac().getId().equals(request.getColor()))
                .findFirst();

        if (existingItem.isPresent()) {
            GioHangChiTiet item = existingItem.get();
            int newQuantity = item.getSoLuong() + request.getQuantity();
            // Kiểm tra nếu số lượng mới vượt quá tồn kho
            if (chiTietSanPham.getSoLuong() < newQuantity) {
                throw new InsufficientStockException("Số lượng sản phẩm không đủ để thêm vào giỏ hàng!");
            }
            item.setSoLuong(newQuantity);
            gioHangChiTietRepository.save(item);
        } else {
            GioHangChiTiet gioHangChiTiet = new GioHangChiTiet();
            gioHangChiTiet.setGioHang(gioHang);
            gioHangChiTiet.setChiTietSanPham(chiTietSanPham);
            gioHangChiTiet.setSoLuong(request.getQuantity());
            gioHangChiTiet.setGia(chiTietSanPham.getGiaBan());
            // Kiểm tra số lượng tồn kho trước khi thêm sản phẩm mới vào giỏ hàng
            if (chiTietSanPham.getSoLuong() < request.getQuantity()) {
                throw new InsufficientStockException("Số lượng sản phẩm không đủ để thêm vào giỏ hàng!");
            }

            gioHangChiTietRepository.save(gioHangChiTiet);
        }

        // Cập nhật tổng giá trị giỏ hàng
        updateTotalPrice(gioHang);
    }

 

    public class ProductNotFoundException extends RuntimeException {
        public ProductNotFoundException(String message) {
            super(message);
        }
    }

    public class InsufficientStockException extends RuntimeException {
        public InsufficientStockException(String message) {
            super(message);
        }
    }

    // Lưu giỏ hàng
    @Transactional
    public void save(GioHang cart) {
        gioHangRepository.save(cart);
    }

    // Xóa hết sản phẩm trong giỏ hàng
    @Transactional
    public void clearCart(GioHang cart) {
        cart.getItems().clear();
        gioHangRepository.save(cart); // Lưu lại giỏ hàng đã được xóa hết sản phẩm
    }

    @Transactional
    public void updateGiaInDatHangChiTiet(Long idChiTietSanPham, Double newGiaBan) {
        gioHangChiTietRepository.updateGiaInDatHangChiTiet(idChiTietSanPham, newGiaBan);
    }

}
