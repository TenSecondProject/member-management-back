package org.colcum.admin.domain.post.application;

import org.colcum.admin.domain.post.api.dto.CommentResponseDto;
import org.colcum.admin.domain.post.api.dto.EmojiResponseDto;
import org.colcum.admin.domain.post.api.dto.PostBookmarkedResponse;
import org.colcum.admin.domain.post.api.dto.PostCreateDto;
import org.colcum.admin.domain.post.api.dto.PostDetailResponseDto;
import org.colcum.admin.domain.post.api.dto.PostResponseDto;
import org.colcum.admin.domain.post.api.dto.PostUpdateDto;
import org.colcum.admin.domain.post.dao.PostRepository;
import org.colcum.admin.domain.post.domain.PostEntity;
import org.colcum.admin.domain.post.domain.type.PostCategory;
import org.colcum.admin.domain.post.domain.type.PostStatus;
import org.colcum.admin.domain.post.domain.type.SearchType;
import org.colcum.admin.domain.user.dao.UserRepository;
import org.colcum.admin.domain.user.domain.UserEntity;
import org.colcum.admin.domain.user.domain.vo.Bookmark;
import org.colcum.admin.global.Error.PostNotFoundException;
import org.colcum.admin.global.util.Fixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.colcum.admin.global.util.Fixture.createFixtureUser;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("local")
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    private UserEntity user;

    @BeforeEach
    void setup() {
        user = userRepository.save(createFixtureUser());
    }

    @AfterEach
    void clean() {
        userRepository.deleteAll();
        postRepository.deleteAll();
    }

    @Test
    @DisplayName("게시글을 조회한다.")
    void inquirePost() {
        // given
        PostEntity post1 = Fixture.createFixturePost("title", "content1", user);
        PostEntity post2 = Fixture.createFixturePost("title", "content2", user);
        PostEntity post3 = Fixture.createFixturePost("title", "content3", user);
        postRepository.save(post1);
        postRepository.save(post2);
        postRepository.save(post3);

        SearchType searchType = SearchType.TITLE;
        String searchValue = "title";
        List<PostCategory> categories = List.of(PostCategory.ANNOUNCEMENT, PostCategory.DELIVERY);
        List<PostStatus> status = List.of(PostStatus.UNCOMPLETED, PostStatus.COMPLETE);
        PageRequest pageRequest = PageRequest.ofSize(10);

        // when
        Page<PostResponseDto> posts = postService.findByCriteria(searchType, searchValue, categories, status, pageRequest);

        // then
        assertThat(posts.getContent().size()).isEqualTo(3);
        assertThat(posts.getSize()).isEqualTo(10);
        assertThat(posts.getContent()).contains(PostResponseDto.from(post1), PostResponseDto.from(post2), PostResponseDto.from(post3));
    }

    @Test
    @DisplayName("게시글 상태로 게시글을 조회한다.")
    void inquirePostsWithPostStatus() {
        // given
        int NUMBER_OF_POST = 3;
        PostEntity post1 = Fixture.createFixturePost("title", "content1", user);
        PostEntity post2 = Fixture.createFixturePost("title", "content2", user);
        PostEntity post3 = Fixture.createFixturePost("title", "content3", user);
        postRepository.save(post1);
        postRepository.save(post2);
        postRepository.save(post3);

        List<PostStatus> searchStandardForNothing = Collections.singletonList(PostStatus.COMPLETE);
        List<PostStatus> searchStandardForAllPosts = Collections.singletonList(PostStatus.UNCOMPLETED);
        PageRequest page = PageRequest.of(0, 10);

        // when
        Page<PostResponseDto> nothing = postService.findByCriteria(null, null, null, searchStandardForNothing, page);
        Page<PostResponseDto> allPosts = postService.findByCriteria(null, null, null, searchStandardForAllPosts, page);

        // then
        assertThat(nothing.getContent().size()).isEqualTo(0);
        assertThat(allPosts.getContent().size()).isEqualTo(NUMBER_OF_POST);
        assertThat(allPosts.getContent()).contains(PostResponseDto.from(post1), PostResponseDto.from(post2), PostResponseDto.from(post3));
    }

    @Test
    @DisplayName("2개 이상의 게시글 상태로 게시글을 조회한다.")
    void inquirePostsWithPluralPostStatus() {
        // given
        int NUMBER_OF_POST = 3;
        PostEntity post1 = Fixture.createFixturePost("title", "content1", user);
        PostEntity post2 = Fixture.createFixturePost("title", "content2", user);
        PostEntity post3 = Fixture.createFixturePost("title", "content3", user);
        postRepository.save(post1);
        postRepository.save(post2);
        postRepository.save(post3);

        List<PostStatus> statuses = List.of(PostStatus.COMPLETE, PostStatus.UNCOMPLETED, PostStatus.IN_PROGRESS);
        PageRequest page = PageRequest.of(0, 10);

        // when
        Page<PostResponseDto> allPosts = postService.findByCriteria(null, null, null, statuses, page);

        // then
        assertThat(allPosts.getContent().size()).isEqualTo(NUMBER_OF_POST);
        assertThat(allPosts.getContent()).contains(PostResponseDto.from(post1), PostResponseDto.from(post2), PostResponseDto.from(post3));
    }

    @Test
    @DisplayName("게시글 종류로 게시글을 조회한다.")
    void inquirePostsWithPostCategory() {
        // given
        int NUMBER_OF_POST = 3;
        PostEntity post1 = Fixture.createFixturePost("title", "content1", user);
        PostEntity post2 = Fixture.createFixturePost("title", "content2", user);
        PostEntity post3 = Fixture.createFixturePost("title", "content3", user);
        postRepository.save(post1);
        postRepository.save(post2);
        postRepository.save(post3);

        List<PostCategory> searchStandardForNothing = Collections.singletonList(PostCategory.DELIVERY);
        List<PostCategory> searchStandardForAllPosts = Collections.singletonList(PostCategory.ANNOUNCEMENT);
        PageRequest page = PageRequest.of(0, 10);

        // when
        Page<PostResponseDto> nothing = postService.findByCriteria(null, null, searchStandardForNothing, null, page);
        Page<PostResponseDto> allPosts = postService.findByCriteria(null, null, searchStandardForAllPosts, null, page);

        // then
        assertThat(nothing.getContent().size()).isEqualTo(0);
        assertThat(allPosts.getContent().size()).isEqualTo(NUMBER_OF_POST);
        assertThat(allPosts.getContent()).contains(PostResponseDto.from(post1), PostResponseDto.from(post2), PostResponseDto.from(post3));
    }

    @Test
    @DisplayName("2개 이상의 게시글 종류로 게시글을 조회한다.")
    void inquirePostsWithPluralPostCategory() {
        // given
        int NUMBER_OF_POST = 3;
        PostEntity post1 = Fixture.createFixturePost("title", "content1", user);
        PostEntity post2 = Fixture.createFixturePost("title", "content2", user);
        PostEntity post3 = Fixture.createFixturePost("title", "content3", user);
        postRepository.save(post1);
        postRepository.save(post2);
        postRepository.save(post3);

        List<PostCategory> categories = List.of(PostCategory.ANNOUNCEMENT, PostCategory.DELIVERY, PostCategory.ANNOUNCEMENT);
        PageRequest page = PageRequest.of(0, 10);

        // when
        Page<PostResponseDto> allPosts = postService.findByCriteria(null, null, categories, null, page);

        // then
        assertThat(allPosts.getContent().size()).isEqualTo(NUMBER_OF_POST);
        assertThat(allPosts.getContent()).contains(PostResponseDto.from(post1), PostResponseDto.from(post2), PostResponseDto.from(post3));
    }

    @Test
    @DisplayName("제목으로 검색하여 게시글을 조회한다.")
    void searchWithTitle() {
        // given
        int NUMBER_OF_POST = 3;
        PostEntity post1 = Fixture.createFixturePost("title1", "content1", user);
        PostEntity post2 = Fixture.createFixturePost("title2", "content2", user);
        PostEntity post3 = Fixture.createFixturePost("title3", "content3", user);
        postRepository.save(post1);
        postRepository.save(post2);
        postRepository.save(post3);

        SearchType searchType = SearchType.TITLE;
        String searchValueForNothing = "nothing";
        String searchValueForAll = "title";
        PageRequest page = PageRequest.of(0, 10);

        // when
        Page<PostResponseDto> nothing = postService.findByCriteria(searchType, searchValueForNothing, null, null, page);
        Page<PostResponseDto> allPosts = postService.findByCriteria(searchType, searchValueForAll, null, null, page);

        // then
        assertThat(nothing.getContent().size()).isEqualTo(0);
        assertThat(allPosts.getContent().size()).isEqualTo(NUMBER_OF_POST);
        assertThat(allPosts.getContent()).contains(PostResponseDto.from(post1), PostResponseDto.from(post2), PostResponseDto.from(post3));
    }

    @Test
    @DisplayName("내용으로 검색하여 게시글을 조회한다.")
    void searchWithContent() {
        // given
        int NUMBER_OF_POST = 3;
        PostEntity post1 = Fixture.createFixturePost("title1", "content1", user);
        PostEntity post2 = Fixture.createFixturePost("title2", "content2", user);
        PostEntity post3 = Fixture.createFixturePost("title3", "content3", user);
        postRepository.save(post1);
        postRepository.save(post2);
        postRepository.save(post3);

        SearchType searchType = SearchType.CONTENT;
        String searchValueForNothing = "nothing";
        String searchValueForAll = "content";
        PageRequest page = PageRequest.of(0, 10);

        // when
        Page<PostResponseDto> nothing = postService.findByCriteria(searchType, searchValueForNothing, null, null, page);
        Page<PostResponseDto> allPosts = postService.findByCriteria(searchType, searchValueForAll, null, null, page);

        // then
        assertThat(nothing.getContent().size()).isEqualTo(0);
        assertThat(allPosts.getContent().size()).isEqualTo(NUMBER_OF_POST);
        assertThat(allPosts.getContent()).contains(PostResponseDto.from(post1), PostResponseDto.from(post2), PostResponseDto.from(post3));
    }

    @Test
    @DisplayName("작성자로 검색하여 게시글을 조회한다.")
    void searchWithWriter() {
        // given
        int NUMBER_OF_POST = 3;
        PostEntity post1 = Fixture.createFixturePost("title1", "content1", user);
        PostEntity post2 = Fixture.createFixturePost("title2", "content2", user);
        PostEntity post3 = Fixture.createFixturePost("title3", "content3", user);
        postRepository.save(post1);
        postRepository.save(post2);
        postRepository.save(post3);

        SearchType searchType = SearchType.WRITTEN_USER;
        String searchValueForNothing = "nothing";
        String searchValueForAll = "unknown";
        PageRequest page = PageRequest.of(0, 10);

        // when
        Page<PostResponseDto> nothing = postService.findByCriteria(searchType, searchValueForNothing, null, null, page);
        Page<PostResponseDto> allPosts = postService.findByCriteria(searchType, searchValueForAll, null, null, page);

        // then
        assertThat(nothing.getContent().size()).isEqualTo(0);
        assertThat(allPosts.getContent().size()).isEqualTo(NUMBER_OF_POST);
        assertThat(allPosts.getContent()).contains(PostResponseDto.from(post1), PostResponseDto.from(post2), PostResponseDto.from(post3));
    }

    @Test
    @DisplayName("게시글 상세페이지를 조회한다.")
    void inquirePostDetail() {
        // given
        PostEntity post = Fixture.createFixturePost("title1", "content1", user);
        post = postRepository.save(post);

        // when
        PostDetailResponseDto response = postService.inquirePostDetail(post.getId());

        // then
        assertThat(post.getId()).isEqualTo(response.getId());
        assertThat(post.getTitle()).isEqualTo(response.getTitle());
        assertThat(post.getContent()).isEqualTo(response.getContent());
        assertThat(post.getCategory()).isEqualTo(response.getCategory());
        assertThat(post.getStatus()).isEqualTo(response.getStatus());
        assertThat(post.getExpiredDate()).isEqualTo(response.getExpiredDate());
        assertThat(post.getUser().getName()).isEqualTo(response.getWrittenBy());
        assertThat(post.getCommentEntities().stream().map(CommentResponseDto::from).toList()).isEqualTo(response.getCommentResponseDtos());
        assertThat(EmojiResponseDto.from(post.getEmojiReactionEntities())).isEqualTo(response.getEmojiResponseDtos());
    }

    @Test
    @DisplayName("게시글을 생성한다.")
    void createPost() {
        // given
        PostCreateDto dto = new PostCreateDto("title", "content", PostCategory.ANNOUNCEMENT, PostStatus.COMPLETE, null);

        // when
        PostEntity result = postService.createPost(dto, user);

        // then
        assertThat(result.getTitle()).isEqualTo(dto.getTitle());
        assertThat(result.getContent()).isEqualTo(dto.getContent());
        assertThat(result.getCategory()).isEqualTo(dto.getCategory());
        assertThat(result.getStatus()).isEqualTo(dto.getStatus());
        assertThat(result.getExpiredDate()).isEqualTo(dto.getExpiredDate());
        assertThat(result.getUser()).isEqualTo(user);
    }

    @Test
    @DisplayName("게시글을 수정한다.")
    void updatePost() {
        // given
        PostEntity post = Fixture.createFixturePost("title1", "content1", user);
        post = postRepository.save(post);
        Long postId = post.getId();
        PostUpdateDto requestDto = new PostUpdateDto("updatedTitle", "updatedContent", PostStatus.COMPLETE, LocalDateTime.now());

        // when
        PostUpdateDto responseDto = postService.updatePost(postId, requestDto, user);
        PostEntity updatedPost = postRepository.findById(post.getId()).orElseThrow(PostNotFoundException::new);

        // then
        assertThat(responseDto.getTitle()).isEqualTo(updatedPost.getTitle());
        assertThat(responseDto.getContent()).isEqualTo(updatedPost.getContent());
        assertThat(responseDto.getStatus()).isEqualTo(updatedPost.getStatus());
        assertThat(responseDto.getExpiredDate().truncatedTo(ChronoUnit.MILLIS))
            .isEqualTo(updatedPost.getExpiredDate().truncatedTo(ChronoUnit.MILLIS));
    }

    @Test
    @DisplayName("게시글을 삭제한다.")
    void deletePost() {
        // given
        PostEntity post = Fixture.createFixturePost("title1", "content1", user);
        post = postRepository.save(post);
        Long postId = post.getId();

        // when
        postService.deletePost(postId, user);

        // then
        assertThrows(PostNotFoundException.class, () -> postService.inquirePostDetail(postId));
    }

    @Test
    @DisplayName("북마크 된 게시글을 조회한다.")
    void inquirePostsWithBookmarked() {
        // given
        PostEntity post1 = Fixture.createFixturePost("title1", "content1", user);
        PostEntity post2 = Fixture.createFixturePost("title2", "content2", user);
        PostEntity post3 = Fixture.createFixturePost("title3", "content3", user);

        post1 = postRepository.save(post1);
        postRepository.save(post2);
        postRepository.save(post3);

        // when
        user.addBookmark(new Bookmark(post1.getId()));
        user = userRepository.save(user);
        List<PostBookmarkedResponse> responses = postService.findBookmarkedPosts(user);

        // then
        assertThat(responses).containsExactly(PostBookmarkedResponse.from(post1));
        assertThat(responses).doesNotContain(PostBookmarkedResponse.from(post2));
        assertThat(responses).doesNotContain(PostBookmarkedResponse.from(post3));
    }
    
}