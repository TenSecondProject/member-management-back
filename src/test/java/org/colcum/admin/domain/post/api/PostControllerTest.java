package org.colcum.admin.domain.post.api;

import org.colcum.admin.domain.post.api.dto.PostResponseDto;
import org.colcum.admin.domain.post.application.PostService;
import org.colcum.admin.domain.post.domain.type.PostCategory;
import org.colcum.admin.domain.post.domain.type.PostStatus;
import org.colcum.admin.domain.post.domain.type.SearchType;
import org.colcum.admin.global.auth.WithMockJwtAuthentication;
import org.colcum.admin.global.common.AbstractRestDocsTest;
import org.colcum.admin.global.common.IsNullOrType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;

import java.util.List;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
class PostControllerTest extends AbstractRestDocsTest {

    @MockBean
    PostService postService;

    @Test
    @DisplayName("조회 조건 없이 게시글을 조회한다")
    @WithMockJwtAuthentication
    void inquirePostsWithoutParameters() throws Exception {
        // given
        Long postId = 1L;
        List<PostResponseDto> dtos = List.of(
            PostResponseDto.of(
                postId,
                "title",
                "content",
                PostStatus.IN_PROGRESS,
                "tester",
                false,
                3)
        );
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<PostResponseDto> page = new PageImpl<>(dtos, pageRequest, dtos.size());

        // when
        when(
            postService.findByCriteria(
                argThat(new IsNullOrType<>(SearchType.class)),
                argThat(new IsNullOrType<>(String.class)),
                argThat(new IsNullOrType<>(List.class)),
                argThat(new IsNullOrType<>(List.class)),
                argThat(new IsNullOrType<>(Pageable.class))
            )
        ).thenReturn(page);

        // then
        this.mockMvc
            .perform(
                get("/api/v1/posts").accept(MediaType.APPLICATION_JSON)
                    .param("searchType", (String) null)
                    .param("searchValue", (String) null)
                    .param("postStatus", (String) null)
                    .param("postCategory", (String) null)
                    .param("page", "0")
                    .param("size", "10")
            )
            .andExpectAll(
                status().isOk(),
                jsonPath("$.data.content[0].id").value(postId),
                jsonPath("$.data.content[0].title").value("title"),
                jsonPath("$.data.content[0].content").value("content"),
                jsonPath("$.data.content[0].status").value(PostStatus.IN_PROGRESS.name()),
                jsonPath("$.data.content[0].writtenBy").value("tester"),
                jsonPath("$.data.content[0].commentCount").value(3),
                jsonPath("$.data.content[0].bookmarked").value(false)
             )
            .andDo(document("posts"))
            .andDo(print())
            .andReturn();
    }

    @Test
    @DisplayName("조회 조건으로 게시글을 조회한다")
    @WithMockJwtAuthentication
    void inquirePostsWithParameters() throws Exception {
        // given
        Long postId = 1L;
        List<PostResponseDto> dtos = List.of(
            PostResponseDto.of(
                postId,
                "title",
                "content",
                PostStatus.IN_PROGRESS,
                "tester",
                false,
                3)
        );
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<PostResponseDto> page = new PageImpl<>(dtos, pageRequest, dtos.size());

        // when
        when(
            postService.findByCriteria(
                argThat(new IsNullOrType<>(SearchType.class)),
                argThat(new IsNullOrType<>(String.class)),
                argThat(new IsNullOrType<>(List.class)),
                argThat(new IsNullOrType<>(List.class)),
                argThat(new IsNullOrType<>(Pageable.class))
            )
        ).thenReturn(page);

        // then
        this.mockMvc
            .perform(
                get("/api/v1/posts").accept(MediaType.APPLICATION_JSON)
                    .param("searchType", SearchType.TITLE.name())
                    .param("searchValue", "title")
                    .param("postStatus", PostStatus.IN_PROGRESS.name())
                    .param("postCategory", PostCategory.DELIVERY.name())
                    .param("page", "0")
                    .param("size", "10")
            )
            .andExpectAll(
                status().isOk(),
                jsonPath("$.data.content[0].id").value(postId),
                jsonPath("$.data.content[0].title").value("title"),
                jsonPath("$.data.content[0].content").value("content"),
                jsonPath("$.data.content[0].status").value(PostStatus.IN_PROGRESS.name()),
                jsonPath("$.data.content[0].writtenBy").value("tester"),
                jsonPath("$.data.content[0].commentCount").value(3),
                jsonPath("$.data.content[0].bookmarked").value(false)
            )
            .andDo(document("posts"))
            .andDo(print())
            .andReturn();
    }

}