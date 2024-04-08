package org.colcum.admin.global.auth.jwt;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.colcum.admin.domain.user.domain.UserEntity;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class JwtAuthentication {

    public final String token;

    public final UserEntity userEntity;

    public JwtAuthentication(String token, UserEntity userEntity) {
        checkArgument(isNotEmpty(token));
        checkArgument(userEntity != null);
        this.token = token;
        this.userEntity = userEntity;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("token", token)
            .append("userEntity", userEntity)
            .toString();
    }

}
