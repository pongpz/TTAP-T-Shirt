package com.project.ttaptshirt.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "khuyen_mai")
public class KhuyenMai {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Column(name = "ten")
    private String ten;
    @NotNull
    @Column(name = "ngay_bat_dau")
    private Date ngayBatDau;
    @NotNull
    @Column(name = "ngay_ket_thuc")
    private Date ngayKetThuc;
    @NotNull
    @Column(name = "hinh_thuc")
    private Integer hinhThuc;
    @NotNull
    @Column(name = "gia_tri_giam")
    private Float giaTriGiam;
    @NotBlank
    @Column(name = "trang_thai")
    private String trangThai;
    @NotNull
    @Column(name = "ngay_tao")
    private Date ngayTao;
    @NotNull
    @Column(name = "ngay_sua")
    private Date ngaySua;

    // Getters and setters (omitted for brevity)
}
