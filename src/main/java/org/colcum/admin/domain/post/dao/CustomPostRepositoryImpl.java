package org.colcum.admin.domain.post.dao;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.colcum.admin.domain.post.api.dto.CommentResponseDto;
import org.colcum.admin.domain.post.api.dto.EmojiResponseDto;
import org.colcum.admin.domain.post.api.dto.PostBookmarkedResponse;
import org.colcum.admin.domain.post.api.dto.PostResponseDto;
import org.colcum.admin.domain.post.api.dto.PostSearchCondition;
import org.colcum.admin.domain.post.api.dto.SentPostResponseDto;
import org.colcum.admin.domain.post.domain.PostEntity;
import org.colcum.admin.domain.post.domain.QPostEntity;
import org.colcum.admin.domain.post.domain.type.PostCategory;
import org.colcum.admin.domain.user.domain.UserEntity;
import org.colcum.admin.domain.user.domain.vo.Bookmark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static org.colcum.admin.domain.post.domain.QCommentEntity.commentEntity;
import static org.colcum.admin.domain.post.domain.QDirectPost.directPost;
import static org.colcum.admin.domain.post.domain.QEmojiReactionEntity.emojiReactionEntity;
import static org.colcum.admin.domain.post.domain.QPostEntity.postEntity;
import static org.colcum.admin.domain.user.domain.QUserEntity.userEntity;
import static org.colcum.admin.domain.user.domain.vo.QBookmark.bookmark;

@Component
@RequiredArgsConstructor
public class CustomPostRepositoryImpl implements CustomPostRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<PostResponseDto> search(PostSearchCondition condition, UserEntity receivedUser, Pageable pageable) {
        BooleanBuilder builder = getPostBooleanBuilder(condition);
        List<PostEntity> posts = queryFactory
            .select(postEntity).distinct()
            .from(postEntity)
            .innerJoin(postEntity.user, userEntity)
            .leftJoin(postEntity.commentEntities, commentEntity)
            .leftJoin(postEntity.emojiReactionEntities, emojiReactionEntity).fetchJoin()
            .where(builder
                .and(
                    postEntity.category.eq(PostCategory.ANNOUNCEMENT)
                        .or(
                            postEntity.category.eq(PostCategory.DELIVERY)
                                .and(
                                    JPAExpressions
                                        .selectOne()
                                        .from(directPost)
                                        .where(directPost.receiver.eq(receivedUser)
                                            .and(directPost.postEntity.eq(postEntity))) // postEntity와 관련된 조건 추가
                                        .exists()
                                )
                        )
                )
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(this.getOrderBySpecifiers(postEntity, pageable))
            .fetch();

        List<Bookmark> bookmarks = receivedUser.getBookmarks();
        List<Long> bookmarkedPostIds = bookmarks.stream().map(Bookmark::getPostId).toList();

        List<PostResponseDto> dtos = posts.stream()
            .map(p -> {
                boolean isBookmarked = bookmarkedPostIds.contains(p.getId());
                return PostResponseDto.from(p, isBookmarked);
            })
            .toList();

        JPAQuery<Long> count = queryFactory
            .select(postEntity.count())
            .from(postEntity)
            .where(builder);

        return PageableExecutionUtils.getPage(dtos, pageable, count::fetchCount);
    }

    @Override
    public Page<PostResponseDto> searchWithBookmarkedPost(PostSearchCondition condition, UserEntity user, Pageable pageable) {
        BooleanBuilder builder = getPostBooleanBuilder(condition);
        List<PostEntity> posts = queryFactory
            .select(postEntity).distinct()
            .from(postEntity)
            .leftJoin(postEntity.commentEntities, commentEntity)
            .leftJoin(postEntity.emojiReactionEntities, emojiReactionEntity).fetchJoin()
            .where(postEntity.id.in(
                    JPAExpressions
                        .select(bookmark.postId)
                        .from(userEntity)
                        .join(userEntity.bookmarks, bookmark)
                        .where(userEntity.id.eq(user.getId()))
                ).and(builder)
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(this.getOrderBySpecifiers(postEntity, pageable))
            .fetch();

        List<Bookmark> bookmarks = user.getBookmarks();
        List<Long> bookmarkedPostIds = bookmarks.stream().map(Bookmark::getPostId).toList();

        List<PostResponseDto> dtos = posts.stream()
            .map(p -> {
                boolean isBookmarked = bookmarkedPostIds.contains(p.getId());
                return PostResponseDto.from(p, isBookmarked);
            })
            .toList();

        JPAQuery<Long> count = queryFactory
            .select(postEntity.count())
            .from(postEntity)
            .where(builder);

        return PageableExecutionUtils.getPage(dtos, pageable, count::fetchCount);
    }

    @Override
    public Page<PostResponseDto> searchReceivedPost(PostSearchCondition condition, UserEntity receivedUser, Pageable pageable) {
        BooleanBuilder builder = getPostBooleanBuilder(condition);
        List<PostEntity> posts = queryFactory
            .select(postEntity).distinct()
            .from(directPost)
            .innerJoin(directPost.postEntity, postEntity)
            .innerJoin(postEntity.user, userEntity)
            .leftJoin(userEntity.bookmarks, bookmark)
            .leftJoin(postEntity.commentEntities, commentEntity)
            .leftJoin(postEntity.emojiReactionEntities, emojiReactionEntity)
            .where(builder.and(directPost.receiver.id.eq(receivedUser.getId())))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(this.getOrderBySpecifiers(postEntity, pageable))
            .fetch();

        List<Bookmark> bookmarks = receivedUser.getBookmarks();
        List<Long> bookmarkedPostIds = bookmarks.stream().map(Bookmark::getPostId).toList();

        List<PostResponseDto> dtos = posts.stream()
            .map(p -> {
                boolean isBookmarked = bookmarkedPostIds.contains(p.getId());
                return PostResponseDto.from(p, isBookmarked);
            })
            .toList();

        JPAQuery<Long> count = queryFactory
            .select(postEntity.count())
            .from(postEntity)
            .where(builder);

        return PageableExecutionUtils.getPage(dtos, pageable, count::fetchCount);
    }

    @Override
    public Page<PostResponseDto> findSentPostByUserId(PostSearchCondition condition, UserEntity user, Pageable pageable) {
        BooleanBuilder builder = getPostBooleanBuilder(condition);
        List<PostEntity> posts = queryFactory
            .select(postEntity).distinct()
            .from(postEntity)
            .innerJoin(postEntity.user, userEntity)
            .leftJoin(postEntity.commentEntities, commentEntity)
            .leftJoin(postEntity.emojiReactionEntities, emojiReactionEntity)
            .leftJoin(userEntity.bookmarks, bookmark)
            .where(
                builder
                    .and(postEntity.user.id.eq(user.getId()))
                    .and(postEntity.category.eq(PostCategory.DELIVERY))
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(this.getOrderBySpecifiers(postEntity, pageable))
            .fetch();

        List<Bookmark> bookmarks = user.getBookmarks();
        List<Long> bookmarkedPostIds = bookmarks.stream().map(Bookmark::getPostId).toList();

        List<PostResponseDto> dtos = posts.stream()
            .map(p -> {
                boolean isBookmarked = bookmarkedPostIds.contains(p.getId());
                return PostResponseDto.from(p, isBookmarked);
            })
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
            .innerJoin(postEntity.user, userEntity).fetchJoin()
            .fetchJoin()
            .where(postEntity.id.eq(id)
                .and(postEntity.deleted.eq(false))
            )
            .fetchOne());
    }

    @Override
    public Optional<PostEntity> findByIdAndDeletedIsFalse(Long id) {
        return Optional.ofNullable(
            queryFactory
                .selectFrom(postEntity)
                .leftJoin(postEntity.commentEntities, commentEntity)
                .where(postEntity.id.eq(id)
                    .and(postEntity.deleted.eq(false))
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

    private static BooleanBuilder getPostBooleanBuilder(PostSearchCondition condition) {
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
        return builder.and(postEntity.deleted.eq(false));
    }

    private OrderSpecifier<?>[] getOrderBySpecifiers(QPostEntity postEntity, Pageable pageable) {
        return pageable.getSort().stream()
            .map(order -> {
                PathBuilder<?> pathBuilder = new PathBuilder<>(postEntity.getType(), postEntity.getMetadata());
                return new OrderSpecifier(
                    order.isAscending() ? Order.ASC : Order.DESC,
                    pathBuilder.get(order.getProperty())
                );
            })
            .toArray(OrderSpecifier[]::new);
    }

}
