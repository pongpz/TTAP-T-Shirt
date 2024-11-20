package com.project.ttaptshirt.service;

<<<<<<< HEAD


import com.cloudinary.Cloudinary;
import com.project.ttaptshirt.dto.HinhAnhDTO;
import com.project.ttaptshirt.exception.FuncErrorException;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.auth.oauth2.GoogleCredentials;
import com.project.ttaptshirt.entity.HinhAnh;

=======

import com.cloudinary.Cloudinary;
import com.project.ttaptshirt.entity.HinhAnh;
>>>>>>> c9a6ef1936c4dfc37880867d73b0ad45e479c571
import com.project.ttaptshirt.repository.HinhAnhRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
<<<<<<< HEAD

import java.io.File;
=======
>>>>>>> c9a6ef1936c4dfc37880867d73b0ad45e479c571
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
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

<<<<<<< HEAD

    public String uploadImageToDrive(File file) throws GeneralSecurityException, IOException {
=======
>>>>>>> c9a6ef1936c4dfc37880867d73b0ad45e479c571


}
