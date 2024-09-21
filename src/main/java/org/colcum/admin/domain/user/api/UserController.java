package org.colcum.admin.domain.user.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.colcum.admin.domain.user.api.dto.UserResponseDto;
import org.colcum.admin.domain.user.application.UserService;
import org.colcum.admin.domain.user.domain.UserEntity;
import org.colcum.admin.global.auth.api.dto.RefreshToken;
import org.colcum.admin.global.auth.jwt.Jwt;
import org.colcum.admin.global.auth.jwt.JwtAuthentication;
import org.colcum.admin.global.common.api.dto.ApiResponse;
import org.colcum.admin.global.common.application.RedisUserService;
import org.colcum.admin.global.exception.InvalidAuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.colcum.admin.global.auth.api.AuthenticationSuccessHandler.getTokenMap;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final Jwt jwt;
    private final UserService userService;
    private final RedisUserService redisUserService;
    private final ObjectMapper objectMapper;

    @GetMapping("/receivers")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<UserResponseDto>> getReceiversInfo(
        @AuthenticationPrincipal JwtAuthentication authentication
    ) {
        if (Objects.isNull(authentication)) {
            log.info("[Authentication Error]");
            throw new InvalidAuthenticationException("해당 서비스는 로그인 후 사용하실 수 있습니다.");
        }

        return new ApiResponse<>(HttpStatus.OK.value(), "success", userService.getReceivers());
    }

    @PutMapping("/token/refresh")
    @ResponseStatus(HttpStatus.OK)
    public void renewRefreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (Objects.isNull(request.getHeader("refresh"))) {
            throw new IllegalArgumentException("refresh 토큰이 존재하지 않습니다.");
        }

        String refreshTokenKey = request.getHeader("refresh");
        if (redisUserService.isRefreshTokenExpired(refreshTokenKey)) {
            throw new IllegalArgumentException("refresh 토큰이 만료되었습니다.");
        }

        Long userId = redisUserService.getUserIdInRefreshToken(refreshTokenKey);
        String role = redisUserService.getUserRoleInRefreshToken(refreshTokenKey);

        UserEntity user = userService.getUser(userId);

        String accessToken = jwt.sign(Jwt.Claims.of(userId, user.getName(), new String[]{role}));
        RefreshToken refreshToken = redisUserService.renewRefreshToken(refreshTokenKey);
        Map<String, Object> tokens = getTokenMap(accessToken, refreshToken);
        response.getWriter().write(objectMapper.writeValueAsString(tokens));
    }

}
