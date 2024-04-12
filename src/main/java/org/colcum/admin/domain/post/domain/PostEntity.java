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
import org.colcum.admin.domain.post.domain.type.PostCategory;
import org.colcum.admin.domain.post.domain.type.PostStatus;
import org.colcum.admin.domain.user.domain.UserEntity;
import org.colcum.admin.global.common.domain.BaseEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    @Column(nullable = false)
    private boolean isBookmarked;

    private LocalDate expiredDate;

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

    public PostEntity(String title, String content, PostCategory category, PostStatus status, boolean isBookmarked, LocalDate expiredDate, UserEntity user) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.status = status;
        this.isBookmarked = isBookmarked;
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

}
