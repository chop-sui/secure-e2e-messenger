package org.sec.secureapp.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category {
    QNA("QnA"),
    KREPO("KnowledgeRepo");

    private final String value;
}

