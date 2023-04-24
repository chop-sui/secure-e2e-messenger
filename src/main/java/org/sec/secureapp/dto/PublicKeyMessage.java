package org.sec.secureapp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PublicKeyMessage {
    private String from;
    private String to;
    private Object key;
    private String time;

    public PublicKeyMessage(final String from, final Object key, final String time) {
        setFrom(from);
        setKey(key);
        this.time = time;
    }
}
