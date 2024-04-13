package org.colcum.admin.domain.post.application;

import lombok.RequiredArgsConstructor;
import org.colcum.admin.domain.post.api.dto.PostCreateDto;
import org.colcum.admin.domain.post.api.dto.PostDetailResponseDto;
import org.colcum.admin.domain.post.api.dto.PostResponseDto;
import org.colcum.admin.domain.post.api.dto.PostSearchCondition;
import org.colcum.admin.domain.post.api.dto.PostUpdateDto;
import org.colcum.admin.domain.post.dao.PostRepository;
import org.colcum.admin.domain.post.domain.PostEntity;
import org.colcum.admin.domain.post.domain.type.PostCategory;
import org.colcum.admin.domain.post.domain.type.PostStatus;
import org.colcum.admin.domain.post.domain.type.SearchType;
import org.colcum.admin.domain.user.domain.UserEntity;
import org.colcum.admin.global.Error.InvalidAuthenticationException;
import org.colcum.admin.global.Error.PostNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    @Transactional(readOnly = true)
    public Page<PostResponseDto> findByCriteria(SearchType searchType, String searchValue, List<PostCategory> categories, List<PostStatus> statuses, Pageable pageable) {
        return postRepository.search(
            new PostSearchCondition(searchType, searchValue, categories, statuses),
            pageable
        );
    }

    @Transactional(readOnly = true)
    public PostDetailResponseDto inquirePostDetail(Long id) {
        PostEntity post = postRepository.findById(id)
            .orElseThrow(() -> new PostNotFoundException("대상 게시글은 존재하지 않습니다."));

        return PostDetailResponseDto.from(post);
    }

    @Transactional
    public PostEntity createPost(PostCreateDto dto, UserEntity user) {
        PostEntity postEntity = dto.toEntity(user);
        return postRepository.save(postEntity);
    }

    @Transactional
    public PostUpdateDto updatePost(Long postId, PostUpdateDto dto, UserEntity user) {
        PostEntity post = postRepository.findByIdWithUser(postId).orElseThrow(() -> {
            throw new PostNotFoundException("해당 게시글을 찾을 수 없습니다.");
        });

        if (!post.getUser().getId().equals(user.getId())) {
            throw new InvalidAuthenticationException("해당 게시글을 수정할 권한이 없습니다.");
        }

        post = post.update(dto);
        postRepository.save(post);

        return dto;
    }

}
