package org.colcum.admin.domain.post.dao;

import org.colcum.admin.domain.post.domain.EmojiReactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmojiReactionRepository extends JpaRepository<EmojiReactionEntity, Long> {

    Optional<EmojiReactionEntity> findByPostEntity_IdAndUser_Id(Long postId, Long userId);

}
