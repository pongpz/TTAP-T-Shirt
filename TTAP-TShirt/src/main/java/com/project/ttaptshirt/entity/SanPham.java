package com.project.ttaptshirt.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "san_pham")
public class SanPham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma")
    private String ma;

    @Column(name = "ten")
    private String ten;


    @Column(name = "mo_ta")
    private String moTa;


    @Column(name = "trang_thai")
    private String trangThai;


    @Column(name = "ngay_tao")
    private Date ngayTao;


    @Column(name = "ngay_sua")
    private Date ngaySua;



    @ManyToOne
    @JoinColumn(name = "id_hinh_anh")
    private HinhAnh hinhAnh;
}
