package org.sec.secureapp.dto;

import java.util.List;

public record UserDto(
        Integer id,
        List<Integer> friends
) {
}
