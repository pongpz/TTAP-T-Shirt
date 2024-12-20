package com.project.ttaptshirt.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "nhan_vien")
public class NhanVien {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ho_ten", columnDefinition = "NVARCHAR(255)")
    @NotBlank(message = "Họ tên không được phép trống")
    @Pattern(regexp = "^[a-zA-ZÀ-ỹ\\s]+$", message = "Họ tên chỉ được chứa các ký tự chữ và dấu cách")
    private String hoTen;

    @NotBlank(message = "SDT không được phép trống")
    @Pattern(regexp = "\\d+", message = "Phải là số")
    @Column(name = "so_dien_thoai")
    private String soDienthoai;

    @Column(name = "email")
    private String email;

    @Column(name = "ngay_sinh")
    @DateTimeFormat(pattern = "MM-dd-yyyy")
    private LocalDate ngaySinh;

    @Column(name = "gioi_tinh")
    private Boolean gioiTinh;

    @Column(name = "ngay_tao")
    private LocalDate ngayTao;

    @OneToOne
    @JoinColumn(name = "id_tai_khoan")
    @JsonIgnore
    private TaiKhoan taiKhoan;

    @Override
    public String toString() {
        // Avoid recursive call by not including `user`
        return "KhachHang{" +
                "id=" + id +
                ", hoTen='" + hoTen + '\'' +
                ", soDienThoai='" + soDienthoai + '\'' +
                '}';
    }

}
