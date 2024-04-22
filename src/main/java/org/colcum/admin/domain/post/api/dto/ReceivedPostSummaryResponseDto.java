package org.colcum.admin.domain.post.api.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReceivedPostSummaryResponseDto {

    private Long userId;
    private String username;
    private Long unReadPostCount;

    @QueryProjection
    public ReceivedPostSummaryResponseDto(Long userId, String username, Long unReadPostCount) {
        this.userId = userId;
        this.username = username;
        this.unReadPostCount = unReadPostCount;
    }

}
