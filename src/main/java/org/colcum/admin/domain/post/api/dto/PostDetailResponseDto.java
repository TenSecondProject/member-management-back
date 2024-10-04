package org.colcum.admin.domain.post.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.colcum.admin.domain.post.domain.PostEntity;
import org.colcum.admin.domain.post.domain.type.PostCategory;
import org.colcum.admin.domain.post.domain.type.PostStatus;
import org.colcum.admin.domain.user.domain.vo.Bookmark;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    @JsonFormat(pattern = "yyyy.MM.dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime expiredDate;
    private Long userId;
    private String username;

    @JsonFormat(pattern = "yyyy.MM.dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    private List<CommentResponseDto> commentResponseDtos;
    private List<EmojiResponseDto> emojiResponseDtos;

    public static PostDetailResponseDto of(
        Long id,
        String title,
        String content,
        PostCategory category,
        PostStatus status,
        boolean isBookmarked,
        LocalDateTime expiredDate,
        Long userId,
        String username,
        LocalDateTime createdAt,
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
            userId,
            username,
            createdAt,
            commentResponseDtos,
            emojiResponseDtos
        );
    }

    public static PostDetailResponseDto from(PostEntity post, boolean isBookmarked) {
        return new PostDetailResponseDto(
            post.getId(),
            post.getTitle(),
            post.getContent(),
            post.getCategory(),
            post.getStatus(),
            isBookmarked,
            post.getExpiredDate(),
            post.getUser().getId(),
            post.getUser().getName(),
            post.getCreatedAt(),
            post.getCommentEntities().stream().filter(c -> !c.isDeleted()).map(CommentResponseDto::from).toList(),
            EmojiResponseDto.from(post.getEmojiReactionEntities().stream().filter(e -> !e.isDeleted()).collect(Collectors.toList()))
        );
    }

}
