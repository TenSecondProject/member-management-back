package org.colcum.admin.domain.post.dao;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.colcum.admin.domain.post.api.dto.PostBookmarkedResponse;
import org.colcum.admin.domain.post.api.dto.PostResponseDto;
import org.colcum.admin.domain.post.api.dto.PostSearchCondition;
import org.colcum.admin.domain.post.domain.PostEntity;
import org.colcum.admin.domain.user.domain.vo.Bookmark;
import org.colcum.admin.domain.user.domain.vo.QBookmark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.colcum.admin.domain.post.domain.QCommentEntity.commentEntity;
import static org.colcum.admin.domain.post.domain.QEmojiReactionEntity.emojiReactionEntity;
import static org.colcum.admin.domain.post.domain.QPostEntity.postEntity;
import static org.colcum.admin.domain.user.domain.QUserEntity.userEntity;
import static org.colcum.admin.domain.user.domain.vo.QBookmark.bookmark;

@Component
@RequiredArgsConstructor
public class CustomPostRepositoryImpl implements CustomPostRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<PostResponseDto> search(PostSearchCondition condition, Pageable pageable) {
        BooleanBuilder builder = getBooleanBuilder(condition);

        List<PostEntity> fetch = queryFactory
            .select(postEntity)
            .from(postEntity)
            .innerJoin(postEntity.user, userEntity)
            .leftJoin(postEntity.commentEntities, commentEntity)
            .leftJoin(postEntity.emojiReactionEntities, emojiReactionEntity).fetchJoin()
            .where(builder)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .groupBy(postEntity.id)
            .fetch();

        List<PostResponseDto> dtos = fetch.stream()
            .map(PostResponseDto::from)
            .toList();

        JPAQuery<Long> count = queryFactory
            .select(postEntity.count())
            .from(postEntity)
            .where(builder);

        return PageableExecutionUtils.getPage(dtos, pageable, count::fetchCount);
    }

    @Override
    public Optional<PostEntity> findByIdWithUser(Long id) {
        return Optional.ofNullable(queryFactory
            .selectFrom(postEntity)
            .innerJoin(postEntity.user, userEntity)
            .fetchJoin()
            .where(postEntity.id.eq(id)
                .and(postEntity.isDeleted.eq(false))
            )
            .fetchOne());
    }

    @Override
    public Optional<PostEntity> findByIdAndDeletedIsFalse(Long id) {
        return Optional.ofNullable(
            queryFactory
                .selectFrom(postEntity)
                .innerJoin(postEntity.commentEntities, commentEntity).fetchJoin()
                .where(postEntity.id.eq(id)
                    .and(postEntity.isDeleted.eq(false))
                    .and(commentEntity.isDeleted.eq(false))
                )
                .distinct()
                .fetchOne()
        );
    }

    @Override
    public List<PostBookmarkedResponse> findWithBookmarked(Long userId) {
        return queryFactory
            .select(Projections.constructor(
                PostBookmarkedResponse.class,
                postEntity.id,
                postEntity.title
            ))
            .from(postEntity)
            .where(
                postEntity.id.in(
                    JPAExpressions
                        .select(bookmark.postId)
                        .from(userEntity)
                        .join(userEntity.bookmarks, bookmark)
                        .where(userEntity.id.eq(userId)
                    )
                )
            )
            .fetch();
    }

    private static BooleanBuilder getBooleanBuilder(PostSearchCondition condition) {
        BooleanBuilder builder = new BooleanBuilder();
        if (Objects.nonNull(condition.getCategories()) && condition.getCategories().size() > 0) {
            builder.and(postEntity.category.in(condition.getCategories()));
        }
        if (Objects.nonNull(condition.getPostStatuses()) && condition.getPostStatuses().size() > 0) {
            builder.and(postEntity.status.in(condition.getPostStatuses()));
        }
        if (Objects.nonNull(condition.getSearchType())) {
            switch (condition.getSearchType()) {
                case TITLE -> builder.and(postEntity.title.contains(condition.getSearchValue()));
                case CONTENT -> builder.and(postEntity.content.contains(condition.getSearchValue()));
                case WRITTEN_USER -> builder.and(postEntity.createdBy.contains(condition.getSearchValue()));
            }
        }
        return builder.and(postEntity.isDeleted.eq(false));
    }

}
