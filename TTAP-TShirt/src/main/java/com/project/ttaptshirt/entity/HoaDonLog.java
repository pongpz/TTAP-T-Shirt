package com.project.ttaptshirt.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
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

    @Column(name = "hanh_dong",columnDefinition = "NVARCHAR(255)")
    private String hanhDong; // (e.g., "Đặt hàng", "Xác nhận đơn")

    @Column(name = "thoi_gian", nullable = false)
    private LocalDateTime thoiGian; // Timestamp of the action

    @Column(name = "nguoi_thuc_hien",columnDefinition = "NVARCHAR(255)")
    private String  nguoiThucHien; // User ID of the person who performed the action

    @Column(name = "ghi_chu",columnDefinition = "NVARCHAR(255)")
    private String ghiChu; // Additional remarks or notes

    @Column(name = "trang_thai")
    private Integer trangThai;

}
