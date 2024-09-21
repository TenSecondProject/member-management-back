package org.colcum.admin.domain.user.application;

import lombok.extern.slf4j.Slf4j;
import org.colcum.admin.domain.user.api.dto.UserResponseDto;
import org.colcum.admin.domain.user.dao.UserRepository;
import org.colcum.admin.domain.user.domain.UserEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public UserEntity getUser(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> {
                throw new UsernameNotFoundException("해당 유저는 존재하지 않습니다.");
            });
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> getReceivers() {
        log.info("[Get Receivers Info]");
        return userRepository.findAllByDeletedIsFalse()
            .stream().map(UserResponseDto::from).collect(Collectors.toList());
    }

}
