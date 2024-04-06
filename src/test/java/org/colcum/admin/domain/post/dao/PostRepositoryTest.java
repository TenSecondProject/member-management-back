package org.colcum.admin.domain.post.dao;

import org.colcum.admin.domain.post.api.dto.PostResponseDto;
import org.colcum.admin.domain.post.api.dto.PostSearchCondition;
import org.colcum.admin.domain.post.domain.CommentEntity;
import org.colcum.admin.domain.post.domain.EmojiReactionEntity;
import org.colcum.admin.domain.post.domain.PostEntity;
import org.colcum.admin.domain.post.domain.type.PostCategory;
import org.colcum.admin.domain.post.domain.type.PostStatus;
import org.colcum.admin.domain.post.domain.type.SearchType;
import org.colcum.admin.domain.user.dao.UserRepository;
import org.colcum.admin.domain.user.domain.UserEntity;
import org.colcum.admin.global.common.config.JpaConfiguration;
import org.colcum.admin.global.util.Fixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.colcum.admin.global.util.Fixture.createFixtureComment;
import static org.colcum.admin.global.util.Fixture.createFixtureEmoji;
import static org.colcum.admin.global.util.Fixture.createFixtureUser;

@DataJpaTest
@ActiveProfiles("local")
@Import(JpaConfiguration.class)
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private EmojiReactionRepository emojiReactionRepository;

    private UserEntity user;

    @BeforeEach
    void setup() {
        user = userRepository.save(createFixtureUser());
    }

    @Test
    @DisplayName("검색조건으로 게시글을 조회한다.")
    void inquirePostsWithParameters() {
        // given
        PostEntity post1 = Fixture.createFixturePost("title", "content1", user);
        PostEntity post2 = Fixture.createFixturePost("title", "content2", user);
        PostEntity post3 = Fixture.createFixturePost("title", "content3", user);
        postRepository.save(post1);
        postRepository.save(post2);
        postRepository.save(post3);

        CommentEntity commentEntity = createFixtureComment(user, post1, "commentContent1");
        post1.addComment(commentEntity);
        commentRepository.save(commentEntity);

        EmojiReactionEntity emojiReactionEntity = createFixtureEmoji(user, post1, "\uD83D\uDE00");
        post1.addEmoji(emojiReactionEntity);
        emojiReactionRepository.save(emojiReactionEntity);

        PostSearchCondition condition = new PostSearchCondition(
            SearchType.TITLE,
            "title",
            List.of(PostCategory.ANNOUNCEMENT),
            List.of(PostStatus.UNCOMPLETED)
        );

        // when
        Page<PostResponseDto> posts = postRepository.search(condition, PageRequest.of(0, 10));

        // then
        assertThat(posts.getContent().size()).isEqualTo(3);
        assertThat(posts.getSize()).isEqualTo(10);
        assertThat(posts.getContent()).contains(PostResponseDto.from(post1), PostResponseDto.from(post2), PostResponseDto.from(post3));
    }

    @Test
    @DisplayName("게시글 상세 페이지를 조회한다.")
    void inquirePostDetail() {
        // given
        PostEntity post = Fixture.createFixturePost("title", "content1", user);
        post = postRepository.save(post);

        CommentEntity commentEntity = createFixtureComment(user, post, "commentContent1");
        post.addComment(commentEntity);
        commentRepository.save(commentEntity);

        EmojiReactionEntity emojiReactionEntity = createFixtureEmoji(user, post, "\uD83D\uDE00");
        post.addEmoji(emojiReactionEntity);
        emojiReactionRepository.save(emojiReactionEntity);

        // when
        PostEntity result = postRepository.findById(post.getId()).orElseThrow();

        // then
        assertThat(result).isEqualTo(post);
    }

}