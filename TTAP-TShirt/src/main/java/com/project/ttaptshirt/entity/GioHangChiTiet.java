package com.project.ttaptshirt.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "gio_hang_chi_tiet")
public class GioHangChiTiet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_san_pham_chi_tiet")
    private ChiTietSanPham chiTietSanPham;

    @Column(name = "gia")
    private Double gia;

    @ManyToOne
    @JoinColumn(name = "id_dat_hang")
    private GioHang gioHang;

    @Column(name = "so_luong")
    private Integer soLuong;

    @Column(name = "tong_tien")
    private Double tongTien;

    // Getters and Setters
}
