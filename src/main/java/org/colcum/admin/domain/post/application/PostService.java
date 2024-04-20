package org.colcum.admin.domain.post.application;

import lombok.RequiredArgsConstructor;
import org.colcum.admin.domain.post.api.dto.CommentCreateRequestDto;
import org.colcum.admin.domain.post.api.dto.CommentUpdateRequestDto;
import org.colcum.admin.domain.post.api.dto.PostBookmarkedResponse;
import org.colcum.admin.domain.post.api.dto.PostCreateDto;
import org.colcum.admin.domain.post.api.dto.PostDetailResponseDto;
import org.colcum.admin.domain.post.api.dto.PostResponseDto;
import org.colcum.admin.domain.post.api.dto.PostSearchCondition;
import org.colcum.admin.domain.post.api.dto.PostUpdateDto;
import org.colcum.admin.domain.post.dao.CommentRepository;
import org.colcum.admin.domain.post.dao.PostRepository;
import org.colcum.admin.domain.post.domain.CommentEntity;
import org.colcum.admin.domain.post.domain.PostEntity;
import org.colcum.admin.domain.post.domain.type.PostCategory;
import org.colcum.admin.domain.post.domain.type.PostStatus;
import org.colcum.admin.domain.post.domain.type.SearchType;
import org.colcum.admin.domain.user.dao.UserRepository;
import org.colcum.admin.domain.user.domain.UserEntity;
import org.colcum.admin.domain.user.domain.vo.Bookmark;
import org.colcum.admin.global.Error.CommentNotFoundException;
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

    private final UserRepository userRepository;

    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    public Page<PostResponseDto> findByCriteria(SearchType searchType, String searchValue, List<PostCategory> categories, List<PostStatus> statuses, Pageable pageable) {
        return postRepository.search(
            new PostSearchCondition(searchType, searchValue, categories, statuses),
            pageable
        );
    }

    @Transactional(readOnly = true)
    public PostDetailResponseDto inquirePostDetail(Long id) {
        PostEntity post = postRepository.findByIdAndDeletedIsFalse(id)
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

    @Transactional
    public void deletePost(Long postId, UserEntity user) {
        PostEntity post = postRepository.findByIdWithUser(postId).orElseThrow(() -> {
            throw new PostNotFoundException("해당 게시글을 찾을 수 없습니다.");
        });
        if (!post.getUser().getId().equals(user.getId())) {
            throw new InvalidAuthenticationException("해당 게시글을 수정할 권한이 없습니다.");
        }
        post.delete();
        postRepository.save(post);
    }

    @Transactional(readOnly = true)
    public List<PostBookmarkedResponse> findBookmarkedPosts(UserEntity user) {
        return postRepository.findWithBookmarked(user.getId());
    }

    @Transactional
    public void addBookmark(Long postId, UserEntity user) {
        user.addBookmark(new Bookmark(postId));
        userRepository.save(user);
    }

    @Transactional
    public void removeBookmark(Long postId, UserEntity user) {
        user.removeBookmark(new Bookmark(postId));
        userRepository.save(user);
    }

    @Transactional
    public Long addComment(Long postId, CommentCreateRequestDto dto, UserEntity user) {
        PostEntity post = postRepository.findById(postId).orElseThrow(() -> {
            throw new PostNotFoundException("해당 게시글을 찾을 수 없습니다.");
        });
        CommentEntity comment = dto.toEntity(user, post);
        comment = commentRepository.save(commentRepository.save(comment));
        return comment.getId();
    }

    @Transactional
    public Long updateComment(Long commentId, CommentUpdateRequestDto dto) {
        CommentEntity comment = commentRepository.findByIdAndDeletedIsFalse(commentId)
            .orElseThrow(() -> new CommentNotFoundException("해당 댓글은 찾을 수 없습니다."));

        CommentEntity updatedComment = comment.update(dto);
        return updatedComment.getId();
    }

}
