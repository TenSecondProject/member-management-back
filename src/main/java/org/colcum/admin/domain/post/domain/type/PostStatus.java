package org.colcum.admin.domain.post.domain.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PostStatus {
    UNCOMPLETED("미완료"), IN_PROGRESS("진행중"), COMPLETE("완료");

    private final String description;

}
