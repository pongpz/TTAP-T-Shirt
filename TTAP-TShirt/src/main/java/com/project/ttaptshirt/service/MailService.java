package com.project.ttaptshirt.service;


import com.project.ttaptshirt.dto.DataMailDTO;
import jakarta.mail.MessagingException;



public interface MailService {
    void sendHtmlMail(DataMailDTO dataMail, String templateName) throws MessagingException;
}
