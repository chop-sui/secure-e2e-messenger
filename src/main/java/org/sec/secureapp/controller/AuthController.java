package org.sec.secureapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sec.secureapp.dto.TodoDto;
import org.sec.secureapp.dto.UserDto;
import org.sec.secureapp.entity.User;
import org.sec.secureapp.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

    @GetMapping("/profile")
    public String showProfilePage(Model model) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();

        User user = userService.getUserByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("todos", user.getTodos());
        return "profile";
    }

    @PostMapping("/profile/todos")
    public String addTodo(@RequestParam String todoText) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();

        User user = userService.getUserByUsername(username);
        userService.addTodo(user, todoText);
        return "redirect:/profile";
    }

}
