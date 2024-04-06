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

    private String writtenBy;

    @JsonFormat(pattern = "yy/MM/dd", timezone = "Asia/Seoul")
    private LocalDate writtenDate;
    private String content;

    public static CommentResponseDto of(String writtenBy, LocalDate writtenDate, String content) {
        return new CommentResponseDto(writtenBy, writtenDate, content);
    }

    public static CommentResponseDto from(CommentEntity comment) {
        return new CommentResponseDto(
            comment.getUser().getName(),
            comment.getCreatedAt().toLocalDate(),
            comment.getContent()
        );
    }

}
