package org.colcum.admin.global.auth.domain;

import lombok.Getter;
import org.colcum.admin.domain.user.domain.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class UserContext extends User {

    private final UserEntity userEntity;

    public UserContext(UserEntity userEntity, Collection<? extends GrantedAuthority> authorities) {
        super(userEntity.getEmail(), userEntity.getPassword(), authorities);
        this.userEntity = userEntity;
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }

}
