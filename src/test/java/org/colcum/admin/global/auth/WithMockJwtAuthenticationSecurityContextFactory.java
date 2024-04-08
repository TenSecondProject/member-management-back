package org.colcum.admin.global.auth;

import org.colcum.admin.global.auth.jwt.JwtAuthentication;
import org.colcum.admin.global.auth.jwt.JwtAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;

public class WithMockJwtAuthenticationSecurityContextFactory implements
    WithSecurityContextFactory<WithMockJwtAuthentication> {

    @Override
    public SecurityContext createSecurityContext(WithMockJwtAuthentication annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        JwtAuthenticationToken authentication =
            new JwtAuthenticationToken(
                new JwtAuthentication(annotation.token(), annotation.id()),
                null,
                createAuthorityList(annotation.role())
            );
        context.setAuthentication(authentication);
        return context;
    }

}
