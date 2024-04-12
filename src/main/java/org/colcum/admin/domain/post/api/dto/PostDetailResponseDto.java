package org.colcum.admin.domain.post.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.colcum.admin.domain.post.domain.PostEntity;
import org.colcum.admin.domain.post.domain.type.PostCategory;
import org.colcum.admin.domain.post.domain.type.PostStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDetailResponseDto {

    private Long id;
    private String title;
    private String content;
    private PostCategory category;
    private PostStatus status;
    private boolean isBookmarked;

    @JsonFormat(pattern = "yy/MM/dd", timezone = "Asia/Seoul")
    private LocalDate expiredDate;
    private String writtenBy;

    @JsonFormat(pattern = "yyyy.MM.dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    private String username;
    private List<CommentResponseDto> commentResponseDtos;
    private List<EmojiResponseDto> emojiResponseDtos;

    public static PostDetailResponseDto of(
        Long id,
        String title,
        String content,
        PostCategory category,
        PostStatus status,
        boolean isBookmarked,
        LocalDate expiredDate,
        String writtenBy,
        LocalDateTime createdAt,
        String username,
        List<CommentResponseDto> commentResponseDtos,
        List<EmojiResponseDto> emojiResponseDtos
    ) {
        return new PostDetailResponseDto(
            id,
            title,
            content,
            category,
            status,
            isBookmarked,
            expiredDate,
            writtenBy,
            createdAt,
            username,
            commentResponseDtos,
            emojiResponseDtos
        );
    }

    public static PostDetailResponseDto from(PostEntity post) {
        return new PostDetailResponseDto(
            post.getId(),
            post.getTitle(),
            post.getContent(),
            post.getCategory(),
            post.getStatus(),
            post.isBookmarked(),
            post.getExpiredDate(),
            post.getUser().getName(),
            post.getCreatedAt(),
            post.getUser().getName(),
            post.getCommentEntities().stream().map(CommentResponseDto::from).toList(),
            EmojiResponseDto.from(post.getEmojiReactionEntities())
        );
    }

}
