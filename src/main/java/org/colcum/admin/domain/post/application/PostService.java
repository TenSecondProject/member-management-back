package org.colcum.admin.domain.post.application;

import lombok.RequiredArgsConstructor;
import org.colcum.admin.domain.post.api.dto.PostResponseDto;
import org.colcum.admin.domain.post.api.dto.PostSearchCondition;
import org.colcum.admin.domain.post.dao.PostRepository;
import org.colcum.admin.domain.post.domain.type.PostCategory;
import org.colcum.admin.domain.post.domain.type.PostStatus;
import org.colcum.admin.domain.post.domain.type.SearchType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    @Transactional(readOnly = true)
    public Page<PostResponseDto> findByCriteria(List<PostCategory> category, List<PostStatus> status, PageRequest sort) {
        return findByCriteria(null, null, category, status, sort);
    }

    @Transactional(readOnly = true)
    public Page<PostResponseDto> findByCriteria(SearchType searchType, String searchValue, List<PostCategory> categories, List<PostStatus> statuses, PageRequest pageRequest) {
        return postRepository.search(
            new PostSearchCondition(searchType, searchValue, categories, statuses),
            pageRequest
        );
    }

}
