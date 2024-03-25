package org.colcum.admin.domain.post.api.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.colcum.admin.domain.post.domain.PostEntity;
import org.colcum.admin.domain.post.domain.type.PostStatus;

import java.util.List;

@Data
@NoArgsConstructor
public class PostResponseDto {

    private Long id;
    private String title;
    private String content;
    private PostStatus status;
    private String writtenBy;
    private boolean isBookmarked;
    private int commentCount;
//    private List<EmojiResponse> emojis;

    @QueryProjection
    public PostResponseDto(Long id, String title, String content, PostStatus status, String writtenBy, boolean isBookmarked, int commentCount) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.status = status;
        this.writtenBy = writtenBy;
        this.isBookmarked = isBookmarked;
        this.commentCount = commentCount;
//        this.emojis = emojis;
    }

    public static PostResponseDto of(Long id, String title, String content, PostStatus status, String writtenBy, boolean isBookmarked, int commentCount) {
        return new PostResponseDto(
            id,
            title,
            content,
            status,
            writtenBy,
            isBookmarked,
            commentCount
        );
    }

    public static PostResponseDto from(PostEntity entity) {
        return new PostResponseDto(
            entity.getId(),
            entity.getTitle(),
            entity.getContent(),
            entity.getStatus(),
            entity.getCreatedBy(),
            entity.isBookmarked(),
            entity.getCommentEntities().size()
        );
    }

}
