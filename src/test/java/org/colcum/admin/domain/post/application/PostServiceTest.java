package org.colcum.admin.domain.post.application;

import org.colcum.admin.domain.post.api.dto.PostResponseDto;
import org.colcum.admin.domain.post.dao.PostRepository;
import org.colcum.admin.domain.post.domain.PostEntity;
import org.colcum.admin.domain.post.domain.type.PostCategory;
import org.colcum.admin.domain.post.domain.type.PostStatus;
import org.colcum.admin.domain.post.domain.type.SearchType;
import org.colcum.admin.domain.user.dao.UserRepository;
import org.colcum.admin.domain.user.domain.UserEntity;
import org.colcum.admin.global.util.Fixture;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.colcum.admin.global.util.Fixture.createFixtureUser;

@SpringBootTest
@ActiveProfiles("local")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    private UserEntity user;

    @BeforeAll
    void setup() {
        user = userRepository.save(createFixtureUser());
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
        Page<PostResponseDto> allPosts = postService.findByCriteria(null, null, null, statuses, page);;

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
        Page<PostResponseDto> allPosts = postService.findByCriteria(null, null, categories, null, page);;

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
        Page<PostResponseDto> nothing = postService.findByCriteria(searchType, searchValueForNothing, null, null, page);;
        Page<PostResponseDto> allPosts = postService.findByCriteria(searchType, searchValueForAll, null, null, page);;

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
        Page<PostResponseDto> nothing = postService.findByCriteria(searchType, searchValueForNothing, null, null, page);;
        Page<PostResponseDto> allPosts = postService.findByCriteria(searchType, searchValueForAll, null, null, page);;

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

}