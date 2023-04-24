package org.sec.secureapp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Message {
    private String from;
    private String to;
    private String text;
}
