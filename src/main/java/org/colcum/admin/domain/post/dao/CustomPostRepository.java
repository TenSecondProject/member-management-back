package org.colcum.admin.domain.post.dao;

import org.colcum.admin.domain.post.api.dto.PostResponseDto;
import org.colcum.admin.domain.post.api.dto.PostSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;

public interface CustomPostRepository {

    Page<PostResponseDto> search(PostSearchCondition condition, Pageable pageable);

}
