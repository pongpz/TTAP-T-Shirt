package com.project.ttaptshirt.service;


import com.project.ttaptshirt.dto.DataMailDTO;
import com.project.ttaptshirt.entity.DiaChi;
import com.project.ttaptshirt.entity.User;
import com.project.ttaptshirt.repository.DiaChiRepo;
import com.project.ttaptshirt.repository.KhachHangReopository;
import com.project.ttaptshirt.util.Const;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class KhachHangService {
//    @Autowired
//    private KhachHangReopository repoKhachHang;
//
//    @Autowired
//    private DiaChiRepo repoDc;
//
//    @Autowired
//    private JavaMailSender mailSender;
//
//    @Autowired
//    private SpringTemplateEngine template;
//    @Autowired
//    private TemplateEngine thymeleafTemplateEngine;
//
//    @Autowired
//    private MailService mailService;
//
//    public User save (User kh){
//        return repoKhachHang.save(kh);
//    }
//
//    public List<User> findAll(){
//        return repoKhachHang.findAll();
//    }
//
//    public List<User> findByKhachHang(String name){return repoKhachHang.findByHoTenContainingIgnoreCase(name);}
//
//    public Optional<User> findById(Long id){
//        return repoKhachHang.findById(id);
//    }
//
//    public void deleteById(Long id){
//        repoKhachHang.deleteById(id);
//    }
//
//    public User updateDiachi(Long khachHangId, DiaChi diaChi){
//        Optional<User> khachHangOptional =repoKhachHang.findById(khachHangId);
//        if (khachHangOptional.isPresent()){
//            User khachHang = khachHangOptional.get();
//            repoDc.save(diaChi);
//            khachHang.setDc(diaChi);
//            return repoKhachHang.save(khachHang);
//        }else {
//            throw new RuntimeException("KhachHang not found with id " + khachHangId);
//        }
//    }
//
//    public User createKhachHang(User khachHang) {
//        repoKhachHang.save(khachHang);
//        sendEmail(khachHang);
//        return khachHang;
//    }
//
//    private void sendEmail(User khachHang) {
//        // Example of sending email using Thymeleaf template
//        MimeMessage message = mailSender.createMimeMessage();
//        MimeMessageHelper helper;
//        try {
//            helper = new MimeMessageHelper(message, true);
//            helper.setTo(khachHang.getEmail());
//            helper.setSubject("Account Details");
//            Context context = new Context();
//            context.setVariable("KhachHangname", khachHang.getTaiKhoan());
//            context.setVariable("password", khachHang.getMatKhau());
//            String htmlContent = thymeleafTemplateEngine.process("/user/email-template", context);
//            helper.setText(htmlContent, true);
//            mailSender.send(message);
//        } catch (MessagingException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public Boolean registerKhachHang(User khachHang) {
//        repoKhachHang.save(khachHang);
//        try {
//            DataMailDTO dataMail = new DataMailDTO();
//
//            dataMail.setTo(khachHang.getEmail());
//            dataMail.setSubject(Const.SEND_MAIL_SUBJECT.CLIENT_REGISTER);
//
//            Map<String, Object> props = new HashMap<>();
//            props.put("name", khachHang.getHoTen());
//            props.put("Username", khachHang.getTaiKhoan());
//            props.put("password", khachHang.getMatKhau());
//            dataMail.setProps(props);
//
//            mailService.sendHtmlMail(dataMail, Const.TEMPLATE_FILE_NAME.CLIENT_REGISTER);
//            return true;
//        } catch (MessagingException exp){
//            exp.printStackTrace();
//        }
//        return false;
//    }

//    private void sendAccountDetailsEmail(KhachHang KhachHang) throws MessagingException {
//        MimeMessage mimeMessage = mailSender.createMimeMessage();
//        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
//
//        Context context = new Context();
//        context.setVariable("KhachHangname", KhachHang.getTaiKhoan());
//        context.setVariable("password", KhachHang.getMatKhau());
//
//        String html = templateEngine.process("email-template", context);
//        helper.setText(html, true);  // true để chỉ định rằng nội dung email là HTML
//        helper.setTo(KhachHang.getEmail());
//        helper.setSubject("Your Account Details");
//
//        mailSender.send(mimeMessage);
//    }

//    public void registerKhachHang(KhachHang KhachHang) {
//        repoKhachHang.save(KhachHang);
//        sendAccountDetailsEmail(KhachHang);
//    }
//
//    private void sendAccountDetailsEmail(KhachHang KhachHang) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(KhachHang.getEmail());
//        message.setSubject("Your Account Details");
//        message.setText("Kính gửi " + KhachHang.getTaiKhoan() + ",\n\n" +
//                "Tài khoản của bạn đã được kích hoạt. Đây là ch tiết\n\n" +
//                "KhachHangname: " + KhachHang.getTaiKhoan()+ "\n" +
//                "Password: " + KhachHang.getMatKhau() + "\n\n" +
//                "Giữ tài khoản cua bạn 1 cách an toàn.\n\n" +
//                "Best,\nTTAP Company");
//
//        mailSender.send(message);
//    }

}
