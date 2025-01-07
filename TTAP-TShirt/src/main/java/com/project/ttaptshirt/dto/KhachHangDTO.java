package com.project.ttaptshirt.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class KhachHangDTO {
    private Long id;
    private String hoTen;
    private String email;
    private String soDienThoai;
}
