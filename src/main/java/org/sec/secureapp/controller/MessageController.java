package org.sec.secureapp.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.sec.secureapp.dto.Message;
import org.sec.secureapp.dto.OutputMessage;
import org.sec.secureapp.dto.PublicKeyMessage;
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
import org.springframework.web.bind.annotation.GetMapping;


import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequiredArgsConstructor
public class MessageController {
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/secured/chat")
    public void sendToSpecificUser(
        @Payload Message message,
        Principal user,
        @Header("simpSessionId") String sessionId) throws Exception {
        System.out.println("Received message: " + message.getText() +" from sessionId: " + sessionId);
        OutputMessage outputMessage = new OutputMessage(
            message.getFrom(),
            message.getText(),
            new SimpleDateFormat("HH:mm").format(new Date()));
        simpMessagingTemplate.convertAndSendToUser(
            message.getTo(),
            "/secured/user/queue/specific-user", outputMessage
        );
    }

    @MessageMapping("/secured/chat/key")
    public void sendPublicKey(
        @Payload PublicKeyMessage message,
        Principal user,
        @Header("simpSessionId") String sessionId) throws Exception {
        System.out.println("Received Public Key: " + message.getKey() +" from sessionId: " + sessionId);
        PublicKeyMessage publicKeyMessage = new PublicKeyMessage(
            message.getFrom(),
            message.getKey(),
            new SimpleDateFormat("HH:mm").format(new Date()));
        simpMessagingTemplate.convertAndSendToUser(
            message.getTo(),
            "/secured/user/queue/specific-user", message
        );
    }


    @GetMapping("/chat-room")
    public String showChatRoom(Model model) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        String username = null;
        if (authentication != null) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            username = userDetails.getUsername();
        }
        if (username != null) {
            model.addAttribute("username", username);
        } else {
            model.addAttribute("username", "someUser");
        }
        return "chat";
    }

}
