package org.colcum.admin.domain.post.domain;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.colcum.admin.domain.post.api.dto.CommentUpdateRequestDto;
import org.colcum.admin.domain.user.domain.UserEntity;
import org.colcum.admin.global.common.domain.BaseEntity;

@Entity
@Table(name = "comment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Getter
public class CommentEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500, nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private PostEntity postEntity;

    public CommentEntity(String content, UserEntity user, PostEntity postEntity) {
        this.content = content;
        this.user = user;
        this.postEntity = postEntity;
    }

    public void setPostEntity(PostEntity postEntity) {
        this.postEntity = postEntity;
    }

    public CommentEntity update(CommentUpdateRequestDto dto) {
        this.content = dto.getContent();
        return this;
    }

}
