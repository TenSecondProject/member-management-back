package org.colcum.admin.domain.post.dao;

import org.colcum.admin.domain.post.domain.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<PostEntity, Long>, CustomPostRepository {}
