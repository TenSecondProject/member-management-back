package org.colcum.admin.global.auth.application;

import lombok.RequiredArgsConstructor;
import org.colcum.admin.global.auth.domain.UserContext;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
public class UserAuthenticationProvider implements AuthenticationProvider {

    private final UserAuthenticationService userAuthenticationService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String credentials = (String) authentication.getCredentials();

        UserContext context = (UserContext) userAuthenticationService.loadUserByUsername(username);

        if (!passwordEncoder.matches(credentials, context.getPassword())) {
            throw new BadCredentialsException("BadCredentialException");
        }

        return new UsernamePasswordAuthenticationToken(context.getUserEntity(), null, authentication.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
