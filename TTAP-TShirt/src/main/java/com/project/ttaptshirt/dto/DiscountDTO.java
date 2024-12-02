package com.project.ttaptshirt.dto;

import com.project.ttaptshirt.entity.MaGiamGia;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class DiscountDTO {
    private MaGiamGia discount;
    private String message;
}
