package org.colcum.admin.domain.user.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.colcum.admin.domain.user.domain.type.Branch;
import org.colcum.admin.domain.user.domain.type.UserType;
import org.colcum.admin.domain.user.domain.vo.Bookmark;
import org.colcum.admin.global.common.domain.BaseEntity;
import org.colcum.admin.global.util.EmailValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString(exclude = {"bookmarks"})
public class UserEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50, unique = true)
    private String email;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private Branch branch = Branch.JONGRO;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private UserType userType = UserType.STAFF;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "userId")
    private List<Bookmark> bookmarks = new ArrayList<>();

    public UserEntity(String email, String password, String name, Branch branch) {
        EmailValidator.validate(email);
        this.email = email;
        this.password = password;
        this.name = name;
        this.branch = branch;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public void setType(UserType userType) {
        this.userType = userType;
    }

    public void addBookmark(Bookmark bookmark) {
        this.bookmarks.add(bookmark);
    }

    public void removeBookmark(Bookmark bookmark) {
        this.bookmarks.remove(bookmark);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserEntity user = (UserEntity) o;

        if (!Objects.equals(this.id, user.id)) return false;
        if (!Objects.equals(email, user.email)) return false;
        return Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {
        int result = email != null ? email.hashCode() : 0;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }

}
