package com.project.ttaptshirt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GHNShippingDTO {
    private int service_type_id;
    private int insurance_value;
    private String coupon;
    private int from_district_id;
    private int to_district_id;
    private String to_ward_code;
    private int height;
    private int length;
    private int weight;
    private int width;
}
