package org.colcum.admin.domain.post.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.colcum.admin.domain.post.domain.PostEntity;
import org.colcum.admin.domain.post.domain.type.PostCategory;
import org.colcum.admin.domain.post.domain.type.PostStatus;
import org.colcum.admin.domain.user.domain.UserEntity;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateDto {

    private String title;
    private String content;
    private PostCategory category;
    private PostStatus status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime expiredDate;
    private List<Long> sendTargetUserIds;

    public PostEntity toEntity(UserEntity user) {
        return new PostEntity(
            title,
            content,
            category,
            status,
            expiredDate,
            user
        );
    }

}
