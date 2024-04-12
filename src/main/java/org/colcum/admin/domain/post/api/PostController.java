package org.colcum.admin.domain.post.api;

import lombok.RequiredArgsConstructor;
import org.colcum.admin.domain.post.api.dto.PostCreateDto;
import org.colcum.admin.domain.post.api.dto.PostDetailResponseDto;
import org.colcum.admin.domain.post.api.dto.PostResponseDto;
import org.colcum.admin.domain.post.application.PostService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
        Page<PostResponseDto> responses = postService.findByCriteria(searchType, searchValue, categories, statuses, pageable);
        return new ApiResponse<>(HttpStatus.OK.value(), "success", responses);
    }

    @GetMapping
    @RequestMapping("/{postId}")
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
    @RequestMapping
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

}
