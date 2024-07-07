package com.project.ttaptshirt.controller;


import com.project.ttaptshirt.entity.ChatLieu;
import com.project.ttaptshirt.repository.ChatLieuInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ChatLieuController {
    @Autowired
    ChatLieuInterface cli;

    @GetMapping("/chat-lieu/view")
    public String viewChatLieu(Model model){
        model.addAttribute("listChatLieu",cli.findAll());
        return "viewChatLieu";
    }

    @PostMapping("/chat-lieu/add")
    public String addChatLieu(ChatLieu cl){
        cli.save(cl);
        return "redirect:/chat-lieu/view";
    }

    @PostMapping("/chat-lieu/update")
    public String updateChatLieu(ChatLieu cl){
        cli.save(cl);
        return "redirect:/chat-lieu/view";
    }

    @GetMapping("/chat-lieu/delete")
    public String deleteChatLieu(@RequestParam("id") Integer id){
        cli.delete(cli.getReferenceById(id));
        return "redirect:/chat-lieu/view";
    }

    @GetMapping("/chat-lieu/detail")
    public String detailChatLieu(@RequestParam("id") Integer id, Model model){
        model.addAttribute("detailChatLieu",cli.getReferenceById(id));
        model.addAttribute("listChatLieu",cli.findAll());
        return "viewChatLieu";
    }
}
