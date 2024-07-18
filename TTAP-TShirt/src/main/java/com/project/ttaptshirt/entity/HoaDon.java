package com.project.ttaptshirt.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.sql.Date;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "hoa_don")
public class HoaDon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_users")
    private Users khachHang;


    @Column(name = "ma")
    private String ma;

    @Column(name = "ngay_tao")
    private Date ngayTao;

    @Column(name = "ngay_thanh_toan")
    private Date ngayThanhToan;

    @Column(name = "ghi_chu")
    private String ghiChu;

    @Column(name = "tong_tien")
    private Float tongTien;

    @Column(name = "loai_don")
    private Integer loaiDon;

    @Column(name = "trang_thai")
    private Integer trangThai;
    // Getters and setters (omitted for brevity)
}
