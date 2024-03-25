package org.colcum.admin.domain.post.api;

import lombok.RequiredArgsConstructor;
import org.colcum.admin.domain.post.api.dto.PostResponseDto;
import org.colcum.admin.domain.post.application.PostService;
import org.colcum.admin.domain.post.domain.type.PostCategory;
import org.colcum.admin.domain.post.domain.type.PostStatus;
import org.colcum.admin.domain.post.domain.type.SearchType;
import org.colcum.admin.global.auth.api.ApiResponse;
import org.colcum.admin.global.auth.domain.UserContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    public ApiResponse<Page<PostResponseDto>> inquirePosts(
        @RequestParam(name = "searchType",  required = false) SearchType searchType,
        @RequestParam(name = "searchValue", required = false) String searchValue,
        @RequestParam(name = "category",    required = false) List<PostCategory> categories,
        @RequestParam(name = "status",      required = false) List<PostStatus> statuses,
        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
        @AuthenticationPrincipal UserContext user
    ) {
        if (Objects.isNull(user)) {
            throw new UsernameNotFoundException("해당 서비스는 로그인 후 사용하실 수 있습니다.");
        }
        Page<PostResponseDto> responses = postService.findByCriteria(searchType, searchValue, categories, statuses, pageable);
        return new ApiResponse<>(HttpStatus.OK.value(), "success", responses);
    }

}
