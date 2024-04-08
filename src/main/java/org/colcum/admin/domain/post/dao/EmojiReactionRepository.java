package org.colcum.admin.domain.post.dao;

import org.colcum.admin.domain.post.domain.EmojiReactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmojiReactionRepository extends JpaRepository<EmojiReactionEntity, Long> {

}
