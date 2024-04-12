package org.colcum.admin.domain.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.colcum.admin.global.common.domain.BaseEntity;
import org.colcum.admin.global.util.EmailValidator;

@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
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

}
