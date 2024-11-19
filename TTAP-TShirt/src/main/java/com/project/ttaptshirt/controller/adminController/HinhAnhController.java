
package com.project.ttaptshirt.controller.adminController;

import com.project.ttaptshirt.service.HinhAnhService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;


@Controller
@RequestMapping("/admin/hinh-anh")
public class HinhAnhController {


    @Autowired
    private HinhAnhService hinhAnhService;

    @PostMapping("/uploadToGoogleDrive")
    public Object handleFileUpload(@RequestParam("image") MultipartFile file) throws IOException, GeneralSecurityException {
        if (file.isEmpty()) {
            return "FIle is empty";
        }
        File tempFile = File.createTempFile("temp", null);
        file.transferTo(tempFile);
        String res = hinhAnhService.uploadImageToDrive(tempFile);
        System.out.println(res);
        return res;
    }

//    @Autowired
//    private HinhAnhService hinhAnhService;
//
//    @PostMapping("/uploadToGoogleDrive")
//    public Object handleFileUpload(@RequestParam("image") MultipartFile file) throws IOException, GeneralSecurityException {
//        if (file.isEmpty()) {
//            return "FIle is empty";
//        }
//        File tempFile = File.createTempFile("temp", null);
//        file.transferTo(tempFile);
//        HinhAnh res = hinhAnhService.uploadImageToDrive(tempFile);
//        System.out.println(res);
//        return res;
//    }

//}
//    @Autowired
//    private HinhAnhService hinhAnhService;
//
//    @PostMapping("/uploadToGoogleDrive")
//    public Object handleFileUpload(@RequestParam("image") MultipartFile file) throws IOException, GeneralSecurityException {
//        if (file.isEmpty()) {
//            return "FIle is empty";
//        }
//        File tempFile = File.createTempFile("temp", null);
//        file.transferTo(tempFile);
//        HinhAnh res = hinhAnhService.uploadImageToDrive(tempFile);
//        System.out.println(res);
//        return res;
//    }
}

