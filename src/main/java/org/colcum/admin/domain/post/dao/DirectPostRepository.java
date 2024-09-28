package org.colcum.admin.domain.post.dao;

import org.colcum.admin.domain.post.domain.DirectPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DirectPostRepository extends JpaRepository<DirectPost, Long>, CustomDirectPostRepository {

    List<DirectPost> findByPostEntity_IdAndDeletedIsFalse(Long id);

}