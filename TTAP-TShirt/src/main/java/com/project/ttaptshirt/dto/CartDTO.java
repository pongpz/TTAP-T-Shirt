package com.project.ttaptshirt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CartDTO {
    private List<CartItemDTO> items;
    private String fullName;
    private String  phoneNumber;
    private String address;
    private BigDecimal totalAmount;
}
