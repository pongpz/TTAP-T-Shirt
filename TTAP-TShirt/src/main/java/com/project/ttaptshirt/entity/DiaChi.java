package com.project.ttaptshirt.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Table(name = "dia_chi")
@Entity
public class DiaChi extends CreatedUpdatedAt{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = " không được phép trống")
    @Column(name = "ho_ten", columnDefinition = "NVARCHAR(255)")
    private String hoTen;

    @NotBlank(message = " không được phép trống")
    @Column(name = "so_dien_thoai")
    private String soDienThoai;

    @NotBlank(message = " không được phép trống")
    @Column(name = "so_nha", columnDefinition = "NVARCHAR(255)")
    private String soNha;

    @NotBlank(message = " không được phép trống")
    @Column(name = "ten_phuong_xa", columnDefinition = "NVARCHAR(255)")
    private String tenDuong;

    @NotBlank(message = " không được phép trống")
    @Column(name = "ten_quan_huyen", columnDefinition = "NVARCHAR(255)")
    private String tenQuanhuyen;

    @NotBlank(message = " không được phép trống")
    @Column(name = "ten_thanh_pho", columnDefinition = "NVARCHAR(255)")
    private String tenThanhpho;


    @Column(name = "trang_thai")
    private String trangThai;

    @ManyToOne
    @JoinColumn(name = "id_tai_khoan", nullable = true)
    private TaiKhoan taiKhoan;
}
