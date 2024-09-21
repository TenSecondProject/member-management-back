package org.colcum.admin.global.auth;

import org.colcum.admin.domain.user.domain.type.Branch;
import org.colcum.admin.domain.user.domain.UserEntity;
import org.colcum.admin.global.auth.jwt.JwtAuthentication;
import org.colcum.admin.global.auth.jwt.JwtAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.lang.reflect.Field;

import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;

public class WithMockJwtAuthenticationSecurityContextFactory implements
    WithSecurityContextFactory<WithMockJwtAuthentication> {

    @Override
    public SecurityContext createSecurityContext(WithMockJwtAuthentication annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        UserEntity user = new UserEntity("tester@gmail.com", "1234", "tester", Branch.JONGRO);

        try {
            Field idField = UserEntity.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, annotation.id());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Mock 유저의 Id 반영이 실패하였습니다.", e);
        }

        JwtAuthenticationToken authentication =
            new JwtAuthenticationToken(
                new JwtAuthentication(annotation.token(), user),
                null,
                createAuthorityList(annotation.role())
            );
        context.setAuthentication(authentication);
        return context;
    }

}
