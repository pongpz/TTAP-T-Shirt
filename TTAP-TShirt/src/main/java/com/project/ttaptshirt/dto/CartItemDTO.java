package com.project.ttaptshirt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CartItemDTO {
    private Long idItem;
    private String name;
    private String imagePath;
    private Double price;
    private int quantity;

}
