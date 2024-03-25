package org.colcum.admin.domain.post.api.dto;

import lombok.Data;
import org.colcum.admin.domain.post.domain.type.PostCategory;
import org.colcum.admin.domain.post.domain.type.PostStatus;
import org.colcum.admin.domain.post.domain.type.SearchType;

import java.util.List;

@Data
public class PostSearchCondition {

    private final SearchType searchType;
    private final String searchValue;
    private final List<PostCategory> categories;
    private final List<PostStatus> postStatuses;

}
