package org.colcum.admin.domain.post.dao;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.colcum.admin.domain.post.api.dto.QReceivedPostSummaryResponseDto;
import org.colcum.admin.domain.post.api.dto.ReceivedPostSummaryResponseDto;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.colcum.admin.domain.post.domain.QDirectedPost.directedPost;
import static org.colcum.admin.domain.user.domain.QUserEntity.userEntity;

@Component
@RequiredArgsConstructor
public class CustomDirectedPostRepositoryImpl implements CustomDirectedPostRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ReceivedPostSummaryResponseDto> findDirectedPostByReceiverId(Long receiverId) {
        List<ReceivedPostSummaryResponseDto> dtos = queryFactory
            .select(new QReceivedPostSummaryResponseDto(userEntity.id, userEntity.name, directedPost.count()))
            .from(directedPost)
            .innerJoin(directedPost.receiver, userEntity)
            .where(
                directedPost.receiver.id.eq(receiverId)
                    .and(directedPost.deleted.isFalse())
            )
            .groupBy(userEntity.id, userEntity.name)
            .fetch();

        return dtos;
    }

}
