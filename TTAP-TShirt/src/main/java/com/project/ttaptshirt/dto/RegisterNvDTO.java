package com.project.ttaptshirt.dto;

import com.project.ttaptshirt.entity.NhanVien;
import com.project.ttaptshirt.entity.TaiKhoan;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class RegisterNvDTO {
    private TaiKhoan user;
    private NhanVien nv;
    private String password;
}
