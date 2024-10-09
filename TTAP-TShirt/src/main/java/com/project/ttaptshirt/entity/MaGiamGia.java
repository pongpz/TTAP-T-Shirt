package com.project.ttaptshirt.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "ma_giam_gia")
public class MaGiamGia {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
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

    @NotBlank
    @Column(name = "hinh_thuc")
    private Integer hinhThuc;

    @NotNull
    @Column(name = "gia_tri_giam")
    private Float giaTriGiam;

    @NotNull
    @Column(name = "gia_tri_toi_thieu")
    private Float giaTriGiamToiThieu;

    @NotNull
    @Column(name = "gia_tri_toi_da")
    private Float giaTriToiDa;

    @NotBlank
    @Column(name = "trang_thai")
    private String trangThai;

    @NotBlank
    @Column(name = "ngay_tao")
    private Date ngayTao;

    @NotBlank
    @Column(name = "ngay_sua")
    private Date ngaySua;

    @NotBlank
    @Column(name = "ma")
    private String ma;

    @NotNull
    @Column(name = "so_luong")
    private Integer soLuong;

}