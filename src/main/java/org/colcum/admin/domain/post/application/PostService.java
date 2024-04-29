package org.colcum.admin.domain.post.application;

import lombok.RequiredArgsConstructor;
import org.colcum.admin.domain.post.api.dto.CommentCreateRequestDto;
import org.colcum.admin.domain.post.api.dto.CommentUpdateRequestDto;
import org.colcum.admin.domain.post.api.dto.EmojiCreateDto;
import org.colcum.admin.domain.post.api.dto.EmojiDeleteDto;
import org.colcum.admin.domain.post.api.dto.PostBookmarkedResponse;
import org.colcum.admin.domain.post.api.dto.PostCreateDto;
import org.colcum.admin.domain.post.api.dto.PostDetailResponseDto;
import org.colcum.admin.domain.post.api.dto.PostResponseDto;
import org.colcum.admin.domain.post.api.dto.PostSearchCondition;
import org.colcum.admin.domain.post.api.dto.PostUpdateDto;
import org.colcum.admin.domain.post.api.dto.ReceivedPostSummaryResponseDto;
import org.colcum.admin.domain.post.dao.CommentRepository;
import org.colcum.admin.domain.post.dao.EmojiReactionRepository;
import org.colcum.admin.domain.post.dao.PostRepository;
import org.colcum.admin.domain.post.dao.DirectedPostRepository;
import org.colcum.admin.domain.post.domain.CommentEntity;
import org.colcum.admin.domain.post.domain.DirectedPost;
import org.colcum.admin.domain.post.domain.EmojiReactionEntity;
import org.colcum.admin.domain.post.domain.PostEntity;
import org.colcum.admin.domain.post.domain.type.PostCategory;
import org.colcum.admin.domain.post.domain.type.PostStatus;
import org.colcum.admin.domain.post.domain.type.SearchType;
import org.colcum.admin.domain.user.dao.UserRepository;
import org.colcum.admin.domain.user.domain.UserEntity;
import org.colcum.admin.domain.user.domain.vo.Bookmark;
import org.colcum.admin.global.Error.CommentNotFoundException;
import org.colcum.admin.global.Error.EmojiNotFoundException;
import org.colcum.admin.global.Error.InvalidAuthenticationException;
import org.colcum.admin.global.Error.PostNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    private final UserRepository userRepository;

    private final CommentRepository commentRepository;

    private final DirectedPostRepository directedPostRepository;

    private final EmojiReactionRepository emojiReactionRepository;

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
        postEntity = postRepository.save(postEntity);
        if (Objects.nonNull(dto.getSendTargetUserIds()) && dto.getCategory().equals(PostCategory.DELIVERY)) {
            createDirectedPosts(dto, postEntity, user);
        }
        return postEntity;
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

    @Transactional(readOnly = true)
    public CommentEntity findCommentEntity(Long commentId) {
        return commentRepository.findByIdAndDeletedIsFalse(commentId)
            .orElseThrow(() -> new CommentNotFoundException("해당 댓글은 찾을 수 없습니다."));
    }

    @Transactional
    public Long addComment(Long postId, CommentCreateRequestDto dto, UserEntity user) {
        PostEntity post = postRepository.findByIdAndDeletedIsFalse(postId).orElseThrow(() -> {
            throw new PostNotFoundException("해당 게시글을 찾을 수 없습니다.");
        });
        CommentEntity comment = dto.toEntity(user, post);
        comment = commentRepository.save(commentRepository.save(comment));
        return comment.getId();
    }

    @Transactional
    public Long updateComment(Long commentId, CommentUpdateRequestDto dto, UserEntity user) {
        CommentEntity comment = findCommentEntity(commentId);

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new InvalidAuthenticationException("댓글 수정은 댓글 최초 작성자만 가능합니다.");
        }
        CommentEntity updatedComment = comment.update(dto);
        return updatedComment.getId();
    }

    @Transactional
    public void deleteComment(Long commentId, UserEntity user) {
        CommentEntity comment = findCommentEntity(commentId);

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new InvalidAuthenticationException("댓글 수정은 댓글 최초 작성자만 가능합니다.");
        }
        comment.delete();
        commentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public List<ReceivedPostSummaryResponseDto> findReceivedPostSummary(Long receivedUserId) {
        return directedPostRepository.findDirectedPostByReceiverId(receivedUserId);
    }

    @Transactional(readOnly = true)
    public Page<PostResponseDto> findReceivedPosts(SearchType searchType, String searchValue, List<PostStatus> statuses, UserEntity receivedUser, Pageable pageable) {
        return postRepository.searchReceivedPost(
            new PostSearchCondition(searchType, searchValue, List.of(PostCategory.DELIVERY), statuses),
            receivedUser,
            pageable
        );
    }

    @Transactional
    public Long addEmojiOnPost(Long postId, EmojiCreateDto dto, UserEntity user) {
        PostEntity post = postRepository.findByIdAndDeletedIsFalse(postId).orElseThrow(() -> {
            throw new PostNotFoundException("해당 게시글을 찾을 수 없습니다.");
        });
        EmojiReactionEntity entity = dto.toEntity(post, user);
        entity = emojiReactionRepository.save(entity);
        return entity.getId();
    }

    @Transactional
    public void removeEmojiOnPost(Long postId, UserEntity user, EmojiDeleteDto dto) {
        EmojiReactionEntity emojiReactionEntity = emojiReactionRepository.findByPostEntity_IdAndUser_IdAndContent(postId, user.getId(), dto.getContent()).orElseThrow(() -> {
            throw new EmojiNotFoundException("게시글에 등록된 이모지 중, 해당 이모지는 찾을 수 없습니다.");
        });
        emojiReactionEntity.delete();
        emojiReactionRepository.save(emojiReactionEntity);
    }

    private void createDirectedPosts(PostCreateDto dto, PostEntity post, UserEntity user) {
        for (Long targetUserId: dto.getSendTargetUserIds()) {
            DirectedPost directedPost = new DirectedPost(post, user);
            directedPostRepository.save(directedPost);
        }
    }

}
