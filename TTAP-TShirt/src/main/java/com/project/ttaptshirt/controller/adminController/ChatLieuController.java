
package com.project.ttaptshirt.controller.adminController;


import com.project.ttaptshirt.entity.ChatLieu;
import com.project.ttaptshirt.entity.User;
import com.project.ttaptshirt.repository.ChatLieuRepository;
import com.project.ttaptshirt.security.CustomUserDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/chat-lieu")
public class ChatLieuController {
    @Autowired
    ChatLieuRepository cli;

    @GetMapping("/view")
    public String viewChatLieu(Model model, Authentication authentication){
        if (authentication != null) {
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            User user = customUserDetail.getUser();
            model.addAttribute("userLogged", user);
        }
        model.addAttribute("listChatLieu",cli.findAll());
        return "admin/thuoctinhsanpham/chat-lieu";
    }

    @PostMapping("/add")
    public String addChatLieu(ChatLieu cl){
        cli.save(cl);
        return "redirect:/admin/chat-lieu/view";
    }

    @PostMapping("/update/{id}")
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

    @GetMapping("/update/{id}")
    public String detailChatLieu(@PathVariable("id") Long id, Model model,Authentication authentication ){
        if (authentication != null) {
            CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
            User user = customUserDetail.getUser();
            model.addAttribute("userLogged", user);
        }
        ChatLieu chatLieu = cli.findById(id).orElse(null);
        model.addAttribute("detailChatLieu",chatLieu);
        model.addAttribute("listChatLieu",cli.findAll());
        return "admin/thuoctinhsanpham/update-chat-lieu";
    }
}
