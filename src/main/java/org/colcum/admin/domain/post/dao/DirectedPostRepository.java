package org.colcum.admin.domain.post.dao;

import org.colcum.admin.domain.post.domain.DirectedPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DirectedPostRepository extends JpaRepository<DirectedPost, Long> {}