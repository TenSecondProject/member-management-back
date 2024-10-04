package org.colcum.admin.domain.post.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.colcum.admin.domain.post.domain.PostEntity;
import org.colcum.admin.domain.post.domain.type.PostStatus;
import org.colcum.admin.domain.user.domain.vo.Bookmark;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class PostResponseDto {

    private Long id;
    private String title;
    private String content;
    private PostStatus status;
    private String writtenBy;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm")
    private LocalDateTime createdAt;
    private boolean isBookmarked;
    private int commentCount;
    private List<EmojiResponseDto> emojiResponseDtos;

    public PostResponseDto(Long id, String title, String content, PostStatus status, String writtenBy, LocalDateTime createdAt, boolean isBookmarked, int commentCount, List<EmojiResponseDto> emojiResponseDtos) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.status = status;
        this.writtenBy = writtenBy;
        this.createdAt = createdAt;
        this.isBookmarked = isBookmarked;
        this.commentCount = commentCount;
        this.emojiResponseDtos = emojiResponseDtos;
    }

    public static PostResponseDto of(Long id, String title, String content, PostStatus status, String writtenBy, LocalDateTime createdAt, boolean isBookmarked, int commentCount, List<EmojiResponseDto> emojiResponseDtos) {
        return new PostResponseDto(
            id,
            title,
            content,
            status,
            writtenBy,
            createdAt.truncatedTo(ChronoUnit.MILLIS),
            isBookmarked,
            commentCount,
            emojiResponseDtos
        );
    }

    public static PostResponseDto from(PostEntity entity, boolean isBookmarked) {
        return new PostResponseDto(
            entity.getId(),
            entity.getTitle(),
            entity.getContent(),
            entity.getStatus(),
            entity.getUser().getName(),
            entity.getCreatedAt().truncatedTo(ChronoUnit.MILLIS),
            isBookmarked,
            (int) entity.getCommentEntities().stream().filter(c -> !c.isDeleted()).count(),
            EmojiResponseDto.from(entity.getEmojiReactionEntities().stream().filter(e -> !e.isDeleted()).collect(Collectors.toList()))
        );
    }

}
