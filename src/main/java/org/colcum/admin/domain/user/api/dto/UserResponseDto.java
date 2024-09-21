package org.colcum.admin.domain.user.api.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.colcum.admin.domain.user.domain.UserEntity;

@Getter
@RequiredArgsConstructor
public class UserResponseDto {

    private final Long userId;
    private final String username;

    public static UserResponseDto from(UserEntity userEntity) {
        return new UserResponseDto(userEntity.getId(), userEntity.getName());
    }

}
