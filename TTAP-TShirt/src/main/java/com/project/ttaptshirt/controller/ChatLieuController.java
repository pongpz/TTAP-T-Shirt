
package com.project.ttaptshirt.controller;


import com.project.ttaptshirt.entity.ChatLieu;
import com.project.ttaptshirt.repository.ChatLieuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ChatLieuController {
    @Autowired
    ChatLieuRepository cli;

    @GetMapping("/admin/chat-lieu/view")
    public String viewChatLieu(Model model){
        model.addAttribute("listChatLieu",cli.findAll());
        return "admin/thuoctinhsanpham/chat-lieu";
    }

    @PostMapping("/admin/chat-lieu/add")
    public String addChatLieu(ChatLieu cl){
        cli.save(cl);
        return "redirect:/admin/chat-lieu/view";
    }

    @PostMapping("/admin/chat-lieu/update/{id}")
    public String updateChatLieu(ChatLieu cl,@PathVariable("id") Long id){
        cl.setId(id);
        cli.save(cl);
        return "redirect:/admin/chat-lieu/view";
    }

//    @GetMapping("/admin/chat-lieu/delete")
//    public String deleteChatLieu(@RequestParam("id") Integer id){
//        cli.delete(cli.getReferenceById(id));
//        return "redirect:/admin/chat-lieu/view";
//    }

    @GetMapping("/admin/chat-lieu/update/{id}")
    public String detailChatLieu(@PathVariable("id") Long id, Model model){
        model.addAttribute("detailChatLieu",cli.getReferenceById(id));
        model.addAttribute("listChatLieu",cli.findAll());
        return "admin/thuoctinhsanpham/update-chat-lieu";
    }
}
