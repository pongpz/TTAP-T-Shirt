package com.project.ttaptshirt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("/TTAP")
public class HomeCustomerController {
    @GetMapping("/trang-chu")
    public String home() {
        return "user/home/trangchu";
    }

    @GetMapping("/chinh-sach-van-chuyen")
    public String chinhSachVanChuyen(){
        return "user/chinhsach/chinh-sach-van-chuyen";
    }

    @GetMapping("/chinh-sach-bao-mat")
    public String chinhSachBaoMat(){
        return "user/chinhsach/chinh-sach-bao-mat";
    }

    @GetMapping("/chinh-sach-doi-tra")
    public String chinhSachDoiTra(){
        return "user/chinhsach/chinh-sach-doi-tra";
    }

    @GetMapping("/chinh-sach-xu-ly-khieu-lai")
    public String chinhSachXuLyKhieuLai(){
        return "user/chinhsach/chinh-sach-xu-ly-khieu-lai";
    }

    @GetMapping("/huong-dan-mua-hang")
    public String huongDanMuaHang(){
        return "user/chinhsach/huong-dan-mua-hang";
    }
    @GetMapping("/hinh-thuc-thanh-toan")
    public String hinhThucThanhToan(){
        return "user/chinhsach/hinh-thuc-thanh-toan";
    }
}
