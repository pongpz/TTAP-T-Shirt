package demo.ttap.controller;

import demo.ttap.entity.ChatLieu;
import demo.ttap.entity.NSX;
import demo.ttap.service.NSXInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class NSXController {
    @Autowired
    NSXInterface nsxi;

    @GetMapping("/nsx/view")
    public String viewChatLieu(Model model){
        model.addAttribute("listNSX",nsxi.findAll());
        return "viewNSX";
    }

    @PostMapping("/nsx/add")
    public String addChatLieu(NSX nsx){
        nsxi.save(nsx);
        return "redirect:/nsx/view";
    }

    @PostMapping("/nsx/update")
    public String updateChatLieu(NSX nsx){
        nsxi.save(nsx);
        return "redirect:/nsx/view";
    }

    @GetMapping("/nsx/delete")
    public String deleteChatLieu(@RequestParam("id") Integer id){
        nsxi.delete(nsxi.getReferenceById(id));
        return "redirect:/nsx/view";
    }

    @GetMapping("/nsx/detail")
    public String detailChatLieu(@RequestParam("id") Integer id, Model model){
        model.addAttribute("detailNSX",nsxi.getReferenceById(id));
        model.addAttribute("listNSX",nsxi.findAll());
        return "viewNSX";
    }
}
