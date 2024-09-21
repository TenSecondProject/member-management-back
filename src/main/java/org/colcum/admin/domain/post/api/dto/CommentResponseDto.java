package org.colcum.admin.domain.post.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.colcum.admin.domain.post.domain.CommentEntity;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDto {

    private Long id;
    private Long userId;
    private String username;

    @JsonFormat(pattern = "MM/dd", timezone = "Asia/Seoul")
    private LocalDate writtenDate;
    private String content;

    public static CommentResponseDto of(Long id, Long userId, String username, LocalDate writtenDate, String content) {
        return new CommentResponseDto(id, userId, username, writtenDate, content);
    }

    public static CommentResponseDto from(CommentEntity comment) {
        return new CommentResponseDto(
            comment.getId(),
            comment.getUser().getId(),
            comment.getUser().getName(),
            comment.getCreatedAt().toLocalDate(),
            comment.getContent()
        );
    }

}
