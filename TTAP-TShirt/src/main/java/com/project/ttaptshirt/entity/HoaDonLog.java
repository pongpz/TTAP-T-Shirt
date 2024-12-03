package com.project.ttaptshirt.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "hoa_don_log")
public class HoaDonLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_hoa_don", nullable = false)
    private HoaDon hoaDon;

    @Column(name = "hanh_dong", nullable = false,columnDefinition = "NVARCHAR(255)")
    private String hanhDong; // Action performed (e.g., "Đặt hàng", "Xác nhận đơn")

    @Column(name = "thoi_gian", nullable = false)
    private LocalDateTime thoiGian; // Timestamp of the action

    @Column(name = "nguoi_thuc_hien",columnDefinition = "NVARCHAR(255)")
    private String  nguoiThucHien; // User ID of the person who performed the action

    @Column(name = "ghi_chu", length = 1000)
    private String ghiChu; // Additional remarks or notes

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public HoaDon getHoaDon() {
        return hoaDon;
    }

    public void setHoaDon(HoaDon hoaDon) {
        this.hoaDon = hoaDon;
    }

    public String getHanhDong() {
        return hanhDong;
    }

    public void setHanhDong(String hanhDong) {
        this.hanhDong = hanhDong;
    }

    public LocalDateTime getThoiGian() {
        return thoiGian;
    }

    public void setThoiGian(LocalDateTime thoiGian) {
        this.thoiGian = thoiGian;
    }

    public String getNguoiThucHien() {
        return nguoiThucHien;
    }

    public void setNguoiThucHien(String nguoiThucHien) {
        this.nguoiThucHien = nguoiThucHien;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }
}
