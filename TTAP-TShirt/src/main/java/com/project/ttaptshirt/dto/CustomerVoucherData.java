package com.project.ttaptshirt.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CustomerVoucherData {
    private Long customerId;
    private Integer quantity;
}
