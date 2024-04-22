package org.colcum.admin.domain.post.dao;

import org.colcum.admin.domain.post.api.dto.ReceivedPostSummaryResponseDto;

import java.util.List;

public interface CustomDirectedPostRepository {

    List<ReceivedPostSummaryResponseDto> findDirectedPostByReceiverId(Long receiverId);

}
