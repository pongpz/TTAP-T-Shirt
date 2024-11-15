package com.project.ttaptshirt.service.impl;

import com.project.ttaptshirt.dto.CartDTO;
import com.project.ttaptshirt.dto.CartItemDTO;
import com.project.ttaptshirt.entity.ChiTietSanPham;
import com.project.ttaptshirt.entity.DatHang;
import com.project.ttaptshirt.entity.DatHangChiTiet;
import com.project.ttaptshirt.repository.ChiTietSanPhamRepository;
import com.project.ttaptshirt.repository.DatHangChiTietRepository;
import com.project.ttaptshirt.repository.DatHangRepository;
import com.project.ttaptshirt.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@SessionAttributes("cart")
public class CartService {
    private final ChiTietSanPhamRepository productRepository;
    private CartDTO cart = new CartDTO();

    @ModelAttribute("cart")
    public CartDTO createCart() {
        return new CartDTO();
    }

    public CartService(ChiTietSanPhamRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void addItem(CartDTO cart,Long productId, int quantity) {
        ChiTietSanPham product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        CartItemDTO item = new CartItemDTO();
        item.setIdItem(product.getId());
        item.setName(product.getSanPham().getTen());
        item.setPrice(product.getGiaBan());
        item.setQuantity(quantity);

        cart.getItems().add(item);
        updateTotalPrice();
    }

    public void updateItemQuantity(Long productId, int quantity) {
        cart.getItems().stream()
                .filter(item -> item.getIdItem().equals(productId))
                .forEach(item -> item.setQuantity(quantity));
        updateTotalPrice();
    }

    public void removeItem(Long productId) {
        cart.getItems().removeIf(item -> item.getIdItem().equals(productId));
        updateTotalPrice();
    }

    private void updateTotalPrice() {
        BigDecimal total = (BigDecimal) cart.getItems().stream()
                .map(item -> item.getPrice());
        cart.setTotalAmount(total);
    }

    public CartDTO getCart() {
        return cart;
    }


}
