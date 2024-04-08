package org.colcum.admin.global.auth.application;

import lombok.RequiredArgsConstructor;
import org.colcum.admin.domain.user.dao.UserRepository;
import org.colcum.admin.domain.user.domain.UserEntity;
import org.colcum.admin.global.auth.domain.UserContext;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserAuthenticationService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException(MessageFormat.format("대상 이메일 : %s가 존재하지 않습니다", email)));

        List<SimpleGrantedAuthority> roles = List.of(new SimpleGrantedAuthority(user.getUserType().name()));

        return new UserContext(user, roles);
    }

    public UserEntity loadUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(
            () -> new UsernameNotFoundException("해당 유저는 존재하지 않습니다.")
        );
    }

}
