package com.project.ttaptshirt.controller;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin/image")
public class HinhAnhController {


    @Autowired
    private HinhAnhService serImage;

    @GetMapping("")
    public List<HinhAnh> getall(){
        return serImage.findAll();
    }

    @GetMapping("/showuploadFrom")
    public String showUploadForm(Model model) {
        HinhAnh hinhAnh = new HinhAnh();
        model.addAttribute("hinhanh", hinhAnh);
        model.addAttribute("image", serImage.findAll());
        return "/admin/hinhanh/index";
    }

    @PostMapping("/uploadImage")
    public RedirectView uploadImage(@ModelAttribute("hinhanh") HinhAnh hinhAnh,
                              @RequestParam("image") MultipartFile miMultipartFile)throws IOException  {
        String filename = StringUtils.cleanPath(miMultipartFile.getOriginalFilename());

        hinhAnh.setTen(filename);

        HinhAnh saveHinhAnh1 = serImage.save(hinhAnh);
        String uploadDir = "image/"+saveHinhAnh1.getId();
        FileUploadUtil.savefile(uploadDir,filename,miMultipartFile);

//        Date cretaDate = new Date();
//        String storagePath = cretaDate.getTime()+"_"+image.getOriginalFilename();
//        try {
//            String uploadDir = "public/images/";
//            Path upPath = Paths.get(uploadDir);
//            if (!Files.exists(upPath)){
//                Files.createDirectories(upPath);
//            }
//            try(InputStream inputStream = image.getInputStream()) {
//                Files.copy(inputStream,Paths.get(uploadDir+storagePath),
//                        StandardCopyOption.REPLACE_EXISTING);
//            }
//        }catch (Exception ex){
//            System.out.println("Exception: "+ex.getMessage());
//        }
//
//        HinhAnh anh = new HinhAnh();
//        anh.setMa(hinhAnh.getMa());
//        anh.setTen(storagePath);
        return new RedirectView("/admin/image/showuploadFrom", true);
    }


}
