package com.project.ttaptshirt.service;


import com.cloudinary.Cloudinary;
import com.project.ttaptshirt.dto.HinhAnhDTO;
import com.project.ttaptshirt.exception.FuncErrorException;
import com.project.ttaptshirt.repository.HinhAnhRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HinhAnhService {


    @Autowired
    private HinhAnhRepository hinhAnhRepository;

    @Autowired
    private Cloudinary cloudinary;

    public String uploadFile(MultipartFile file) throws IOException {
        Map<String, Object> uploadParams = new HashMap<>();
        uploadParams.put("folder", "test"); // Specify your folder here
        uploadParams.put("public_id", UUID.randomUUID().toString());

        return cloudinary.uploader().upload(file.getBytes(), uploadParams)
                .get("url").toString();
    }


}
