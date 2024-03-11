package org.colcum.admin.domain.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.colcum.admin.global.Error.EmailValidationException;
import org.colcum.admin.global.common.domain.BaseEntity;
import org.colcum.admin.global.util.EmailValidator;

import java.util.regex.Pattern;

@Entity
@Table(name = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UserEntity extends BaseEntity {

    @Column(nullable = false, length = 50, unique = true)
    private String email;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private Branch branch;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private UserType type = UserType.STAFF;

    public UserEntity(String email, String name, Branch branch) {
        EmailValidator.validate(email);
        this.email = email;
        this.name = name;
        this.branch = branch;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public Branch getBranch() {
        return branch;
    }

    public UserType getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public void setType(UserType type) {
        this.type = type;
    }

}
