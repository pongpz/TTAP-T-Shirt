package com.project.ttaptshirt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GHNShippingResponse {
    private Integer code;
    private String message;
    private Data data;

    @lombok.Data
    public static class Data {
        private Integer total;
        private Integer service_fee;
        private Integer insurance_fee;
        private Integer pick_station_fee;
        private Integer coupon_value;
    }
}
