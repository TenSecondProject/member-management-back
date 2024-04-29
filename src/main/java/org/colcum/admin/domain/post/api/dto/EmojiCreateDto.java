package org.colcum.admin.domain.post.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.colcum.admin.domain.post.domain.EmojiReactionEntity;
import org.colcum.admin.domain.post.domain.PostEntity;
import org.colcum.admin.domain.user.domain.UserEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmojiCreateDto {

    private String content;

    public EmojiReactionEntity toEntity(PostEntity post, UserEntity user) {
        return new EmojiReactionEntity(user, post, this.content);
    }

}
