package org.colcum.admin.domain.post.api.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.colcum.admin.domain.post.domain.EmojiReactionEntity;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@RequiredArgsConstructor
public class EmojiResponseDto {

    private final String emoji;

    private final int totalCount;

    private final List<String> usernames;

    public static EmojiResponseDto of(String emoji, int totalCount, List<String> usernames) {
        return new EmojiResponseDto(emoji, totalCount, usernames);
    }

    public static List<EmojiResponseDto> from(List<EmojiReactionEntity> entities) {
        Map<String, List<String>> groupByEmoji = entities.stream().collect(
            Collectors.groupingBy(
                EmojiReactionEntity::getContent,
                Collectors.mapping(e -> e.getUser().getName(), Collectors.toList())
            )
        );
        return groupByEmoji.entrySet().stream()
            .map(entry -> EmojiResponseDto.of(entry.getKey(), entry.getValue().size(), entry.getValue()))
            .toList();
    }

}
