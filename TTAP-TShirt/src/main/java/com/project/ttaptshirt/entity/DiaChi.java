package com.project.ttaptshirt.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Table(name = "dia_chi")
@Entity
public class DiaChi{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = " không được phép trống")
    @Column(name = "so_nha")
    private String soNha;

    @NotBlank(message = " không được phép trống")
    @Column(name = "ten_duong")
    private String tenDuong;

    @NotBlank(message = " không được phép trống")
    @Column(name = "ten_quan_huyen")
    private String tenQuanhuyen;

    @NotBlank(message = " không được phép trống")
    @Column(name = "ten_thanh_pho")
    private String tenThanhpho;


    @Column(name = "trang_thai")
    private String trangThai;

    @Column(name = "ngay_tao")
    private LocalDate ngayTao;

    @Column(name = "ngay_sua")
    private LocalDate ngaySua;

}
