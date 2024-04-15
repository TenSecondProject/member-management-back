package org.colcum.admin.global.util;

import org.colcum.admin.domain.post.domain.CommentEntity;
import org.colcum.admin.domain.post.domain.EmojiReactionEntity;
import org.colcum.admin.domain.post.domain.type.PostCategory;
import org.colcum.admin.domain.post.domain.PostEntity;
import org.colcum.admin.domain.post.domain.type.PostStatus;
import org.colcum.admin.domain.user.domain.type.Branch;
import org.colcum.admin.domain.user.domain.UserEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Fixture {

    public static PostEntity createFixturePost(String title, String content, UserEntity user) {
        return createFixturePost(title, content, PostCategory.ANNOUNCEMENT, PostStatus.UNCOMPLETED, null, user, new ArrayList<>(), new ArrayList<>());
    }

    public static PostEntity createFixturePost(String title, String content, PostCategory postCategory, PostStatus postStatus, LocalDateTime expiredDate, UserEntity user, List<CommentEntity> comments, List<EmojiReactionEntity> emojis) {
        return new PostEntity(title, content, postCategory, postStatus, expiredDate, user, comments, emojis);
    }

    public static UserEntity createFixtureUser() {
        return new UserEntity("test@gmail.com", "1234", "tester", Branch.JONGRO);
    }

    public static CommentEntity createFixtureComment(UserEntity user, PostEntity post, String content) {
        return new CommentEntity(user, post, content);
    }

    public static EmojiReactionEntity createFixtureEmoji(UserEntity user, PostEntity post, String emoji) {
        return new EmojiReactionEntity(user, post, emoji);
    }

}
