package com.project.ttaptshirt.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ApiResponse {
    private boolean success;
    private String message;
}
