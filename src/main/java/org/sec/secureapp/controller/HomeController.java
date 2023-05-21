package org.sec.secureapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class HomeController {
    @GetMapping("/")
    public String showHomePage(Model model) {
        model.addAttribute("pageName", "UCSS");
        return "homepage";
    }

    @GetMapping("/about")
    public String showAboutPage(Model model) {
        return "about";
    }

    @GetMapping("/help")
    public String showHelpPage(Model model) {
        return "help";
    }
}
