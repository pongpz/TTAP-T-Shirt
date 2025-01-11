package com.project.ttaptshirt.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate; 
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

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
        @Column(name = "ten", columnDefinition = "NVARCHAR(255)")

        private String ten;

        @NotNull
        @Column(name = "ngay_bat_dau")
        private LocalDateTime ngayBatDau;

        @NotNull
        @Column(name = "ngay_ket_thuc")
        private LocalDateTime ngayKetThuc;

        @NotNull
        @Column(name = "hinh_thuc")
        private Boolean hinhThuc;

        @NotNull
        @Column(name = "gia_tri_giam")
        private Double giaTriGiam;

        @NotNull
        @Column(name = "gia_tri_toi_thieu")
        private Double giaTriToiThieu;

        @NotNull
        @Column(name = "gia_tri_toi_da")
        private Double giaTriToiDa;

//        @NotNull
        @Column(name = "trang_thai")
        private Boolean trangThai;

        @Column(name = "ngay_tao")
        private LocalDateTime ngayTao;

        @Column(name = "ngay_sua")
        private LocalDateTime ngaySua;

        @NotBlank
        @Column(name = "ma")
        private String ma;

        @NotNull
        @Column(name = "so_luong")
        private Integer soLuong;

        // Method to check if the voucher is started
        public boolean isStart() {
                return LocalDateTime.now().isAfter(ngayBatDau);
        }

        // Method to check if the voucher is expired
        public boolean isExpired() {
                return LocalDateTime.now().isAfter(ngayKetThuc);
        }

        // Optional: Method to check if the voucher is valid (active and within the valid date range)
        public boolean isValid() {
                return isStart() && !isExpired() && trangThai != null && trangThai && soLuong>0;
        }
        public String getThoiGianHieuLuc() {
                if (ngayBatDau != null && ngayKetThuc != null) {
                        long days = ChronoUnit.DAYS.between(ngayBatDau, ngayKetThuc);
                        return days + " ngày";
                }
                return "Không xác định";
        }
}

