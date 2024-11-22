package com.project.ttaptshirt.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @Column(name = "ma")
    private String ma;

    @Column(name = "ngay_tao")
    private LocalDate ngayTao;

    @Column(name = "ngay_thanh_toan")
    private LocalDate ngayThanhToan;

    @Column(name = "ghi_chu")
    private String ghiChu;

    @Column(name = "tong_tien")
    private Float tongTien;

    @ManyToOne
    @JoinColumn(name = "id_ma_giam_gia")
    private MaGiamGia maGiamGia;

    @ManyToOne
    @JoinColumn(name = "id_khach_hang")
    private KhachHang khachHang;

    @ManyToOne
    @JoinColumn(name = "id_nhan_vien")
    private User nhanVien;

    @Column(name = "dia_chi_giao_hang")
    private String diaChiGiaoHang;

    @Column(name = "sdt_nguoi_nhan")
    private String sdtNguoiNhan;

    @Column(name = "ngay_ship")
    private LocalDate ngayShip;

    @Column(name = "tien_ship")
    private float tienShip;

    @Column(name = "tien_coc")
    private float tienCoc;

    @Column(name = "ngay_nhan")
    private LocalDate ngayNhan;

    @Column(name = "tien_thu")
    private float tienThu;

    @Column(name = "so_tien_giam_gia")
    private float soTienGiamGia;

    @Column(name = "trang_thai")
    private Integer trangThai;

    @Column(name = "ten_nguoi_nhan")
    private String tenNguoiNhan;


    @Column(name = "loai_don")
    private Integer loaiDon;

    @PreUpdate
    protected  void onUpdate(){
        ngayThanhToan = LocalDate.now();
    }
    // Getters and setters (omitted for brevity)
}
