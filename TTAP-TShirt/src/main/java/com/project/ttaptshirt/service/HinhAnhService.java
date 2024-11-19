package com.project.ttaptshirt.service;

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
import com.project.ttaptshirt.repository.HinhAnhRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class HinhAnhService {

    @Autowired
    private HinhAnhRepository hinhAnhRepository;
//
//    public HinhAnh save(HinhAnh hinhAnh){
//       return repoImage.save(hinhAnh);
//    }
//
//    public List<HinhAnh> findAll(){
//        return repoImage.findAll();
//    }
//
//    public Optional<HinhAnh> findByid(Long id){
//        return repoImage.findById(id);
//    }
//
//    public void delete( Long id){
//        repoImage.deleteById(id);
//    }

    private  static final JsonFactory JSON_FACTORY  = GsonFactory.getDefaultInstance();
    private  static final String SERVICE_ACOUNT_KEY_PATH = getPathToGoodLeCredentials();

    private static String getPathToGoodLeCredentials(){
        String currenDirectory = System.getProperty("user.dir");
        Path filePath = Paths.get(currenDirectory,"cred.json");
        return filePath.toString();
    }

    public String uploadImageToDrive(File file) throws GeneralSecurityException, IOException {

        try{
            String folderId = "1qoyTft_RF3dI7TJQ9-IZcRFVveCJ7d0k";
            Drive drive = createDriveService();
            com.google.api.services.drive.model.File fileMetaData = new com.google.api.services.drive.model.File();
            fileMetaData.setName(file.getName());
            fileMetaData.setParents(Collections.singletonList(folderId));
            FileContent mediaContent = new FileContent("image/jpeg", file);
            com.google.api.services.drive.model.File uploadedFile = drive.files().create(fileMetaData, mediaContent)
                    .setFields("id").execute();
            String imageUrl = "https://drive.google.com/uc?export=view&id="+uploadedFile.getId();
            System.out.println("IMAGE URL: " + imageUrl);
            file.delete();
            return  imageUrl;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    private Drive createDriveService() throws GeneralSecurityException, IOException {

        GoogleCredential credential = GoogleCredential.fromStream(new FileInputStream(SERVICE_ACOUNT_KEY_PATH))
                .createScoped(Collections.singleton(DriveScopes.DRIVE));

        return new Drive.Builder(
                (HttpTransport) GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                (HttpRequestInitializer) credential)
                .build();

    }

    public String getFirstImageLinkBySanPhamId(Long sanPhamId) {
        List<HinhAnh> hinhAnhs = hinhAnhRepository.findBySanPhamId(sanPhamId);
        if (!hinhAnhs.isEmpty()) {
            return hinhAnhs.get(0).getPath(); // Trả về đường dẫn của hình ảnh đầu tiên
        }
        return "/assets/no-image.jpg"; // Trường hợp không có hình ảnh
    }



}
