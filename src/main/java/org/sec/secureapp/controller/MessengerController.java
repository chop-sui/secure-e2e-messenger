package org.sec.secureapp.controller;

import lombok.RequiredArgsConstructor;
import org.sec.secureapp.dto.Message;
import org.sec.secureapp.dto.OutputMessage;
import org.sec.secureapp.dto.PublicKeyMessage;
import org.sec.secureapp.entity.User;
import org.sec.secureapp.service.UserService;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class MessengerController {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final UserService userService;

    @GetMapping("/messenger/chatroom/{id}")
    public String showChatRoom(Model model, @PathVariable String id) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        User friend = userService.getUserById(Integer.parseInt(id));

        if (username != null) {
            model.addAttribute("username", username);
            model.addAttribute("friendUsername", friend.getUsername());
        } else {
            model.addAttribute("username", "someUser");
        }
        return "messenger/chatroom";
    }

    @GetMapping("/messenger/friends")
    public String showFriends(Model model) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();

        if (username != null) {
            model.addAttribute("username", username);
            String finalUsername = username;

            model.addAttribute("users", userService.getAllUsers()
                .stream()
                .filter(user ->
                    !user.getUsername().equals(finalUsername)
                ).collect(Collectors.toList()));

            model.addAttribute("friends", userService.getUserByUsername(username).getFriends());
            model.addAttribute("friendsNames", userService.getUserByUsername(username).getFriends().stream()
                .map(User::getUsername).collect(Collectors.toList()));
        } else {
            model.addAttribute("username", "someUser");
        }
        return "messenger/friends";
    }

    @GetMapping("/messenger/friends/add/{id}")
    public String addFriend(@PathVariable String id) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        User user = userService.getUserByUsername(username);
        User friend = userService.getUserById(Integer.parseInt(id));

        userService.addFriend(user, friend);

        return "redirect:/messenger/friends";
    }

    @GetMapping("/messenger/friends/remove/{id}")
    public String removeFriend(@PathVariable String id) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        User user = userService.getUserByUsername(username);

        userService.removeFriend(user, Integer.parseInt(id));

        return "redirect:/messenger/friends";
    }

    @MessageMapping("/secured/chat")
    public void sendToSpecificUser(@Payload Message message, Principal user, @Header("simpSessionId") String sessionId) {

        System.out.println("Received message: " + message.getText() + " from sessionId: " + sessionId);

        OutputMessage outputMessage = new OutputMessage(
            message.getFrom(),
            message.getText(),
            new SimpleDateFormat("HH:mm").format(new Date()));

        simpMessagingTemplate.convertAndSendToUser(message.getTo(), "/secured/user/queue/specific-user", outputMessage);
    }

    @MessageMapping("/secured/chat/key")
    public void sendPublicKey(@Payload PublicKeyMessage message, Principal user, @Header("simpSessionId") String sessionId) {

        System.out.println("Received Public Key: " + message.getKey() + " from sessionId: " + sessionId);

        PublicKeyMessage publicKeyMessage = new PublicKeyMessage(
            message.getFrom(),
            message.getKey(),
            new SimpleDateFormat("HH:mm").format(new Date()));

        simpMessagingTemplate.convertAndSendToUser(message.getTo(), "/secured/user/queue/specific-user", message);
    }
}
