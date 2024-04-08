package org.colcum.admin.domain.post.dao;

import org.colcum.admin.domain.post.domain.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

}
