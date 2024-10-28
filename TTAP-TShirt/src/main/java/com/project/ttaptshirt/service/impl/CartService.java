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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {
    @Autowired
    private DatHangRepository repoDathang;

    @Autowired
    private ChiTietSanPhamRepository repoCtsp;

    @Autowired
    private DatHangChiTietRepository datHangChiTietRepo;

    @Autowired
    private UserRepo repoUser;

    public DatHang getOrCreateDatHang(Long userId) {
        Optional<DatHang> optionalDatHang = repoDathang.findByUserId(userId);

        if (optionalDatHang.isPresent()) {
            return optionalDatHang.get();
        } else {
            DatHang newDatHang = new DatHang();
            newDatHang.setUser(repoUser.findById(userId).orElse(null));
            return repoDathang.save(newDatHang);
        }
    }

    public void addSanPham(Long idCtsp, int soLuong, double tongTien, Long userId) {
        DatHang datHang = getOrCreateDatHang(userId);
        Optional<ChiTietSanPham> optionalCtsp = repoCtsp.findById(idCtsp);

        if (optionalCtsp.isPresent()) {
            ChiTietSanPham ctsp = optionalCtsp.get();
            Optional<DatHangChiTiet> optionalItem = datHang.getItems().stream()
                    .filter(item -> item.getChiTietSanPham().getId().equals(idCtsp))
                    .findFirst();

            DatHangChiTiet newItem;
            if (optionalItem.isPresent()) {
                newItem = optionalItem.get();
                newItem.setSoLuong(newItem.getSoLuong() + soLuong);  // Cập nhật số lượng
                newItem.setTongTien(newItem.getTongTien() + tongTien);  // Cập nhật tổng tiền
            } else {
                newItem = new DatHangChiTiet();
                newItem.setChiTietSanPham(ctsp);
                newItem.setDatHang(datHang);
                newItem.setSoLuong(soLuong);
                newItem.setTongTien(tongTien);
            }

            datHangChiTietRepo.save(newItem);
            datHang.getItems().add(newItem); // Cập nhật danh sách mặt hàng của đơn hàng
            datHang.setTongTien(datHang.getItems().stream().mapToDouble(DatHangChiTiet :: getTongTien).sum()); // Cập nhật tổng tiền của đơn hàng
            repoDathang.save(datHang); // Lưu cập nhật cho đơn hàng
        }
    }


    public CartDTO getCart(Long userId) {
        DatHang datHang = getOrCreateDatHang(userId);
        List<DatHangChiTiet> items = datHangChiTietRepo.findByDatHang(datHang);

        List<CartItemDTO> cartItems = items.stream().map(item -> {
            CartItemDTO cartItem = new CartItemDTO();
            cartItem.setId(item.getChiTietSanPham().getId());
            cartItem.setName(item.getChiTietSanPham().getSanPham().getTen());
            cartItem.setPrice(item.getChiTietSanPham().getGiaBan().doubleValue());
            cartItem.setQuantity(item.getSoLuong());
            cartItem.setTotalPrice(item.getTongTien());
            return cartItem;
        }).collect(Collectors.toList());

        CartDTO cart = new CartDTO();
        cart.setItems(cartItems);
        cart.setTotalAmount(cartItems.stream().mapToDouble(CartItemDTO::getTotalPrice).sum());

        return cart;
    }

    public void updateProductQuantity(Long userId, Long idCtsp, int soLuong) {
        DatHang datHang = getOrCreateDatHang(userId);
        DatHangChiTiet item = datHang.getItems().stream()
                .filter(i -> i.getChiTietSanPham().getId().equals(idCtsp))
                .findFirst().orElse(null);
        if (item != null) {
            item.setSoLuong(soLuong);
            item.setTongTien(item.getChiTietSanPham().getGiaBan() * soLuong);
            datHangChiTietRepo.save(item);
            repoDathang.save(datHang);
        }
    }

    public void removeProductFromCart(Long userId, Long idCtsp) {
        DatHang datHang = getOrCreateDatHang(userId);
        DatHangChiTiet item = datHang.getItems().stream()
                .filter(i -> i.getChiTietSanPham().getId().equals(idCtsp))
                .findFirst().orElse(null);
        if (item != null) {
            datHang.getItems().remove(item);
            datHangChiTietRepo.delete(item);
            repoDathang.save(datHang);
        }
    }


}
