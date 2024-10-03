package org.colcum.admin.global.common.application;

import lombok.RequiredArgsConstructor;
import org.colcum.admin.domain.post.api.dto.MainAnnouncementPostResponseDto;
import org.colcum.admin.domain.post.domain.PostEntity;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RedisPostService {

    private final RedisTemplate<String, Object> postRedisTemplate;
    private static final String REDIS_MAIN_POST_PREFIX = "main-post";

    @Transactional
    public Long changeMainAnnouncementPost(PostEntity post) {
        postRedisTemplate.opsForHash().put(REDIS_MAIN_POST_PREFIX , "postId", post.getId());
        postRedisTemplate.opsForHash().put(REDIS_MAIN_POST_PREFIX , "title", post.getTitle());
        postRedisTemplate.opsForHash().put(REDIS_MAIN_POST_PREFIX , "content", post.getContent());
        return post.getId();
    }

    @Transactional(readOnly = true)
    public MainAnnouncementPostResponseDto getMainAnnouncementPost() {
        Integer postId = (Integer) postRedisTemplate.opsForHash().get(REDIS_MAIN_POST_PREFIX, "postId");
        String title = (String) postRedisTemplate.opsForHash().get(REDIS_MAIN_POST_PREFIX, "title");
        String content = (String) postRedisTemplate.opsForHash().get(REDIS_MAIN_POST_PREFIX, "content");

        if (Objects.isNull(postId)) {
            throw new IllegalArgumentException("등록된 공지사항 게시글이 없습니다.");
        }
        return new MainAnnouncementPostResponseDto(postId.longValue(), title, content);
    }

}
