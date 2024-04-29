package org.colcum.admin.domain.post.application;

import org.colcum.admin.domain.post.api.dto.CommentCreateRequestDto;
import org.colcum.admin.domain.post.api.dto.CommentResponseDto;
import org.colcum.admin.domain.post.api.dto.CommentUpdateRequestDto;
import org.colcum.admin.domain.post.api.dto.EmojiCreateDto;
import org.colcum.admin.domain.post.api.dto.EmojiResponseDto;
import org.colcum.admin.domain.post.api.dto.PostBookmarkedResponse;
import org.colcum.admin.domain.post.api.dto.PostCreateDto;
import org.colcum.admin.domain.post.api.dto.PostDetailResponseDto;
import org.colcum.admin.domain.post.api.dto.PostResponseDto;
import org.colcum.admin.domain.post.api.dto.PostUpdateDto;
import org.colcum.admin.domain.post.api.dto.ReceivedPostSummaryResponseDto;
import org.colcum.admin.domain.post.dao.CommentRepository;
import org.colcum.admin.domain.post.dao.EmojiReactionRepository;
import org.colcum.admin.domain.post.dao.PostRepository;
import org.colcum.admin.domain.post.domain.CommentEntity;
import org.colcum.admin.domain.post.domain.DirectedPost;
import org.colcum.admin.domain.post.domain.EmojiReactionEntity;
import org.colcum.admin.domain.post.domain.PostEntity;
import org.colcum.admin.domain.post.dao.DirectedPostRepository;
import org.colcum.admin.domain.post.domain.type.PostCategory;
import org.colcum.admin.domain.post.domain.type.PostStatus;
import org.colcum.admin.domain.post.domain.type.SearchType;
import org.colcum.admin.domain.user.dao.UserRepository;
import org.colcum.admin.domain.user.domain.UserEntity;
import org.colcum.admin.domain.user.domain.vo.Bookmark;
import org.colcum.admin.global.Error.CommentNotFoundException;
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
import static org.colcum.admin.global.util.Fixture.createFixtureComment;
import static org.colcum.admin.global.util.Fixture.createFixtureDirectedPost;
import static org.colcum.admin.global.util.Fixture.createFixturePost;
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

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private DirectedPostRepository directedPostRepository;

    @Autowired
    private EmojiReactionRepository emojiReactionRepository;

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
        PostCreateDto dto = new PostCreateDto("title", "content", PostCategory.ANNOUNCEMENT, PostStatus.COMPLETE, null, null);

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


    @Test
    @DisplayName("게시글에 북마크를 한다.")
    void doBookmarkOnPost() {
        // given
        PostEntity post1 = Fixture.createFixturePost("title1", "content1", user);
        PostEntity post2 = Fixture.createFixturePost("title2", "content2", user);
        PostEntity post3 = Fixture.createFixturePost("title3", "content3", user);

        post1 = postRepository.save(post1);
        postRepository.save(post2);
        postRepository.save(post3);

        // when
        postService.addBookmark(post1.getId(), user);
        List<PostBookmarkedResponse> responses = postService.findBookmarkedPosts(user);

        // then
        assertThat(responses).containsExactly(PostBookmarkedResponse.from(post1));
        assertThat(responses).doesNotContain(PostBookmarkedResponse.from(post2));
        assertThat(responses).doesNotContain(PostBookmarkedResponse.from(post3));
    }

    @Test
    @DisplayName("게시글에 북마크를 제거한다.")
    void removeBookmarkOnPost() {
        // given
        PostEntity post1 = Fixture.createFixturePost("title1", "content1", user);
        PostEntity post2 = Fixture.createFixturePost("title2", "content2", user);
        PostEntity post3 = Fixture.createFixturePost("title3", "content3", user);

        post1 = postRepository.save(post1);
        post2 = postRepository.save(post2);
        post3 = postRepository.save(post3);

        Bookmark target = new Bookmark(post1.getId());

        // when
        user.addBookmark(target);
        user.addBookmark(new Bookmark(post2.getId()));
        user.addBookmark(new Bookmark(post3.getId()));
        user = userRepository.save(user);

        postService.removeBookmark(target.getPostId(), user);
        List<PostBookmarkedResponse> responses = postService.findBookmarkedPosts(user);

        // then
        assertThat(responses).doesNotContain(PostBookmarkedResponse.from(post1));
        assertThat(responses).contains(PostBookmarkedResponse.from(post2));
        assertThat(responses).contains(PostBookmarkedResponse.from(post3));
    }

    @Test
    @DisplayName("게시글 내의 댓글을 조회한다.")
    void inquireCommentsInPost() {
        // given
        PostEntity post = Fixture.createFixturePost("title1", "content1", user);
        CommentEntity comment1 = new CommentEntity("comment1", user, post);
        CommentEntity comment2 = new CommentEntity("comment2", user, post);
        CommentEntity comment3 = new CommentEntity("comment3", user, post);

        post.addComment(comment1);
        post.addComment(comment2);
        post.addComment(comment3);
        post = postRepository.save(post);
        comment1 = commentRepository.save(comment1);
        comment2 = commentRepository.save(comment2);
        comment3 = commentRepository.save(comment3);

        // when
        PostDetailResponseDto response = postService.inquirePostDetail(post.getId());

        // then
        assertThat(response.getCommentResponseDtos()).contains(CommentResponseDto.from(comment1));
        assertThat(response.getCommentResponseDtos()).contains(CommentResponseDto.from(comment2));
        assertThat(response.getCommentResponseDtos()).contains(CommentResponseDto.from(comment3));
    }

    @Test
    @DisplayName("삭제가 된 댓글은 게시글에 나오지 않는다.")
    void doNotInquireWhenCommentIsDeletedInPost() {
        // given
        PostEntity post = Fixture.createFixturePost("title1", "content1", user);
        CommentEntity comment1 = new CommentEntity("comment1", user, post);
        CommentEntity comment2 = new CommentEntity("comment2", user, post);
        CommentEntity comment3 = new CommentEntity("comment3", user, post);

        comment3.delete();

        post.addComment(comment1);
        post.addComment(comment2);
        post.addComment(comment3);
        post = postRepository.save(post);
        comment1 = commentRepository.save(comment1);
        comment2 = commentRepository.save(comment2);
        comment3 = commentRepository.save(comment3);

        // when
        PostDetailResponseDto response = postService.inquirePostDetail(post.getId());

        // then
        assertThat(response.getCommentResponseDtos()).contains(CommentResponseDto.from(comment1));
        assertThat(response.getCommentResponseDtos()).contains(CommentResponseDto.from(comment2));
        assertThat(response.getCommentResponseDtos()).doesNotContain(CommentResponseDto.from(comment3));
    }

    @Test
    @DisplayName("게시글에 댓글을 생성한다.")
    void createCommentInPost() {
        // given
        PostEntity post = Fixture.createFixturePost("title1", "content1", user);
        post = postRepository.save(post);
        CommentCreateRequestDto dto = new CommentCreateRequestDto("comment1");

        // when
        Long commentId = postService.addComment(post.getId(), dto, user);

        // then
        PostDetailResponseDto response = postService.inquirePostDetail(post.getId());
        assertThat(response.getCommentResponseDtos().stream().map(CommentResponseDto::getId)).contains(commentId);
    }

    @Test
    @DisplayName("게시글에 댓글을 수정한다")
    void updateCommentInPost() {
        // given
        PostEntity post = Fixture.createFixturePost("title1", "content1", user);
        post = postRepository.save(post);
        CommentEntity comment = commentRepository.save(createFixtureComment(user, post, "comment"));
        CommentUpdateRequestDto dto = new CommentUpdateRequestDto("updatedComment");

        // when
        Long updateCommentId = postService.updateComment(comment.getId(), dto, user);
        CommentEntity updatedComment = commentRepository.findById(updateCommentId).get();

        // then
        assertThat(updatedComment.getContent()).isEqualTo(dto.getContent());
        assertThat(updatedComment.getCreatedAt().truncatedTo(ChronoUnit.MILLIS)).isEqualTo(comment.getCreatedAt().truncatedTo(ChronoUnit.MILLIS));
        assertThat(updatedComment.getModifiedAt()).isNotEqualTo(comment.getModifiedAt());
    }

    @Test
    @DisplayName("게시글의 댓글을 삭제한다.")
    void deleteCommentInPost() {
        // given
        PostEntity post = Fixture.createFixturePost("title1", "content1", user);
        post = postRepository.save(post);
        CommentEntity comment = commentRepository.save(createFixtureComment(user, post, "comment"));
        comment = commentRepository.save(comment);
        final Long commentId = comment.getId();

        // when
        postService.deleteComment(commentId, user);

        // then
        assertThrows(CommentNotFoundException.class, () -> postService.findCommentEntity(commentId));
    }

    @Test
    @DisplayName("수신함 요약을 조회한다.")
    void inquirePostsSummaryInReceiveBox() {
        // given
        PostEntity post = createFixturePost("title", "content", user);
        postRepository.save(post);
        DirectedPost directedPost = new DirectedPost(post, user);
        directedPostRepository.save(directedPost);

        // when
        List<ReceivedPostSummaryResponseDto> receivedPostSummaryDto = postService.findReceivedPostSummary(user.getId());

        // then
        assertThat(receivedPostSummaryDto.size()).isEqualTo(1);
        assertThat(receivedPostSummaryDto.get(0).getUserId()).isEqualTo(user.getId());
        assertThat(receivedPostSummaryDto.get(0).getUsername()).isEqualTo(user.getName());
        assertThat(receivedPostSummaryDto.get(0).getUnReadPostCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("수신함을 조회한다.")
    void inquirePostsInReceiveBox() {
        // given
        PostEntity post = createFixtureDirectedPost("title", "content", user);
        postRepository.save(post);
        DirectedPost directedPost = new DirectedPost(post, user);
        directedPostRepository.save(directedPost);

        // when
        Page<PostResponseDto> posts = postService.findReceivedPosts(null, null, null, user, PageRequest.ofSize(10));

        // then
        assertThat(posts.getContent().size()).isEqualTo(1);
        assertThat(posts.getSize()).isEqualTo(10);
        assertThat(posts.getContent()).contains(PostResponseDto.from(post));
    }

    @Test
    @DisplayName("게시글을 생성할 떄, 수신 대상이 있으면 Direct Post도 생성한다.")
    void createDirectPost() {
        // given
        PostCreateDto dto = new PostCreateDto(
            "title",
            "content",
            PostCategory.DELIVERY,
            PostStatus.COMPLETE,
            null,
            List.of(user.getId()));

        PostEntity post = postService.createPost(dto, user);
        List<ReceivedPostSummaryResponseDto> result = directedPostRepository.findDirectedPostByReceiverId(user.getId());

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getUsername()).isEqualTo(user.getName());
        assertThat(result.get(0).getUnReadPostCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("특정 게시글에 이모지를 단다.")
    void addEmojiOnPost() {
        // given
        PostEntity post = createFixturePost("title", "content", user);
        post = postRepository.save(post);
        EmojiCreateDto dto = new EmojiCreateDto("\uD83D\uDE00");

        // when
        Long emojiId = postService.addEmojiOnPost(post.getId(), dto, user);

        EmojiReactionEntity entity = emojiReactionRepository.findById(emojiId).get();
        // then
        assertThat(dto.getEmoji()).isEqualTo(entity.getContent());
        assertThat(post).isEqualTo(entity.getPostEntity());
        assertThat(user).isEqualTo(entity.getUser());
    }

}