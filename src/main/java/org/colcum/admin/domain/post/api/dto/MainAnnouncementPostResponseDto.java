package org.colcum.admin.domain.post.api.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class MainAnnouncementPostResponseDto {

    private final Long postId;
    private final String title;
    private final String content;

}
