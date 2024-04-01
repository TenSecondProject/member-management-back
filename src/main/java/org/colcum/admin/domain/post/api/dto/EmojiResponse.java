package org.colcum.admin.domain.post.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class EmojiResponse {

    private String emoji;
    private int totalCount;
    private List<String> usernames;

}
