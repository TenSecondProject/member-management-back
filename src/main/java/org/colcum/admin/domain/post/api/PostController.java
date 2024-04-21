package org.colcum.admin.domain.post.api;

import lombok.RequiredArgsConstructor;
import org.colcum.admin.domain.post.api.dto.CommentCreateRequestDto;
import org.colcum.admin.domain.post.api.dto.CommentUpdateRequestDto;
import org.colcum.admin.domain.post.api.dto.PostBookmarkedResponse;
import org.colcum.admin.domain.post.api.dto.PostCreateDto;
import org.colcum.admin.domain.post.api.dto.PostDetailResponseDto;
import org.colcum.admin.domain.post.api.dto.PostResponseDto;
import org.colcum.admin.domain.post.api.dto.PostUpdateDto;
import org.colcum.admin.domain.post.api.dto.ReceivedPostSummaryResponseDto;
import org.colcum.admin.domain.post.application.PostService;
import org.colcum.admin.domain.post.domain.PostEntity;
import org.colcum.admin.domain.post.domain.type.PostCategory;
import org.colcum.admin.domain.post.domain.type.PostStatus;
import org.colcum.admin.domain.post.domain.type.SearchType;
import org.colcum.admin.global.Error.InvalidAuthenticationException;
import org.colcum.admin.global.auth.jwt.JwtAuthentication;
import org.colcum.admin.global.common.api.dto.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public ApiResponse<Page<PostResponseDto>> inquirePosts(
        @RequestParam(name = "searchType",  required = false) SearchType searchType,
        @RequestParam(name = "searchValue", required = false) String searchValue,
        @RequestParam(name = "category",    required = false) List<PostCategory> categories,
        @RequestParam(name = "status",      required = false) List<PostStatus> statuses,
        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
        @AuthenticationPrincipal JwtAuthentication authentication
    ) {
        if (Objects.isNull(authentication)) {
            throw new InvalidAuthenticationException("해당 서비스는 로그인 후 사용하실 수 있습니다.");
        }
        if (Objects.nonNull(categories) && categories.contains(PostCategory.DELIVERY)) {
            throw new IllegalArgumentException("공지사항에는 Direct Post가 조회되지 않습니다.");
        }
        Page<PostResponseDto> responses = postService.findByCriteria(searchType, searchValue, categories, statuses, pageable);
        return new ApiResponse<>(HttpStatus.OK.value(), "success", responses);
    }

    @GetMapping("/{postId}")
    @ResponseStatus(value = HttpStatus.OK)
    public ApiResponse<PostDetailResponseDto> inquirePostDetail(
        @PathVariable(name = "postId", required = true) Long postId,
        @AuthenticationPrincipal JwtAuthentication authentication
    ) {
        if (Objects.isNull(authentication)) {
            throw new InvalidAuthenticationException("해당 서비스는 로그인 후 사용하실 수 있습니다.");
        }
        PostDetailResponseDto response = postService.inquirePostDetail(postId);

        return new ApiResponse<>(HttpStatus.OK.value(), "success", response);
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public ApiResponse<String> createPost(
        @RequestBody PostCreateDto dto,
        @AuthenticationPrincipal JwtAuthentication authentication
    ) {
        if (Objects.isNull(authentication)) {
            throw new InvalidAuthenticationException("해당 서비스는 로그인 후 사용하실 수 있습니다.");
        }
        postService.createPost(dto, authentication.userEntity);
        return new ApiResponse<>(HttpStatus.CREATED.value(), "created", null);
    }

    @PutMapping("/{postId}")
    @ResponseStatus(value = HttpStatus.OK)
    public ApiResponse<PostUpdateDto> updatePost(
        @PathVariable(value = "postId") Long postId,
        @RequestBody PostUpdateDto dto,
        @AuthenticationPrincipal JwtAuthentication authentication
    ) {
        if (Objects.isNull(authentication)) {
            throw new InvalidAuthenticationException("해당 서비스는 로그인 후 사용하실 수 있습니다.");
        }
        PostUpdateDto response = postService.updatePost(postId, dto, authentication.userEntity);
        return new ApiResponse<>(HttpStatus.OK.value(), "success", response);
    }

    @DeleteMapping("/{postId}")
    @ResponseStatus(value = HttpStatus.OK)
    public ApiResponse<Void> deletePost(
        @PathVariable(value = "postId") Long postId,
        @AuthenticationPrincipal JwtAuthentication authentication
    ) {
        if (Objects.isNull(authentication)) {
            throw new InvalidAuthenticationException("해당 서비스는 로그인 후 사용하실 수 있습니다.");
        }
        postService.deletePost(postId, authentication.userEntity);
        return new ApiResponse<>(HttpStatus.OK.value(), "success", null);
    }

    @GetMapping("/bookmarks")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<PostBookmarkedResponse>> inquirePostsWithBookmarked(
        @AuthenticationPrincipal JwtAuthentication authentication
    ) {
        if (Objects.isNull(authentication)) {
            throw new InvalidAuthenticationException("해당 서비스는 로그인 후 사용하실 수 있습니다.");
        }
        List<PostBookmarkedResponse> responses = postService.findBookmarkedPosts(authentication.userEntity);
        return new ApiResponse<>(HttpStatus.OK.value(), "success", responses);
    }

    @PostMapping("/{postId}/bookmarks")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> addBookmark(
        @PathVariable(value = "postId") Long postId,
        @AuthenticationPrincipal JwtAuthentication authentication
    ) {
        if (Objects.isNull(authentication)) {
            throw new InvalidAuthenticationException("해당 서비스는 로그인 후 사용하실 수 있습니다.");
        }
        postService.addBookmark(postId, authentication.userEntity);
        return new ApiResponse<>(HttpStatus.OK.value(), "success", null);
    }

    @DeleteMapping("/{postId}/bookmarks")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> removeBookmark(
        @PathVariable(value = "postId") Long postId,
        @AuthenticationPrincipal JwtAuthentication authentication
    ) {
        if (Objects.isNull(authentication)) {
            throw new InvalidAuthenticationException("해당 서비스는 로그인 후 사용하실 수 있습니다.");
        }
        postService.removeBookmark(postId, authentication.userEntity);
        return new ApiResponse<>(HttpStatus.OK.value(), "success", null);
    }

    @PostMapping("/{postId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Long> addComment(
        @PathVariable(value = "postId") Long postId,
        @RequestBody CommentCreateRequestDto dto,
        @AuthenticationPrincipal JwtAuthentication authentication
    ) {
        if (Objects.isNull(authentication)) {
            throw new InvalidAuthenticationException("해당 서비스는 로그인 후 사용하실 수 있습니다.");
        }
        Long commentId = postService.addComment(postId, dto, authentication.userEntity);
        return new ApiResponse<>(HttpStatus.CREATED.value(), "created", commentId);
    }

    @PutMapping("/{postId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Long> updatedComment(
        @PathVariable(value = "postId") Long postId,
        @PathVariable(value = "commentId") Long commentId,
        @RequestBody CommentUpdateRequestDto dto,
        @AuthenticationPrincipal JwtAuthentication authentication
    ) {
        if (Objects.isNull(authentication)) {
            throw new InvalidAuthenticationException("해당 서비스는 로그인 후 사용하실 수 있습니다.");
        }
        Long updateCommentId = postService.updateComment(commentId, dto, authentication.userEntity);
        return new ApiResponse<>(HttpStatus.OK.value(), "success", updateCommentId);
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> deleteComment(
        @PathVariable(value = "postId") Long postId,
        @PathVariable(value = "commentId") Long commentId,
        @AuthenticationPrincipal JwtAuthentication authentication
    ) {
        if (Objects.isNull(authentication)) {
            throw new InvalidAuthenticationException("해당 서비스는 로그인 후 사용하실 수 있습니다.");
        }
        postService.deleteComment(commentId, authentication.userEntity);
        return new ApiResponse<>(HttpStatus.OK.value(), "success", null);
    }

    @GetMapping("/received/summary")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<ReceivedPostSummaryResponseDto>> getReceivedPostSummary(
        @AuthenticationPrincipal JwtAuthentication authentication
    ) {
        if (Objects.isNull(authentication)) {
            throw new InvalidAuthenticationException("해당 서비스는 로그인 후 사용하실 수 있습니다.");
        }
        List<ReceivedPostSummaryResponseDto> dtos = postService.findReceivedPostSummary(authentication.userEntity.getId());
        return new ApiResponse<>(HttpStatus.OK.value(), "success", dtos);
    }

    @GetMapping("/received")
    @ResponseStatus(value = HttpStatus.OK)
    public ApiResponse<Page<PostResponseDto>> inquireReceivedPosts(
        @RequestParam(name = "searchType",  required = false) SearchType searchType,
        @RequestParam(name = "searchValue", required = false) String searchValue,
        @RequestParam(name = "status",      required = false) List<PostStatus> statuses,
        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
        @AuthenticationPrincipal JwtAuthentication authentication
    ) {
        if (Objects.isNull(authentication)) {
            throw new InvalidAuthenticationException("해당 서비스는 로그인 후 사용하실 수 있습니다.");
        }
        Page<PostResponseDto> responses = postService.findReceivedPosts(searchType, searchValue, statuses, authentication.userEntity, pageable);
        return new ApiResponse<>(HttpStatus.OK.value(), "success", responses);
    }

}
