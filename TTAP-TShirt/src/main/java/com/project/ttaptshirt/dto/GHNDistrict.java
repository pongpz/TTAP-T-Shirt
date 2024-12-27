package com.project.ttaptshirt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GHNDistrict {
    private Integer districtId;
    private String districtName;
    private String provinceName;
}
