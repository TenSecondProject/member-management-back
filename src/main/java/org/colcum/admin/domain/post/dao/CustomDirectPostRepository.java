package org.colcum.admin.domain.post.dao;

import org.colcum.admin.domain.post.api.dto.ReceivedPostSummaryResponseDto;

import java.util.List;

public interface CustomDirectPostRepository {

    List<ReceivedPostSummaryResponseDto> findDirectedPostByReceiverId(Long receiverId);

}
