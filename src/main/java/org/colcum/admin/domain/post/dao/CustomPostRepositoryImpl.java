package org.colcum.admin.domain.post.dao;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.colcum.admin.domain.post.api.dto.PostResponseDto;
import org.colcum.admin.domain.post.api.dto.PostSearchCondition;
import org.colcum.admin.domain.post.api.dto.QPostResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

import static org.colcum.admin.domain.post.domain.QCommentEntity.commentEntity;
import static org.colcum.admin.domain.post.domain.QPostEntity.postEntity;

@Component
@RequiredArgsConstructor
public class CustomPostRepositoryImpl implements CustomPostRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<PostResponseDto> search(PostSearchCondition condition, Pageable pageable) {
        BooleanBuilder builder = getBooleanBuilder(condition);

        List<PostResponseDto> fetch = queryFactory
            .select(new QPostResponseDto(
                postEntity.id,
                postEntity.title,
                postEntity.content,
                postEntity.status,
                postEntity.createdBy,
                postEntity.isBookmarked,
                Expressions.constant(0)
//                commentEntity.count().intValue()
            ))
            .from(postEntity)
//            .rightJoin(postEntity.commentEntities, commentEntity)
            .where(builder)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .groupBy(postEntity.id)
            .fetch();

        JPAQuery<Long> count = queryFactory
            .select(postEntity.count())
            .from(postEntity)
            .where(builder);

        return PageableExecutionUtils.getPage(fetch, pageable, count::fetchCount);
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
        return builder;
    }

}
