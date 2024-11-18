
package com.project.ttaptshirt.controller.adminController;

import ch.qos.logback.core.util.StringUtil;
import com.project.ttaptshirt.config.FileUploadUtil;
import com.project.ttaptshirt.dto.HinhAnhDto;
import com.project.ttaptshirt.entity.HinhAnh;
import com.project.ttaptshirt.service.HinhAnhService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
}

