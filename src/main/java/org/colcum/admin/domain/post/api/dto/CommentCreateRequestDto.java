package org.colcum.admin.domain.post.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.colcum.admin.domain.post.domain.CommentEntity;
import org.colcum.admin.domain.post.domain.PostEntity;
import org.colcum.admin.domain.user.domain.UserEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateRequestDto {

    private String content;

    public CommentEntity toEntity(UserEntity user, PostEntity post) {
        return new CommentEntity(this.content, user, post);
    }

}
