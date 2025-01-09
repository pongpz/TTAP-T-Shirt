package com.project.ttaptshirt.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class DiscountResponse {
    private double discountAmount;
    private String discountType;
}
