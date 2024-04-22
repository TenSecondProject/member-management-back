package org.colcum.admin.domain.post.dao;

import org.colcum.admin.domain.post.api.dto.PostBookmarkedResponse;
import org.colcum.admin.domain.post.api.dto.PostResponseDto;
import org.colcum.admin.domain.post.api.dto.PostSearchCondition;
import org.colcum.admin.domain.post.domain.PostEntity;
import org.colcum.admin.domain.user.domain.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CustomPostRepository {

    Page<PostResponseDto> search(PostSearchCondition condition, Pageable pageable);

    Page<PostResponseDto> searchReceivedPost(PostSearchCondition postSearchCondition, UserEntity receivedUser, Pageable pageable);

    Optional<PostEntity> findByIdWithUser(Long userId);

    Optional<PostEntity> findByIdAndDeletedIsFalse(Long id);

    List<PostBookmarkedResponse> findWithBookmarked(Long userId);

}
