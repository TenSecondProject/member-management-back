package org.colcum.admin.domain.post.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.colcum.admin.domain.post.api.dto.CommentResponseDto;
import org.colcum.admin.domain.post.api.dto.EmojiResponseDto;
import org.colcum.admin.domain.post.api.dto.PostCreateDto;
import org.colcum.admin.domain.post.api.dto.PostDetailResponseDto;
import org.colcum.admin.domain.post.api.dto.PostResponseDto;
import org.colcum.admin.domain.post.api.dto.PostUpdateDto;
import org.colcum.admin.domain.post.application.PostService;
import org.colcum.admin.domain.post.domain.type.PostCategory;
import org.colcum.admin.domain.post.domain.type.PostStatus;
import org.colcum.admin.domain.post.domain.type.SearchType;
import org.colcum.admin.domain.user.domain.UserEntity;
import org.colcum.admin.global.auth.WithMockJwtAuthentication;
import org.colcum.admin.global.common.AbstractRestDocsTest;
import org.colcum.admin.global.common.IsNullOrType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.colcum.admin.global.util.Fixture.createFixtureUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
class PostControllerTest extends AbstractRestDocsTest {

    @MockBean
    PostService postService;

    @Autowired
    ObjectMapper objectMapper;

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
            .andDo(print())
            .andDo(document("posts", queryParameters(
                parameterWithName("searchType").description("검색의 유형으로, Title, content, writtenBy가 존재"),
                parameterWithName("searchValue").description("검색에 쓰일 내용"),
                parameterWithName("postStatus").description("게시글의 상태, Complete, InProgress, UnComplete가 존재"),
                parameterWithName("postCategory").description("게시글의 종류, Announcement, Delivery, Etc가 존재"),
                parameterWithName("page").description("Page 번호"),
                parameterWithName("size").description("Page size")
            )))
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
            now,
            "tester",
            now,
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
                jsonPath("$.data.expiredDate").value(now.format(DateTimeFormatter.ofPattern("yy/MM/dd HH:mm"))),
                jsonPath("$.data.writtenBy").value("tester"),
                jsonPath("$.data.createdAt").value(now.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"))),
                jsonPath("$.data.commentResponseDtos[0].writtenBy").value("commentTester"),
                jsonPath("$.data.commentResponseDtos[0].writtenDate").value(now.format(DateTimeFormatter.ofPattern("yy/MM/dd"))),
                jsonPath("$.data.commentResponseDtos[0].content").value("commentContent"),
                jsonPath("$.data.emojiResponseDtos[0].emoji").value("\uD83D\uDE00"),
                jsonPath("$.data.emojiResponseDtos[0].totalCount").value(1),
                jsonPath("$.data.emojiResponseDtos[0].usernames[0]").value("tester2")
            )
            .andDo(document("posts", pathParameters(
                parameterWithName("postId").description("게시글의 ID")
            )))
            .andDo(print());
    }

    @Test
    @DisplayName("게시글을 생성한다")
    @WithMockJwtAuthentication
    void createPost() throws Exception {
        // given
        PostCreateDto dto = new PostCreateDto(
            "title",
            "content",
            PostCategory.ANNOUNCEMENT,
            PostStatus.IN_PROGRESS,
            LocalDateTime.now()
        );

        UserEntity user = createFixtureUser();

        // when
        when(postService.createPost(dto, user)).thenReturn(null);

        // then
        this.mockMvc
            .perform(
                post("/api/v1/posts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))
            .andExpectAll(
                status().isCreated(),
                jsonPath("$.statusCode").value(HttpStatus.CREATED.value()),
                jsonPath("$.message").value("created"),
                jsonPath("$.data").value(Matchers.nullValue())
            )
            .andDo(print());
    }

    @Test
    @DisplayName("게시글을 수정한다")
    @WithMockJwtAuthentication
    void updatePost() throws Exception {
        // given
        Long postId = 1L;
        PostUpdateDto postUpdateDto = new PostUpdateDto("updatedTitle", "updatedContent", PostStatus.COMPLETE, LocalDateTime.now());
        UserEntity user = createFixtureUser();

        // when
        when(postService.updatePost(eq(postId), any(PostUpdateDto.class), eq(user))).thenReturn(postUpdateDto);

        // then
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        this.mockMvc
            .perform(
                put("/api/v1/posts/" + postId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(postUpdateDto)))
            .andExpectAll(
                status().isOk(),
                jsonPath("$.statusCode").value(HttpStatus.OK.value()),
                jsonPath("$.message").value("success"),
                jsonPath("$.data.title").value(postUpdateDto.getTitle()),
                jsonPath("$.data.content").value(postUpdateDto.getContent()),
                jsonPath("$.data.status").value(postUpdateDto.getStatus().name()),
                jsonPath("$.data.expiredDate").value(formatter.format(postUpdateDto.getExpiredDate()))
            );

    }

}