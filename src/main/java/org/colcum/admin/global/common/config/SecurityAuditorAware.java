package org.colcum.admin.global.common.config;

import org.colcum.admin.global.auth.jwt.JwtAuthentication;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class SecurityAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.of("unknown");
        }
        JwtAuthentication principal = (JwtAuthentication) authentication.getPrincipal();
        return Optional.ofNullable(String.valueOf(principal.userEntity.getEmail()));
    }

}
