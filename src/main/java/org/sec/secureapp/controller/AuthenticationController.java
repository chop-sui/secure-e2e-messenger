package org.sec.secureapp.controller;


import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.sec.secureapp.dto.LoginRequestDto;
import org.sec.secureapp.entity.User;
import org.sec.secureapp.repository.UserRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.beans.Transient;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class AuthenticationController implements UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @GetMapping(path = "/user/login")
    public String logIn(Model model,
                      @RequestParam(value = "error", required = false) String error,
                      @RequestParam(value = "exception", required = false) String exception) {
        model.addAttribute("error", error);
        model.addAttribute("exception", exception);
        return "/user/login";
    }

    @PostMapping("/user/save")
    public String save(LoginRequestDto loginRequestDto) {
        String url = "/error/blank";

        try {
            loginRequestDto.setPassword(passwordEncoder.encode(loginRequestDto.getPassword()));
            userRepository.save(loginRequestDto.toUserEntity()).getId();
            url = "redirect:/user/login";
        } catch (Exception e) {
        }

        return url;
    }

    @GetMapping(value="/dashboard/friends")
    public String getDashboard(Model model) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        String username = null;
        if (authentication != null) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            username = userDetails.getUsername();
        }

        model.addAttribute("friends", userRepository.findByUsername(username).get().getFriends());
        return "dashboard/friends";
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Could not find user: " + username));
    }

    @Bean
    @Transient
    InitializingBean createInitialData() {
        return () -> {
            User user1 = User.builder().username("tommy12").password(passwordEncoder.encode("password")).build();
            User user2 = User.builder().username("jonny12").password(passwordEncoder.encode("password")).build();
            user1.setFriends(List.of(user2));
            user2.setFriends(List.of(user1));

            userRepository.save(user1);
            userRepository.save(user2);

        };
    }
}
