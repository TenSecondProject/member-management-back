package org.colcum.admin.domain.post.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.colcum.admin.domain.post.domain.DirectPost;
import org.colcum.admin.domain.post.domain.type.PostStatus;
import org.colcum.admin.domain.user.domain.vo.Bookmark;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class SentPostResponseDto {

    private Long id;
    private String title;
    private String content;
    private PostStatus status;
    private String writtenBy;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm")
    private LocalDateTime createdAt;
    private String receivedUsername;
    private boolean isBookmarked;
    private int commentCount;
    private List<EmojiResponseDto> emojiResponseDtos;

    public SentPostResponseDto(Long id, String title, String content, PostStatus status, String writtenBy, LocalDateTime createdAt, String receivedUsername, boolean isBookmarked, int commentCount, List<EmojiResponseDto> emojiResponseDtos) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.status = status;
        this.writtenBy = writtenBy;
        this.createdAt = createdAt;
        this.receivedUsername = receivedUsername;
        this.isBookmarked = isBookmarked;
        this.commentCount = commentCount;
        this.emojiResponseDtos = emojiResponseDtos;
    }

    public static SentPostResponseDto of(Long id, String title, String content, PostStatus status, String writtenBy, LocalDateTime createdAt, String receivedUsername, boolean isBookmarked, int commentCount, List<EmojiResponseDto> emojiResponseDtos) {
        return new SentPostResponseDto(
            id,
            title,
            content,
            status,
            writtenBy,
            createdAt.truncatedTo(ChronoUnit.MILLIS),
            receivedUsername,
            isBookmarked,
            commentCount,
            emojiResponseDtos
        );
    }

    public static SentPostResponseDto from(DirectPost directPost, boolean isBookmarked) {
        return new SentPostResponseDto(
            directPost.getPostEntity().getId(),
            directPost.getPostEntity().getTitle(),
            directPost.getPostEntity().getContent(),
            directPost.getPostEntity().getStatus(),
            directPost.getPostEntity().getUser().getName(),
            directPost.getPostEntity().getCreatedAt().truncatedTo(ChronoUnit.MILLIS),
            directPost.getReceiver().getName(),
            isBookmarked,
            (int) directPost.getPostEntity().getCommentEntities().stream().filter(c -> !c.isDeleted()).count(),
            EmojiResponseDto.from(directPost.getPostEntity().getEmojiReactionEntities().stream().filter(e -> !e.isDeleted()).collect(Collectors.toList()))
        );
    }

}
