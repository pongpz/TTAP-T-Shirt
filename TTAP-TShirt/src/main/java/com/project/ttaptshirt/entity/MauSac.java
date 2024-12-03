package com.project.ttaptshirt.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Random;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "mau_sac")
public class MauSac {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma")
    private String ma;

    @Column(name = "ten", columnDefinition = "NVARCHAR(255)")
    private String ten;

    @Column(name = "trang_thai")
    private int trangThai;

    @OneToMany(mappedBy = "mauSac", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChiTietSanPham> chiTietSanPhamList;

    @PrePersist
    private void prePersist() {
        if (this.ma == null || this.ma.isEmpty()) {
            this.ma = "MS" + generateRandomCode(3);
        }
    }

    private String generateRandomCode(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            code.append(characters.charAt(random.nextInt(characters.length())));
        }
        return code.toString();
    }

    // Getters and Setters
}
