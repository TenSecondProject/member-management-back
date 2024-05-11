package org.colcum.admin.domain.user.api;

import lombok.RequiredArgsConstructor;
import org.colcum.admin.global.Error.InvalidAuthenticationException;
import org.colcum.admin.global.auth.jwt.Jwt;
import org.colcum.admin.global.auth.jwt.JwtAuthentication;
import org.colcum.admin.global.common.api.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final Jwt jwt;

    @GetMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<String> inquirePostsWithBookmarked(
        @AuthenticationPrincipal JwtAuthentication authentication
    ) {
        if (Objects.isNull(authentication)) {
            throw new InvalidAuthenticationException("해당 refresh token은 잘못되었습니다. 다시 로그인 해주십시오.");
        }
        String accessToken = jwt.sign(
            Jwt.Claims.of(
                authentication.userEntity.getId(),
                new String[]{authentication.userEntity.getUserType().getRole()}
            ),
            Jwt.ACCESS_TOKEN_EXPIRY_MINUTE
        );
        return new ApiResponse<>(HttpStatus.OK.value(), "success", accessToken);
    }

}
