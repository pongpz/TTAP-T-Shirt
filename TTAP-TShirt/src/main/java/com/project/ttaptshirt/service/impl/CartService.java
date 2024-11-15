package com.project.ttaptshirt.service.impl;

import com.project.ttaptshirt.dto.CartDTO;
import com.project.ttaptshirt.dto.CartItemDTO;
import com.project.ttaptshirt.entity.ChiTietSanPham;
import com.project.ttaptshirt.repository.ChiTietSanPhamRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Service
@SessionAttributes("cart")
public class CartService {
    private final ChiTietSanPhamRepository productRepository;
    private CartDTO cart = new CartDTO();
    List<CartItemDTO> items = new ArrayList<>();

    public CartService(ChiTietSanPhamRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<CartItemDTO> getCartItems() {
        return items;
    }

    @ModelAttribute("cart")
    public CartDTO createCart() {
        return new CartDTO();
    }

    public void addItem(CartDTO cart, Long productId, int quantity) {
        // Tìm sản phẩm trong kho
        ChiTietSanPham product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Kiểm tra nếu sản phẩm đã tồn tại trong giỏ hàng
        if (cart.getItems() == null) {
            cart.setItems(new ArrayList<>());
        }

        CartItemDTO existingItem = cart.getItems().stream()
                .filter(item -> item.getIdItem().equals(productId))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            // Nếu sản phẩm đã tồn tại, tăng số lượng
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            // Nếu sản phẩm chưa tồn tại, thêm mới
            CartItemDTO newItem = new CartItemDTO();
            newItem.setIdItem(product.getId());
            newItem.setName(product.getSanPham().getTen());
            newItem.setPrice(product.getGiaBan());
            newItem.setQuantity(quantity);

            cart.getItems().add(newItem);
        }

        // Debug log
        System.out.println("Giỏ hàng sau khi thêm: " + cart.getItems());

        // Cập nhật tổng giá trị giỏ hàng
        updateTotalPrice(cart);
    }

    public void updateItemQuantity(CartDTO cart, Long productId, int quantity) {
        if (cart.getItems() != null) {
            cart.getItems().stream()
                    .filter(item -> item.getIdItem().equals(productId))
                    .forEach(item -> item.setQuantity(quantity));
            updateTotalPrice(cart); // Cập nhật giá sau khi thay đổi số lượng
        }
    }

    public void removeItem(CartDTO cart, Long productId) {
        if (cart.getItems() != null) {
            cart.getItems().removeIf(item -> item.getIdItem().equals(productId));
            updateTotalPrice(cart); // Cập nhật giá sau khi xóa sản phẩm
        }
    }

    private void updateTotalPrice(CartDTO cart) {
        BigDecimal total = cart.getItems() != null
                ? cart.getItems().stream()
                .map(item -> BigDecimal.valueOf(item.getPrice() * item.getQuantity()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                : BigDecimal.ZERO;

        cart.setTotalAmount(BigDecimal.valueOf(total.doubleValue()));
    }

    public CartDTO getCart() {
        return cart;
    }


}
