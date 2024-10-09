package com.project.ttaptshirt.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Table(name = "nhan_vien")
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column( name = "ho_ten")
    @NotBlank(message = "Họ tên không được phép trống")
    @Pattern(regexp = "^[a-zA-ZÀ-ỹ\\s]+$",message = "Họ tên chỉ được chứa các ký tự chữ và dấu cách")
    private String hoTen;

    @Column(name = "ngay_sinh")
    @DateTimeFormat(pattern = "MM-dd-yyyy")
    private LocalDate ngaySinh;

    @Column(name = "gioi_tinh")
    private Boolean gioiTinh;

    @NotBlank(message = "SDT không được phép trống")
    @Pattern(regexp = "\\d+", message = "Phải là số")
    @Column(name = "so_dien_thoai")
    private String soDienthoai;

    @NotBlank(message = "Email không được phép trống")
    @Column(name = "email")
    private String email;

    @NotBlank(message = "Tk không được phép trống")
    @Column(name = "tai_khoan")
    private String taiKhoan;

    @NotBlank(message = "Mk không được phép trống")
    @Column(name = "mat_khau")
    private String matKhau;

    @NotBlank(message = "trang thai không được phép trống")
    @Column(name = "trang_thai")
    private String trangThai;

    @Column(name = "ngay_tao")
    private LocalDate ngayTao;

    @Column(name = "ngay_sua")
    private LocalDate ngaySua;

    @ManyToOne
    @JoinColumn(name = "id_chuc_vu")
    private ChucVu cv;

    @OneToOne
    @JoinColumn(name = "id_dia_chi")
    private DiaChi dc;
}
