package com.project.ttaptshirt.service;

import com.project.ttaptshirt.dto.GHNDistrict;
import com.project.ttaptshirt.dto.GHNShippingDTO;
import com.project.ttaptshirt.dto.GHNShippingResponse;
import com.project.ttaptshirt.dto.GHNWard;
import com.project.ttaptshirt.entity.DiaChi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class GHNService {

    private static final String GHN_API_URL = "https://online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/fee";
    private static final String TOKEN = "25145a88-c2a2-11ef-b247-3a89ee222ada";
    private static final String SHOP_ID = "5349582";

    @Autowired
    private RestTemplate restTemplate;

    public Integer calculateShippingFee(DiaChi diaChi) {
        try {

            // Tạo headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("token", "25145a88-c2a2-11ef-b247-3a89ee222ada");

            Integer districtId = getDistrictId(diaChi.getTenQuanhuyen(), diaChi.getTenThanhpho());
            if (districtId == null) {
                log.error("Không tìm thấy district_id cho địa chỉ: {}", diaChi);
                return 0;
            }
            log.info("District ID: {}", districtId);

            // Lấy ward_code
            String wardCode = getWardCode(districtId, diaChi.getTenDuong());
            if (wardCode == null) {
                log.error("Không tìm thấy ward_code cho địa chỉ: {}", diaChi);
                return 0;
            }

            // Tạo request body
            GHNShippingDTO requestBody = new GHNShippingDTO();
            requestBody.setService_type_id(2);
            requestBody.setInsurance_value(500000);
            requestBody.setFrom_district_id(1493);
            requestBody.setTo_district_id(districtId);
            requestBody.setTo_ward_code(wardCode);
            requestBody.setHeight(15);
            requestBody.setLength(15);
            requestBody.setWeight(1000);
            requestBody.setWidth(15);


            HttpEntity<GHNShippingDTO> entity = new HttpEntity<>(requestBody, headers);

            // Gửi yêu cầu tính phí vận chuyển tới GHN API
            ResponseEntity<GHNShippingResponse> response = restTemplate.exchange(
                    GHN_API_URL,
                    HttpMethod.POST,
                    entity,
                    GHNShippingResponse.class
            );

            // Kiểm tra kết quả phản hồi và trả về phí vận chuyển
            if (response.getBody() != null && response.getBody().getData() != null) {
                return response.getBody().getData().getTotal();
            }

        } catch (Exception e) {
            log.error("Lỗi khi tính toán phí vận chuyển: ", e);
        }

        return 0;
    }

    public Integer calculateShippingFee(String diaChi) {
        try {
            // Tách thông tin từ chuỗi diaChi (giả sử định dạng: "Phường/Xã - Quận/Huyện - Thành phố")
            String[] parts = diaChi.split(" - ");
            if (parts.length < 3) {
                log.error("Địa chỉ không hợp lệ: {}", diaChi);
                return 0;
            }

            String tenDuong = parts[1].trim(); // Phường/Xã
            String tenQuanHuyen = parts[2].trim(); // Quận/Huyện
            String tenThanhPho = parts[3].trim(); // Thành phố

            // Tạo headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("token", "25145a88-c2a2-11ef-b247-3a89ee222ada");

            // Lấy district_id
            Integer districtId = getDistrictId(tenQuanHuyen, tenThanhPho);
            if (districtId == null) {
                log.error("Không tìm thấy district_id cho địa chỉ: {}", diaChi);
                return 0;
            }
            log.info("District ID: {}", districtId);

            // Lấy ward_code
            String wardCode = getWardCode(districtId, tenDuong);
            if (wardCode == null) {
                log.error("Không tìm thấy ward_code cho địa chỉ: {}", diaChi);
                return 0;
            }

            // Tạo request body
            GHNShippingDTO requestBody = new GHNShippingDTO();
            requestBody.setService_type_id(2);
            requestBody.setInsurance_value(500000);
            requestBody.setFrom_district_id(1493);
            requestBody.setTo_district_id(districtId);
            requestBody.setTo_ward_code(wardCode);
            requestBody.setHeight(15);
            requestBody.setLength(15);
            requestBody.setWeight(1000);
            requestBody.setWidth(15);

            HttpEntity<GHNShippingDTO> entity = new HttpEntity<>(requestBody, headers);

            // Gửi yêu cầu tính phí vận chuyển tới GHN API
            ResponseEntity<GHNShippingResponse> response = restTemplate.exchange(
                    GHN_API_URL,
                    HttpMethod.POST,
                    entity,
                    GHNShippingResponse.class
            );

            // Kiểm tra kết quả phản hồi và trả về phí vận chuyển
            if (response.getBody() != null && response.getBody().getData() != null) {
                return response.getBody().getData().getTotal();
            }
        } catch (Exception e) {
            log.error("Lỗi khi tính toán phí vận chuyển: ", e);
        }

        return 0;
    }


    private Integer getDistrictId(String districtName, String provinceName) {
        try {
            String url = "https://online-gateway.ghn.vn/shiip/public-api/master-data/district";
            HttpHeaders headers = new HttpHeaders();
            headers.set("token", TOKEN);

            // Tạo request body vì API này cần province_id
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("province_id", getProvinceId(provinceName));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // Sửa lại response type cho đúng với format API GHN
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            if (response.getBody() != null && response.getBody().get("data") != null) {
                List<Map<String, Object>> districts = (List<Map<String, Object>>) response.getBody().get("data");
                // Tìm district theo tên
                for (Map<String, Object> district : districts) {
                    if (district.get("DistrictName").toString().equalsIgnoreCase(districtName)) {
                        return Integer.parseInt(district.get("DistrictID").toString());
                    }
                }
            }

            return null;
        } catch (Exception e) {
            log.error("Error getting district ID: ", e);
            return null;
        }
    }

    // Thêm method lấy province_id
    private Integer getProvinceId(String provinceName) {
        try {
            String url = "https://online-gateway.ghn.vn/shiip/public-api/master-data/province";
            HttpHeaders headers = new HttpHeaders();
            headers.set("token", TOKEN);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            if (response.getBody() != null && response.getBody().get("data") != null) {
                List<Map<String, Object>> provinces = (List<Map<String, Object>>) response.getBody().get("data");
                for (Map<String, Object> province : provinces) {
                    if (province.get("ProvinceName").toString().equalsIgnoreCase(provinceName)) {
                        return Integer.parseInt(province.get("ProvinceID").toString());
                    }
                }
            }

            return null;
        } catch (Exception e) {
            log.error("Error getting province ID: ", e);
            return null;
        }
    }
    private String getWardCode(int districtId, String streetName) {
        try {
            String url = "https://online-gateway.ghn.vn/shiip/public-api/master-data/ward";
            HttpHeaders headers = new HttpHeaders();
            headers.set("token", TOKEN);

            // Tạo request body với districtId
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("district_id", districtId); // Đảm bảo rằng bạn đang sử dụng đúng key

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // Gửi yêu cầu
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST, // Thay đổi từ GET sang POST nếu API yêu cầu
                    entity,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            if (response.getBody() != null && response.getBody().get("data") != null) {
                List<Map<String, Object>> wards = (List<Map<String, Object>>) response.getBody().get("data");
                for (Map<String, Object> ward : wards) {
                    if (ward.get("WardName").toString().equalsIgnoreCase(streetName)) {
                        return ward.get("WardCode").toString();
                    }
                }
            }

            return null;
        } catch (Exception e) {
            log.error("Error getting ward code: ", e);
            return null;
        }
    }

}