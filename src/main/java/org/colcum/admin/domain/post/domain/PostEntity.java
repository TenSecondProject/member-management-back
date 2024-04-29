package org.colcum.admin.domain.post.domain;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.colcum.admin.domain.post.api.dto.PostUpdateDto;
import org.colcum.admin.domain.post.domain.type.PostCategory;
import org.colcum.admin.domain.post.domain.type.PostStatus;
import org.colcum.admin.domain.user.domain.UserEntity;
import org.colcum.admin.global.common.domain.BaseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "post")
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class PostEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, length = 5000)
    private String content;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private PostCategory category;

    @Column(nullable = false, length = 20)
    @Enumerated
    private PostStatus status;

    private LocalDateTime expiredDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @ToString.Exclude
    private UserEntity user;

    @OneToMany(mappedBy = "postEntity")
    @ToString.Exclude
    private List<CommentEntity> commentEntities = new ArrayList<>();

    @OneToMany(mappedBy = "postEntity")
    @ToString.Exclude
    private List<EmojiReactionEntity> emojiReactionEntities = new ArrayList<>();

    public PostEntity(String title, String content, PostCategory category, PostStatus status, LocalDateTime expiredDate, UserEntity user, List<CommentEntity> commentEntities, List<EmojiReactionEntity> emojiReactionEntities) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.status = status;
        this.expiredDate = expiredDate;
        this.user = user;
        this.commentEntities = commentEntities;
        this.emojiReactionEntities = emojiReactionEntities;
    }

    public PostEntity(String title, String content, PostCategory category, PostStatus status, LocalDateTime expiredDate, UserEntity user) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.status = status;
        this.expiredDate = expiredDate;
        this.user = user;
    }

    public void addComment(CommentEntity commentEntity) {
        this.commentEntities.add(commentEntity);
        if (!commentEntity.getPostEntity().equals(this)) {
            commentEntity.setPostEntity(this);
        }
    }

    public void removeComment(CommentEntity commentEntity) {
        this.commentEntities.remove(commentEntity);
        commentEntity.setPostEntity(null);
    }

    public void addEmoji(EmojiReactionEntity emojiReactionEntity) {
        this.emojiReactionEntities.add(emojiReactionEntity);
        if (!emojiReactionEntity.getPostEntity().equals(this)) {
            emojiReactionEntity.setPostEntity(this);
        }
    }

    public void removeEmoji(EmojiReactionEntity emojiReactionEntity) {
        this.emojiReactionEntities.remove(emojiReactionEntity);
        emojiReactionEntity.setPostEntity(null);
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public PostEntity update(PostUpdateDto dto) {
        this.title = dto.getTitle();
        this.content = dto.getContent();
        this.status = dto.getStatus();
        this.expiredDate = dto.getExpiredDate();
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PostEntity post = (PostEntity) o;

        return Objects.equals(id, post.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

}
