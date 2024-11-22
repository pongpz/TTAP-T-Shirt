package com.project.ttaptshirt.service.impl;

import com.project.ttaptshirt.dto.CartDTO;
import com.project.ttaptshirt.dto.CartItemDTO;
import com.project.ttaptshirt.entity.ChiTietSanPham;
import com.project.ttaptshirt.entity.DatHang;
import com.project.ttaptshirt.entity.DatHangChiTiet;
import com.project.ttaptshirt.entity.User;
import com.project.ttaptshirt.repository.ChiTietSanPhamRepository;
import com.project.ttaptshirt.repository.DatHangChiTietRepository;
import com.project.ttaptshirt.repository.DatHangRepository;
import com.project.ttaptshirt.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DatHangService {
    @Autowired
    private DatHangRepository datHangRepository;

    @Autowired
    private DatHangChiTietRepository datHangChiTietRepository;

    @Autowired
    private ChiTietSanPhamRepository chiTietSanPhamRepository;

    @Autowired
    UserRepo userRepo;

    public DatHang taoDon(String userName, List<CartItemDTO> cartItems ) {
        User user = userRepo.findByUsername(userName);

        DatHang datHang = new DatHang();
        datHang.setUser(user);

        Double tongTien = cartItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        datHang.setTongTien(tongTien);
        datHangRepository.save(datHang);

        List<DatHangChiTiet> hangChiTiets = new ArrayList<>();
        for(CartItemDTO cartItem : cartItems) {
            ChiTietSanPham chiTietSanPham = chiTietSanPhamRepository.findById(cartItem.getIdItem())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
            DatHangChiTiet datHangChiTiet = new DatHangChiTiet();
            datHangChiTiet.setDatHang(datHang);
            datHangChiTiet.setChiTietSanPham(chiTietSanPham);
            datHangChiTiet.setSoLuong(cartItem.getQuantity());
            datHangChiTiet.setGia(cartItem.getPrice());
            hangChiTiets.add(datHangChiTiet);
        }
        datHangChiTietRepository.saveAll(hangChiTiets);
        datHang.setItems(hangChiTiets);
        return datHang;
    }

    public DatHang getOrCreateCart(User user) {
        return datHangRepository.findByUserAndStatus(user, true)
                .orElseGet(() -> {
                    DatHang newCart = new DatHang();
                    newCart.setUser(user);
                    newCart.setTongTien(BigDecimal.ZERO.doubleValue());
                    return datHangRepository.save(newCart);
                });
    }

    // Thêm sản phẩm vào giỏ
    public void addItemToCart(User user, Long productId, int quantity) {
        DatHang cart = getOrCreateCart(user);

        // Kiểm tra sản phẩm có trong giỏ chưa
        Optional<DatHangChiTiet> existingItem = cart.getItems().stream()
                .filter(item -> item.getChiTietSanPham().getId().equals(productId))
                .findFirst();

        ChiTietSanPham product = chiTietSanPhamRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

        if (existingItem.isPresent()) {
            DatHangChiTiet item = existingItem.get();
            item.setSoLuong(item.getSoLuong() + quantity);
            datHangChiTietRepository.save(item);
        } else {
            DatHangChiTiet newItem = new DatHangChiTiet();
            newItem.setDatHang(cart);
            newItem.setChiTietSanPham(product);
            newItem.setSoLuong(quantity);
            newItem.setGia(product.getGiaBan());
            cart.getItems().add(newItem);
            datHangChiTietRepository.save(newItem);
        }

        updateTotalPrice(cart);
    }

    // Cập nhật tổng giá trị giỏ hàng
    private void updateTotalPrice(DatHang cart) {
        BigDecimal total = cart.getItems() != null
                ? cart.getItems().stream()
                .map(item -> BigDecimal.valueOf(item.getGia() * item.getSoLuong()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                : BigDecimal.ZERO;

        cart.setTongTien(total.doubleValue());
    }

    public DatHang createOrderFromCart(User user, List<CartItemDTO> selectedItems) {
        if (selectedItems.isEmpty()) {
            throw new RuntimeException("Không có sản phẩm nào được chọn");
        }

        DatHang cart = getOrCreateCart(user);

        // Nếu giỏ hàng rỗng, ném lỗi
        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống, không thể tạo đơn hàng");
        }

        // Tạo đơn hàng mới từ giỏ hàng
        DatHang order = new DatHang();
        order.setUser(user);
        order.setTongTien(cart.getTongTien());
        datHangRepository.save(order);

        // Lưu các chi tiết đơn hàng
        List<DatHangChiTiet> orderDetails = cart.getItems().stream().map(item -> {
            DatHangChiTiet orderDetail = new DatHangChiTiet();
            orderDetail.setDatHang(order);
            orderDetail.setChiTietSanPham(item.getChiTietSanPham());
            orderDetail.setSoLuong(item.getSoLuong());
            orderDetail.setGia(item.getGia());
            return orderDetail;
        }).collect(Collectors.toList());

        datHangChiTietRepository.saveAll(orderDetails);

        return order;
    }

    // Xóa sản phẩm khỏi giỏ hàng
    public void removeProductFromCart(User user, Long productId) {
        DatHang cart = getOrCreateCart(user);

        Optional<DatHangChiTiet> itemToRemove = cart.getItems().stream()
                .filter(item -> item.getChiTietSanPham().getId().equals(productId))
                .findFirst();

        itemToRemove.ifPresent(item -> {
            cart.getItems().remove(item);
            datHangChiTietRepository.delete(item);
            updateTotalPrice(cart);
        });
    }
    
}
