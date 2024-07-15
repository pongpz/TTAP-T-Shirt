package com.project.ttaptshirt.service;


import com.project.ttaptshirt.dto.DataMailDTO;
import com.project.ttaptshirt.entity.DiaChi;
import com.project.ttaptshirt.entity.User;
import com.project.ttaptshirt.repository.DiaChiRepo;
import com.project.ttaptshirt.repository.UserRepo;
import com.project.ttaptshirt.util.Const;
import com.project.ttaptshirt.util.DataUtils;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
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
public class UserService {
    @Autowired
    private UserRepo repoUser;

    @Autowired
    private DiaChiRepo repoDc;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SpringTemplateEngine template;
    @Autowired
    private TemplateEngine thymeleafTemplateEngine;

    @Autowired
    private MailService mailService;

    public User save (User user){
        return repoUser.save(user);
    }

    public List<User> findAll(){
        return repoUser.findAll();
    }

    public List<User> findByCv(String cv){return repoUser.findByCv_Ten(cv);}

    public Optional<User> findById(Long id){
        return repoUser.findById(id);
    }

    public void deleteById(Long id){
        repoUser.deleteById(id);
    }

    public User updateDiachi(Long UserId, DiaChi diaChi){
        Optional<User> userOptional =repoUser.findById(UserId);
        if (userOptional.isPresent()){
            User user = userOptional.get();
            repoDc.save(diaChi);
            user.setDc(diaChi);
            return repoUser.save(user);
        }else {
            throw new RuntimeException("User not found with id " + UserId);
        }
    }

    public User createUser(User user) {
        repoUser.save(user);
        sendEmail(user);
        return user;
    }

    private void sendEmail(User user) {
        // Example of sending email using Thymeleaf template
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(message, true);
            helper.setTo(user.getEmail());
            helper.setSubject("Account Details");
            Context context = new Context();
            context.setVariable("username", user.getTaiKhoan());
            context.setVariable("password", user.getMatKhau());
            String htmlContent = thymeleafTemplateEngine.process("/user/email-template", context);
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public Boolean registerUser(User user) {
        repoUser.save(user);
        try {
            DataMailDTO dataMail = new DataMailDTO();

            dataMail.setTo(user.getEmail());
            dataMail.setSubject(Const.SEND_MAIL_SUBJECT.CLIENT_REGISTER);

            Map<String, Object> props = new HashMap<>();
            props.put("name", user.getHoTen());
            props.put("username", user.getTaiKhoan());
            props.put("password", user.getMatKhau());
            dataMail.setProps(props);

            mailService.sendHtmlMail(dataMail, Const.TEMPLATE_FILE_NAME.CLIENT_REGISTER);
            return true;
        } catch (MessagingException exp){
            exp.printStackTrace();
        }
        return false;
    }

//    private void sendAccountDetailsEmail(User user) throws MessagingException {
//        MimeMessage mimeMessage = mailSender.createMimeMessage();
//        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
//
//        Context context = new Context();
//        context.setVariable("username", user.getTaiKhoan());
//        context.setVariable("password", user.getMatKhau());
//
//        String html = templateEngine.process("email-template", context);
//        helper.setText(html, true);  // true để chỉ định rằng nội dung email là HTML
//        helper.setTo(user.getEmail());
//        helper.setSubject("Your Account Details");
//
//        mailSender.send(mimeMessage);
//    }

//    public void registerUser(User user) {
//        repoUser.save(user);
//        sendAccountDetailsEmail(user);
//    }
//
//    private void sendAccountDetailsEmail(User user) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(user.getEmail());
//        message.setSubject("Your Account Details");
//        message.setText("Kính gửi " + user.getTaiKhoan() + ",\n\n" +
//                "Tài khoản của bạn đã được kích hoạt. Đây là ch tiết\n\n" +
//                "Username: " + user.getTaiKhoan()+ "\n" +
//                "Password: " + user.getMatKhau() + "\n\n" +
//                "Giữ tài khoản cua bạn 1 cách an toàn.\n\n" +
//                "Best,\nTTAP Company");
//
//        mailSender.send(message);
//    }

}
