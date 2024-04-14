package org.colcum.admin.domain.post.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.colcum.admin.domain.post.domain.PostEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostBookmarkedResponse {

    private Long postId;
    private String title;

    public static PostBookmarkedResponse from(PostEntity post) {
        return new PostBookmarkedResponse(post.getId(), post.getTitle());
    }

}
