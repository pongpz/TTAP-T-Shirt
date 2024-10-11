package com.project.ttaptshirt.controller;

import com.project.ttaptshirt.entity.MaGiamGia;
import com.project.ttaptshirt.repository.MaGiamGiaRepo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/admin/ma-giam-gia")
public class MaGiamGiaController {

    @Autowired
    MaGiamGiaRepo mggr;

    @GetMapping("/hien-thi")
    public String hienthi (Model model, @RequestParam(value = "page",defaultValue = "0") Integer page, MaGiamGia mgg){
        Pageable pageab = PageRequest.of(page, 9);
        Page<MaGiamGia> p = mggr.findAll(pageab);
        if (p.getContent().size() == 0){
            model.addAttribute("nullmgg","Không có mã giảm giá nào");
        }
        model.addAttribute("mgg",mgg);
        model.addAttribute("ListMGG",p);
        model.addAttribute("page",page);
        return "admin/magiamgia/voucher";
    }

    @GetMapping("/detail/{idMGG}")
    public String detail (Model model, @PathVariable("idMGG") Long idMGG){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        MaGiamGia mgg = mggr.getReferenceById(idMGG);
        String formatDateStart = (mgg.getNgayBatDau() != null) ? mgg.getNgayBatDau().format(formatter) : "Không có ngày bắt đầu";
        String formatDateEnd = (mgg.getNgayKetThuc() != null) ? mgg.getNgayKetThuc().format(formatter) : "Không có ngày kết thúc";
        model.addAttribute("mgg",mgg);
        model.addAttribute("startDay",formatDateStart);
        model.addAttribute("endDay",formatDateEnd);
        return "admin/magiamgia/voucher-detail";
    }


    @PostMapping("/update")
    public String update(@Valid MaGiamGia mgg, Errors errors, Model model){
        boolean check_ma = false;
        for (int i = 0; i < mggr.findAllMaNotHaveThisId(mgg.getId()).size(); i ++){
            if (mggr.findAllMaNotHaveThisId(mgg.getId()).get(i).equals(mgg.getMa())){
                check_ma = true;
            }
        }
        if (errors.hasErrors()){
            model.addAttribute("errors","Vui lòng điền đủ trường!");
            model.addAttribute("mgg",mgg);
            return "admin/magiamgia/voucher-detail";
        }
        if (mgg.getNgayBatDau().isAfter(mgg.getNgayKetThuc())){
            model.addAttribute("errors","Ngày bắt đầu phải sớm hơn ngày kết thúc!");
            model.addAttribute("mgg",mgg);
            return "admin/magiamgia/voucher-detail";
        }else if (!mgg.getHinhThuc() && mgg.getGiaTriGiam()>100){
            model.addAttribute("errors","Giá trị giảm không được vượt quá 100%!");
            model.addAttribute("mgg",mgg);
            return "admin/magiamgia/voucher-detail";
        }else if(!mgg.getSoLuong().toString().trim().matches("\\d+")){
            model.addAttribute("errors","Số lượng phải là số!");
            model.addAttribute("mgg",mgg);
            return "admin/magiamgia/voucher-detail";
        }else if(!mgg.getGiaTriGiam().toString().trim().matches("\\d+")){
            model.addAttribute("errors","Giá trị giảm phải là số!");
            model.addAttribute("mgg",mgg);
            return "admin/magiamgia/voucher-detail";
        }else if(!mgg.getGiaTriToiDa().toString().trim().matches("\\d+")){
            model.addAttribute("errors","Giá trị tối đa phải là số!");
            model.addAttribute("mgg",mgg);
            return "admin/magiamgia/voucher-detail";
        }else if(!mgg.getGiaTriToiThieu().toString().trim().matches("\\d+")){
            model.addAttribute("errors","Gíá trị đơn hàng tối thiểu phải là số!");
            model.addAttribute("mgg",mgg);
            return "admin/magiamgia/voucher-detail";
        }else if (mgg.getNgayKetThuc().isBefore(LocalDateTime.now())){
            model.addAttribute("errors","Ngày kết thúc phải sau ngày hôm nay!");
            model.addAttribute("mgg",mgg);
            return "admin/magiamgia/voucher-detail";
        }else if (check_ma){
            model.addAttribute("errors","Mã giảm giá đã tồn tại, vui lòng nhập mã khác!");
            model.addAttribute("mgg",mgg);
            return "admin/magiamgia/voucher-detail";
        }else {
            MaGiamGia mgg2 = mggr.getReferenceById(mgg.getId());
            mgg.setNgayTao(mgg2.getNgayTao());
            mgg.setNgaySua(LocalDateTime.now());
            mgg.setId(mgg.getId());
            mggr.save(mgg);
            return "redirect:/admin/ma-giam-gia/hien-thi";
        }
    }

    @PostMapping("/add")
    public String update(@Valid MaGiamGia mgg , Errors errors, Model model, @RequestParam(value = "page",defaultValue = "0") Integer page){
        if (errors.hasFieldErrors()){
            model.addAttribute("errors","Vui lòng điền đủ trường!");
            model.addAttribute("mgg",mgg);
            Pageable pageab = PageRequest.of(page, 9);
            Page<MaGiamGia> p = mggr.findAll(pageab);
            model.addAttribute("ListMGG",p);
            model.addAttribute("page",page);
            return "admin/magiamgia/voucher";
        }
        Pageable pageab = PageRequest.of(page, 9);
        Page<MaGiamGia> p = mggr.findAll(pageab);
        model.addAttribute("mgg",mgg);
        model.addAttribute("ListMGG",p);
        model.addAttribute("page",page);
        if (mgg.getNgayBatDau().isAfter(mgg.getNgayKetThuc())){
            model.addAttribute("errors","Ngày bắt đầu phải sớm hơn ngày kết thúc!");
            return "admin/magiamgia/voucher";
        }else if (!mgg.getHinhThuc() && mgg.getGiaTriGiam()>100){
            model.addAttribute("errors","Giá trị giảm không được vượt quá 100%!");
            return "admin/magiamgia/voucher";
        }else if(!mgg.getSoLuong().toString().trim().matches("\\d+")){
            model.addAttribute("errors","Số lượng phải là số!");
            return "admin/magiamgia/voucher";
        }else if(!mgg.getGiaTriGiam().toString().trim().matches("\\d+")){
            model.addAttribute("errors","Giá trị giảm phải là số!");
            return "admin/magiamgia/voucher";
        }else if(!mgg.getGiaTriToiDa().toString().trim().matches("\\d+")){
            model.addAttribute("errors","Giá trị tối đa phải là số!");
            return "admin/magiamgia/voucher";
        }else if(!mgg.getGiaTriToiThieu().toString().trim().matches("\\d+")){
            model.addAttribute("errors","Gíá trị đơn hàng tối thiểu phải là số!");
            return "admin/magiamgia/voucher";
        }else if (mgg.getNgayKetThuc().isBefore(LocalDateTime.now())){
            model.addAttribute("errors","Ngày kết thúc phải sau ngày hôm nay!");
            return "admin/magiamgia/voucher";
        }else if (!mggr.findbyMa(mgg.getMa()).isEmpty()){
            model.addAttribute("errors","Mã giảm giá đã tồn tại, vui lòng nhập mã khác!");
            return "admin/magiamgia/voucher";
        }
        else {
            mgg.setNgayTao(LocalDateTime.now());
            mgg.setNgaySua(LocalDateTime.now());
            mggr.save(mgg);
            return "redirect:/admin/ma-giam-gia/hien-thi";
        }
    }
}