package com.project.ttaptshirt.service.impl;

import com.project.ttaptshirt.dto.AddToCartRequest;
import com.project.ttaptshirt.dto.CartItemDTO;
import com.project.ttaptshirt.entity.*;
import com.project.ttaptshirt.repository.*;
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

    public GioHang taoDon(String userName, List<CartItemDTO> cartItems ) {
        User user = userRepo.findByUsername(userName);

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

    public GioHang getOrCreateCart(User user) {
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
    public void addItemToCart(User user, Long productId, int quantity) {
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

    // Cập nhật tổng giá trị giỏ hàng
    private void updateTotalPrice(GioHang cart) {
        BigDecimal total = cart.getItems() != null
                ? cart.getItems().stream()
                .map(item -> BigDecimal.valueOf(item.getGia() * item.getSoLuong()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                : BigDecimal.ZERO;

        cart.setTongTien(total.doubleValue());
    }

    public GioHang createOrderFromCart(User user, List<CartItemDTO> selectedItems) {
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

    // Xóa sản phẩm khỏi giỏ hàng
    public void removeProductFromCart(User user, Long productId) {
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

    @Transactional
    public HoaDon checkoutCart(User user, List<Long> selectedProductIds,String diaChi) {
        // Lấy giỏ hàng của người dùng
        GioHang cart = getOrCreateCart(user);

        // Kiểm tra giỏ hàng có sản phẩm không
        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống, không thể tạo hóa đơn.");
        }

        // Tạo hóa đơn mới
        HoaDon hoaDon = new HoaDon();
        hoaDon.setMa("HD" + (int) (Math.random() * 1000000));
        hoaDon.setKhachHang(user.getKhachHang());
        hoaDon.setNgayTao(LocalDateTime.now());
        hoaDon.setTenNguoiNhan(user.getHoTen());
        hoaDon.setSdtNguoiNhan(user.getSoDienthoai());
        hoaDon.setDiaChiGiaoHang(diaChi);
        hoaDon.setLoaiDon(0);
        hoaDon.setTrangThai(3); // Đặt trạng thái hóa đơn là chờ xử lý
        hoaDonRepository.save(hoaDon);

        // Duyệt qua danh sách sản phẩm được chọn trong giỏ hàng
        List<GioHangChiTiet> itemsToMove = cart.getItems().stream()
                .filter(item -> selectedProductIds.contains(item.getId())) // Chỉ chọn sản phẩm có ID được truyền
                .collect(Collectors.toList());

        if (itemsToMove.isEmpty()) {
            throw new RuntimeException("Không có sản phẩm nào được chọn để chuyển sang hóa đơn.");
        }

        // Tạo danh sách chi tiết hóa đơn
        List<HoaDonChiTiet> hoaDonChiTietList = new ArrayList<>();
        for (GioHangChiTiet gioHangChiTiet : itemsToMove) {
            HoaDonChiTiet hoaDonChiTiet = new HoaDonChiTiet();
            hoaDonChiTiet.setHoaDon(hoaDon);
            hoaDonChiTiet.setChiTietSanPham(gioHangChiTiet.getChiTietSanPham());
            hoaDonChiTiet.setSoLuong(gioHangChiTiet.getSoLuong());
            hoaDonChiTiet.setDonGia(gioHangChiTiet.getGia().floatValue());
            hoaDonChiTietList.add(hoaDonChiTiet);
        }
        hoaDonChiTietRepository.saveAll(hoaDonChiTietList);

        // Cập nhật tổng tiền hóa đơn
        double totalAmount = hoaDonChiTietList.stream()
                .mapToDouble(item -> item.getDonGia() * item.getSoLuong())
                .sum();
        hoaDon.setTongTien(totalAmount);
        hoaDonRepository.save(hoaDon);

        // Xóa các sản phẩm đã chọn khỏi giỏ hàng
        cart.getItems().removeAll(itemsToMove);
        gioHangChiTietRepository.deleteAll(itemsToMove);
        updateTotalPrice(cart);

        return hoaDon;
    }

    @Transactional
    public void addToCart(User user, AddToCartRequest request) {
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
            item.setSoLuong(item.getSoLuong() + request.getQuantity());
            gioHangChiTietRepository.save(item);
        } else {
            GioHangChiTiet gioHangChiTiet = new GioHangChiTiet();
            gioHangChiTiet.setGioHang(gioHang);
            gioHangChiTiet.setChiTietSanPham(chiTietSanPham);
            gioHangChiTiet.setSoLuong(request.getQuantity());
            gioHangChiTiet.setGia(chiTietSanPham.getGiaBan());
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


}
