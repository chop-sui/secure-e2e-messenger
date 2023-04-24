package org.sec.secureapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.sec.secureapp.entity.User;

@Getter
@Setter
@AllArgsConstructor
public class LoginRequestDto {
    private String username;
    private String password;

    public User toUserEntity() {
        return User.builder()
            .username(username)
            .password(password)
            .build();
    }
}
