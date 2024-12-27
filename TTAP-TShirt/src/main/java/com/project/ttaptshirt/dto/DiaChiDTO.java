package com.project.ttaptshirt.dto;

import com.project.ttaptshirt.entity.DiaChi;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiaChiDTO {
    private Long id;
    private String hoTen;
    private String soDienThoai;
    private String soNha;
    private String tenDuong;
    private String tenQuanhuyen;
    private String tenThanhpho;


    public DiaChiDTO(DiaChi diaChi) {
        this.id = diaChi.getId();
        this.hoTen = diaChi.getHoTen();
        this.soDienThoai = diaChi.getSoDienThoai();
        this.soNha = diaChi.getSoNha();
        this.tenDuong = diaChi.getTenDuong();
        this.tenQuanhuyen = diaChi.getTenQuanhuyen();
        this.tenThanhpho = diaChi.getTenThanhpho();
    }

}
