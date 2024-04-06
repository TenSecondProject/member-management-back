package org.colcum.admin.domain.post.api;

import org.colcum.admin.domain.post.api.dto.CommentResponseDto;
import org.colcum.admin.domain.post.api.dto.EmojiResponseDto;
import org.colcum.admin.domain.post.api.dto.PostDetailResponseDto;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
                3,
                List.of(EmojiResponseDto.of("\uD83D\uDE00", 1, List.of("tester2")))
            )
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
                3,
                List.of(EmojiResponseDto.of("\uD83D\uDE00", 1, List.of("tester2")))
            )
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

    @Test
    @DisplayName("게시글 상세 페이지를 조회한다")
    @WithMockJwtAuthentication
    void inquirePostDetail() throws Exception {
        Long postId = 1L;
        LocalDateTime now = LocalDateTime.now();
        // given
        PostDetailResponseDto dtos = PostDetailResponseDto.of(
            postId,
            "title",
            "content",
            PostCategory.ANNOUNCEMENT,
            PostStatus.COMPLETE,
            false,
            now.toLocalDate(),
            "tester",
            now,
            "tester",
            List.of(CommentResponseDto.of("commentTester", now.toLocalDate(), "commentContent")),
            List.of(EmojiResponseDto.of("\uD83D\uDE00", 1, List.of("tester2")))
        );

        // when
        when(postService.inquirePostDetail(postId)).thenReturn(dtos);

        // then
        this.mockMvc
            .perform(
                get("/api/v1/posts/{postId}", postId).accept(MediaType.APPLICATION_JSON)
            )
            .andExpectAll(
                status().isOk(),
                jsonPath("$.data.id").value(postId),
                jsonPath("$.data.title").value("title"),
                jsonPath("$.data.content").value("content"),
                jsonPath("$.data.category").value(PostCategory.ANNOUNCEMENT.name()),
                jsonPath("$.data.status").value(PostStatus.COMPLETE.name()),
                jsonPath("$.data.bookmarked").value(false),
                jsonPath("$.data.expiredDate").value(now.format(DateTimeFormatter.ofPattern("yy/MM/dd"))),
                jsonPath("$.data.writtenBy").value("tester"),
                jsonPath("$.data.createdAt").value(now.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"))),
                jsonPath("$.data.username").value("tester"),
                jsonPath("$.data.commentResponseDtos[0].writtenBy").value("commentTester"),
                jsonPath("$.data.commentResponseDtos[0].writtenDate").value(now.format(DateTimeFormatter.ofPattern("yy/MM/dd"))),
                jsonPath("$.data.commentResponseDtos[0].content").value("commentContent"),
                jsonPath("$.data.emojiResponseDtos[0].emoji").value("\uD83D\uDE00"),
                jsonPath("$.data.emojiResponseDtos[0].totalCount").value(1),
                jsonPath("$.data.emojiResponseDtos[0].usernames[0]").value("tester2")
            )
            .andDo(document("posts"))
            .andDo(print());

    }

}