package org.colcum.admin.domain.post.application;

import org.colcum.admin.domain.post.dao.PostRepository;
import org.colcum.admin.domain.post.domain.PostEntity;
import org.colcum.admin.domain.user.dao.UserRepository;
import org.colcum.admin.domain.user.domain.UserEntity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.colcum.admin.global.util.Fixture.createFixture;
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
        PostEntity post1 = createFixture("title1", "content1", user);
        PostEntity post2 = createFixture("title2", "content2", user);
        PostEntity post3 = createFixture("title3", "content3", user);
        postRepository.save(post1);
        postRepository.save(post2);
        postRepository.save(post3);

        // when
        List<PostEntity> posts = postRepository.findAll();

        // then
        assertThat(posts.size()).isEqualTo(3);
        assertThat(posts).contains(post1, post2, post3);
    }

}