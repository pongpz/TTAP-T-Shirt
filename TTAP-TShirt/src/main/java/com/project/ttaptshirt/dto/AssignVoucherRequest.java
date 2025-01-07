package com.project.ttaptshirt.dto;

import lombok.*;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class AssignVoucherRequest {
    private Long voucherId;
    private List<CustomerVoucherData> customerData;
}
