package com.project.ttaptshirt.controller.adminController;

import com.project.ttaptshirt.entity.MaGiamGia;
import com.project.ttaptshirt.repository.MaGiamGiaRepo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/admin/ma-giam-gia")
public class MaGiamGiaController {

    @Autowired
    MaGiamGiaRepo mggr;

    @GetMapping("/hien-thi")
    public String hienthi (Model model, @RequestParam(value = "page",defaultValue = "0") Integer page){
        Pageable pageab = PageRequest.of(page, 5);
        Page<MaGiamGia> p = mggr.findAllDESC(pageab);
        if (p.getContent().size() == 0){
            model.addAttribute("nullmgg","Không có mã giảm giá nào");
        }
        model.addAttribute("ListMGG",p);
        model.addAttribute("page",page);
        return "admin/magiamgia/voucher";
    }

    @Transactional
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

    @GetMapping("/tim-kiem")
    public String search(@RequestParam String tenSearch,@RequestParam String maSearch,@RequestParam String hinhThucSearch,@RequestParam String thoiHanSearch,@RequestParam String soLuongSearch, @RequestParam(value = "page",defaultValue = "0") Integer page, MaGiamGia mgg, Model model){
        Pageable pageab = PageRequest.of(page, 5);
        Boolean hinhThuc;
        if (hinhThucSearch.equals("VND")){
            hinhThuc = true;
        }else if (hinhThucSearch.equals("phan-tram")){
            hinhThuc = false;
        }else {
            hinhThuc = null;
        }
        LocalDateTime thoiHan1,thoiHan2;
        if (thoiHanSearch.equals("con-han")){
            thoiHan1 = LocalDateTime.now();
            thoiHan2 = null;
        }else if(thoiHanSearch.equals("het-han")){
            thoiHan1 = null;
            thoiHan2 = LocalDateTime.now();
        }else {
            thoiHan1 = null;
            thoiHan2 = null;
        }
        Integer soLuong1,soLuong2;
        if (soLuongSearch.equals("con-hang")){
            soLuong1 = 0;
            soLuong2 = null;
        }else if (soLuongSearch.equals("het-hang")){
            soLuong1 = null;
            soLuong2 = 0;
        }else {
            soLuong1 = null;
            soLuong2 = null;
        }
        List<MaGiamGia> ls = mggr.searchByAll(tenSearch,maSearch,hinhThuc,thoiHan1,thoiHan2,soLuong1,soLuong2,pageab);

        if (ls.size() == 0){
            model.addAttribute("nullmgg","Không có mã giảm giá nào");
        }
        model.addAttribute("mgg",mgg);
        model.addAttribute("ListMGG",ls);
        model.addAttribute("page",page);
        model.addAttribute("hinhThucSearch",hinhThucSearch);
        model.addAttribute("soLuongSearch",soLuongSearch);
        model.addAttribute("thoiHanSearch",thoiHanSearch);
        model.addAttribute("tenSearch",tenSearch);
        model.addAttribute("maSearch",maSearch);
        return "admin/magiamgia/tim-kiem-voucher";
    }

    @Transactional
    @PostMapping("/update")
    public String update(@Valid MaGiamGia mgg, Errors errors, Model model){
        boolean check_ma = false;
        for (int i = 0; i < mggr.findAllMaNotHaveThisId(mgg.getId()).size(); i ++){
            if (mggr.findAllMaNotHaveThisId(mgg.getId()).get(i).equals(mgg.getMa())){
                check_ma = true;
            }
        }
        model.addAttribute("mgg",mgg);
        if (errors.hasErrors()){
            model.addAttribute("errors","Vui lòng điền đủ trường!");
            return "admin/magiamgia/voucher-detail";
        }
        if (mgg.getNgayBatDau().isAfter(mgg.getNgayKetThuc())){
            model.addAttribute("errors","Ngày bắt đầu phải sớm hơn ngày kết thúc!");
            return "admin/magiamgia/voucher-detail";
        }else if (!mgg.getHinhThuc() && mgg.getGiaTriGiam()>100){
            model.addAttribute("errors","Giá trị giảm không được vượt quá 100%!");
            return "admin/magiamgia/voucher-detail";
        }else if (mgg.getHinhThuc() && mgg.getGiaTriToiDa()>mgg.getGiaTriGiam()) {
            model.addAttribute("errors", "Giá trị giảm không được vượt quá giá trị giảm!");
            return "admin/magiamgia/voucher-detail";
        }else if(!mgg.getSoLuong().toString().trim().matches("\\d+") || mgg.getSoLuong() <= 0){
            model.addAttribute("errors","Số lượng phải là số và lớn hơn 0!");
            return "admin/magiamgia/voucher-detail";
        }else if(!mgg.getGiaTriGiam().toString().trim().matches("\\d+") || mgg.getGiaTriGiam() <= 0){
            model.addAttribute("errors","Giá trị giảm phải là số và lớn hơn 0!");
            return "admin/magiamgia/voucher-detail";
        }else if(!mgg.getGiaTriToiDa().toString().trim().matches("\\d+") || mgg.getGiaTriToiDa() <= 0){
            model.addAttribute("errors","Giá trị tối đa phải là số và lớn hơn 0!");
            return "admin/magiamgia/voucher-detail";
        }else if(!mgg.getGiaTriToiThieu().toString().trim().matches("\\d+") || mgg.getGiaTriToiThieu() < 0){
            model.addAttribute("errors","Gíá trị đơn hàng tối thiểu phải là số và lớn hơn 0!");
            return "admin/magiamgia/voucher-detail";
        }else if (mgg.getNgayBatDau().isBefore(LocalDateTime.now())){
            model.addAttribute("errors","Ngày bắt đầu phải sau thời điểm hiện tại!");
            return "admin/magiamgia/voucher-detail";
        }else if (mgg.getNgayKetThuc().isBefore(LocalDateTime.now())){
            model.addAttribute("errors","Ngày kết thúc phải sau thời điểm hiện tại!");
            return "admin/magiamgia/voucher-detail";
        }else if (check_ma){
            model.addAttribute("errors","Mã giảm giá đã tồn tại, vui lòng nhập mã khác!");
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
    public String add(@Valid MaGiamGia mgg , Errors errors, Model model){
        if (errors.hasFieldErrors()){
            model.addAttribute("errors","Vui lòng điền đủ trường!");
            model.addAttribute("mgg",mgg);
            return "admin/magiamgia/form-add-voucher";
        }
        model.addAttribute("mgg",mgg);
        if (mgg.getNgayBatDau().isAfter(mgg.getNgayKetThuc())){
            model.addAttribute("errors","Ngày bắt đầu phải sớm hơn ngày kết thúc!");
            return "admin/magiamgia/form-add-voucher";
        }else if (!mgg.getHinhThuc() && mgg.getGiaTriGiam()>100){
            model.addAttribute("errors","Giá trị giảm không được vượt quá 100%!");
            return "admin/magiamgia/form-add-voucher";
        }else if (mgg.getHinhThuc() && mgg.getGiaTriToiDa()>mgg.getGiaTriGiam()){
            model.addAttribute("errors","Giá trị giảm không được vượt quá giá trị giảm!");
            return "admin/magiamgia/form-add-voucher";
        }else if(!mgg.getSoLuong().toString().trim().matches("\\d+") || mgg.getSoLuong() <= 0){
            model.addAttribute("errors","Số lượng phải là số và lớn hơn 0!");
            return "admin/magiamgia/form-add-voucher";
        }else if(!mgg.getGiaTriGiam().toString().trim().matches("\\d+") || mgg.getGiaTriGiam() <= 0){
            model.addAttribute("errors","Giá trị giảm phải là số và lớn hơn 0!");
            return "admin/magiamgia/form-add-voucher";
        }else if(!mgg.getGiaTriToiDa().toString().trim().matches("\\d+") || mgg.getGiaTriToiDa() <= 0){
            model.addAttribute("errors","Giá trị tối đa phải là số và lớn hơn 0!");
            return "admin/magiamgia/form-add-voucher";
        }else if(!mgg.getGiaTriToiThieu().toString().trim().matches("\\d+") || mgg.getGiaTriToiThieu() < 0){
            model.addAttribute("errors","Gíá trị đơn hàng tối thiểu phải là số và lớn hơn 0!");
            return "admin/magiamgia/form-add-voucher";
        }else if (mgg.getNgayBatDau().isBefore(LocalDateTime.now())){
            model.addAttribute("errors","Ngày bắt đầu phải sau thời điểm hiện tại!");
            return "admin/magiamgia/form-add-voucher";
        }else if (mgg.getNgayKetThuc().isBefore(LocalDateTime.now())){
            model.addAttribute("errors","Ngày kết thúc phải sau thời điểm hiện tại!");
            return "admin/magiamgia/form-add-voucher";
        }else if (!mggr.findbyMa(mgg.getMa()).isEmpty()){
            model.addAttribute("errors","Mã giảm giá đã tồn tại, vui lòng nhập mã khác!");
            return "admin/magiamgia/form-add-voucher";
        }
        else {
            mgg.setNgayTao(LocalDateTime.now());
            mgg.setNgaySua(LocalDateTime.now());
            mggr.save(mgg);
            return "redirect:/admin/ma-giam-gia/hien-thi";
        }
    }

    @GetMapping("/form-add")
    public String formAdd(MaGiamGia mgg, Model model){
        model.addAttribute("mgg",mgg);
        return "admin/magiamgia/form-add-voucher";
    }

//    @Scheduled(fixedRate = 5000)
//    public void ChangeStatus(){
//        List<MaGiamGia> ls = mggr.getMGGHetHan(LocalDateTime.now());
//        for (int i = 0 ; i < ls.size() ; i ++){
//            MaGiamGia mgg = new MaGiamGia();
//            mgg = ls.get(i);
//            mgg.setTrangThai(false);
//            mgg.setId(ls.get(i).getId());
//            mggr.save(mgg);
//        }
//    }
}
