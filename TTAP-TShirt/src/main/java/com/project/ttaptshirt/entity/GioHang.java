package com.project.ttaptshirt.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "gio_hang")
public class GioHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "id_customer")
    private User user;

    @Column(name = "ghi_chu", columnDefinition = "NVARCHAR(255)")

    private String ghiChu;


    @Column(name = "phi_van_chuyen")
    private Double phiVanChuyen;

    @Column(name = "giam_gia")
    private Double giamGia;

    @Column(name = "phuong_thuc_thanh_toan", columnDefinition = "NVARCHAR(255)")

    private String phuongThucThanhToan;

    @Column(name = "tong_tien")
    private Double tongTien;

    @OneToMany(mappedBy = "gioHang",cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    private List<GioHangChiTiet> items;
    // Getters and Setters
}
