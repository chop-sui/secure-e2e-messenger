package org.sec.secureapp.dto;

import java.util.List;

public record UserDto(
    Integer id,
    String username,
    String password,
    List<Integer> friends
) {
}
