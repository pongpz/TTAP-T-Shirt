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
public class DiaChi extends CreatedUpdatedAt{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = " không được phép trống")
    @Column(name = "so_nha", columnDefinition = "NVARCHAR(255)")
    private String soNha;

    @NotBlank(message = " không được phép trống")
    @Column(name = "ten_duong", columnDefinition = "NVARCHAR(255)")
    private String tenDuong;

    @NotBlank(message = " không được phép trống")
    @Column(name = "ten_quan_huyen", columnDefinition = "NVARCHAR(255)")
    private String tenQuanhuyen;

    @NotBlank(message = " không được phép trống")
    @Column(name = "ten_thanh_pho", columnDefinition = "NVARCHAR(255)")
    private String tenThanhpho;


    @Column(name = "trang_thai")
    private String trangThai;


}
