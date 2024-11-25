package com.project.ttaptshirt.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class AddToCartRequest {
    private Long productId; // ID của sản phẩm
    private Long size;    // Kích thước
    private Long color;   // Màu sắc
    private int quantity;
}
