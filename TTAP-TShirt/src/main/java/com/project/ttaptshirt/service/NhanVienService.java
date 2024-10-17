//package com.project.ttaptshirt.service;
//
//
//import com.project.ttaptshirt.dto.DataMailDTO;
//import com.project.ttaptshirt.entity.DiaChi;
//import com.project.ttaptshirt.entity.User;
//import com.project.ttaptshirt.repository.DiaChiRepo;
//import com.project.ttaptshirt.repository.NhanVienReopository;
//import com.project.ttaptshirt.repository.UserRepo;
//import com.project.ttaptshirt.util.Const;
//import jakarta.mail.MessagingException;
//import jakarta.mail.internet.MimeMessage;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.stereotype.Service;
//import org.thymeleaf.TemplateEngine;
//import org.thymeleaf.context.Context;
//import org.thymeleaf.spring6.SpringTemplateEngine;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//
//@Service
//public class NhanVienService {
//    @Autowired
//    private NhanVienReopository repoNhanVien;
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
//    public User save (User nv){
//        return repoNhanVien.save(nv);
//    }
//
//    public List<User> findAll(){
//        return repoNhanVien.findAll();
//    }
//
//    public List<User> findByCv(String cv){return repoNhanVien.findByCv_Ten(cv);}
//
//    public List<User> findByNhanVien(String name){return repoNhanVien.findByHoTenContainingIgnoreCase(name);}
//
//    public Optional<User> findById(Long id){
//        return repoNhanVien.findById(id);
//    }
//
//    public void deleteById(Long id){
//        repoNhanVien.deleteById(id);
//    }
//
//    public User updateDiachi(Long nhanVienId, DiaChi diaChi){
//        Optional<User> NhanVienOptional =repoNhanVien.findById(nhanVienId);
//        if (NhanVienOptional.isPresent()){
//            User nhanVien = NhanVienOptional.get();
//            repoDc.save(diaChi);
//            nhanVien.setDc(diaChi);
//            return repoNhanVien.save(nhanVien);
//        }else {
//            throw new RuntimeException("NhanVien not found with id " + nhanVienId);
//        }
//    }
//
//    public User createNhanVien(User nhanVien) {
//        repoNhanVien.save(nhanVien);
//        sendEmail(nhanVien);
//        return nhanVien;
//    }
//
//    private void sendEmail(User nhanVien) {
//        // Example of sending email using Thymeleaf template
//        MimeMessage message = mailSender.createMimeMessage();
//        MimeMessageHelper helper;
//        try {
//            helper = new MimeMessageHelper(message, true);
//            helper.setTo(nhanVien.getEmail());
//            helper.setSubject("Account Details");
//            Context context = new Context();
//            context.setVariable("NhanVienname", nhanVien.getTaiKhoan());
//            context.setVariable("password", nhanVien.getMatKhau());
//            String htmlContent = thymeleafTemplateEngine.process("/user/email-template", context);
//            helper.setText(htmlContent, true);
//            mailSender.send(message);
//        } catch (MessagingException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public Boolean registerNhanVien(User nhanVien) {
//        repoNhanVien.save(nhanVien);
//        try {
//            DataMailDTO dataMail = new DataMailDTO();
//
//            dataMail.setTo(nhanVien.getEmail());
//            dataMail.setSubject(Const.SEND_MAIL_SUBJECT.CLIENT_REGISTER);
//
//            Map<String, Object> props = new HashMap<>();
//            props.put("name", nhanVien.getHoTen());
//            props.put("Username", nhanVien.getTaiKhoan());
//            props.put("password", nhanVien.getMatKhau());
//            dataMail.setProps(props);
//
//            mailService.sendHtmlMail(dataMail, Const.TEMPLATE_FILE_NAME.CLIENT_REGISTER);
//            return true;
//        } catch (MessagingException exp){
//            exp.printStackTrace();
//        }
//        return false;
//    }
//
////    private void sendAccountDetailsEmail(NhanVien NhanVien) throws MessagingException {
////        MimeMessage mimeMessage = mailSender.createMimeMessage();
////        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
////
////        Context context = new Context();
////        context.setVariable("NhanVienname", NhanVien.getTaiKhoan());
////        context.setVariable("password", NhanVien.getMatKhau());
////
////        String html = templateEngine.process("email-template", context);
////        helper.setText(html, true);  // true để chỉ định rằng nội dung email là HTML
////        helper.setTo(NhanVien.getEmail());
////        helper.setSubject("Your Account Details");
////
////        mailSender.send(mimeMessage);
////    }
//
////    public void registerNhanVien(NhanVien NhanVien) {
////        repoNhanVien.save(NhanVien);
////        sendAccountDetailsEmail(NhanVien);
////    }
////
////    private void sendAccountDetailsEmail(NhanVien NhanVien) {
////        SimpleMailMessage message = new SimpleMailMessage();
////        message.setTo(NhanVien.getEmail());
////        message.setSubject("Your Account Details");
////        message.setText("Kính gửi " + NhanVien.getTaiKhoan() + ",\n\n" +
////                "Tài khoản của bạn đã được kích hoạt. Đây là ch tiết\n\n" +
////                "NhanVienname: " + NhanVien.getTaiKhoan()+ "\n" +
////                "Password: " + NhanVien.getMatKhau() + "\n\n" +
////                "Giữ tài khoản cua bạn 1 cách an toàn.\n\n" +
////                "Best,\nTTAP Company");
////
////        mailSender.send(message);
////    }
//
//}
