package org.sec.secureapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sec.secureapp.dto.UserDto;
import org.sec.secureapp.entity.User;
import org.sec.secureapp.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @RequestMapping(value = {"/login"}, method = RequestMethod.GET)
    public String login() {
        return "/auth/login";
    }

    @RequestMapping(value = {"/register"}, method = RequestMethod.GET)
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "/auth/register";
    }

    @RequestMapping(value = {"/register"}, method = RequestMethod.POST)
    public String registerUser(Model model, @Valid @ModelAttribute("user") UserDto userDto, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("bindingResult", bindingResult);
            return "auth/register";
        }

        List<Object> userPresentObj = userService.isUserPresent(userDto);

        if ((Boolean) userPresentObj.get(0)) {
            model.addAttribute("successMessage", userPresentObj.get(1));
            return "auth/register";
        }

        userService.saveUser(userDto);
        model.addAttribute("successMessage", "User registered successfully");

        return "homepage";
    }

}
