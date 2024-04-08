package org.colcum.admin.domain.post.domain.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PostCategory {
    ANNOUNCEMENT("공지"), DELIVERY("전달"), ETC("기타");

    private final String description;
}
