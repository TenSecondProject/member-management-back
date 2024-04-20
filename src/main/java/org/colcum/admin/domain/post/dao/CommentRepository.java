package org.colcum.admin.domain.post.dao;

import org.colcum.admin.domain.post.domain.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    Optional<CommentEntity> findByIdAndDeletedIsFalse(Long commentId);

}
