package com.project.ttaptshirt.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "khuyen_mai_hoa_don")
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(name = "ten")
    private String ten;

    @Column(name = "ngay_bat_dau")
    private Date ngayBatDau;

    @Column(name = "ngay_ket_thuc")
    private Date ngayKetThuc;

    @Column(name = "hinh_thuc")
    private String hinhThuc;

    @Column(name = "gia_tri_giam")
    private Float giaTriGiam;

    @Column(name = "trang_thai")
    private String trangThai;

    @Column(name = "ngay_tao")
    private Date ngayTao;

    @Column(name = "ngay_sua")
    private Date ngaySua;

    @Column(name = "dieu_kien")
    private Float dieuKien;

//    @Column(name = "dieu_kien_loai")
//    private String dieuKienLoai;

    @Column(name = "ma")
    private String ma;

    @Column(name = "so_luong")
    private Integer soLuong;

}
