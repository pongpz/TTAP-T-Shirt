package com.project.ttaptshirt.service.impl;

import com.project.ttaptshirt.dto.CartItemDTO;
import com.project.ttaptshirt.entity.ChiTietSanPham;
import com.project.ttaptshirt.entity.GioHang;
import com.project.ttaptshirt.entity.GioHangChiTiet;
import com.project.ttaptshirt.entity.User;
import com.project.ttaptshirt.repository.ChiTietSanPhamRepository;
import com.project.ttaptshirt.repository.GioHangChiTietRepository;
import com.project.ttaptshirt.repository.GioHangRepository;
import com.project.ttaptshirt.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
                    return gioHangRepository.save(newCart);
                });
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
        GioHang cart = getOrCreateCart(user);
        Optional<GioHangChiTiet> itemToRemove = cart.getItems().stream()
                .filter(item -> item.getId().equals(productId))
                .findFirst();
        if (itemToRemove.isEmpty()) {
            System.out.println("Sản phẩm không tìm thấy trong giỏ hàng");
        } else {
            itemToRemove.ifPresent(item -> {
                cart.getItems().remove(item);
                gioHangChiTietRepository.delete(item);
                updateTotalPrice(cart);
            });
        }
    }
}
