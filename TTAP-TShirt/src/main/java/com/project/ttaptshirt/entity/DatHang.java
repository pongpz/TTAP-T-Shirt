package com.project.ttaptshirt.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "dat_hang")
public class DatHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "id_customer")
    private User user;

    @Column(name = "ghi_chu")
    private String ghiChu;

    @Column(name = "phuong_thuc_thanh_toan")
    private String phuongThucThanhToan;

    @Column(name = "tong_tien")
    private Double tongTien;

    @OneToMany(mappedBy = "datHang")
    private Set<DatHangChiTiet> items;
    // Getters and Setters
}
