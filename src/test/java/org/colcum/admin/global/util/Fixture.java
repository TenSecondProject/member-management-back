package org.colcum.admin.global.util;

import org.colcum.admin.domain.post.domain.PostCategory;
import org.colcum.admin.domain.post.domain.PostEntity;
import org.colcum.admin.domain.post.domain.PostStatus;
import org.colcum.admin.domain.user.domain.Branch;
import org.colcum.admin.domain.user.domain.UserEntity;

import java.time.LocalDate;

public class Fixture {

    public static PostEntity createFixture(String title, String content, UserEntity user) {
        return createFixture(title, content, PostCategory.ANNOUNCEMENT, PostStatus.UNCOMPLETED, false, null, user);
    }

    public static PostEntity createFixture(String title, String content, PostCategory postCategory, PostStatus postStatus, boolean isBookmarked,LocalDate expiredDate, UserEntity user) {
        return new PostEntity(title, content, postCategory, postStatus, isBookmarked, expiredDate, user);
    }

    public static UserEntity createFixtureUser() {
        return new UserEntity("test@gmail.com", "password", "tester", Branch.JONGRO);
    }

}
