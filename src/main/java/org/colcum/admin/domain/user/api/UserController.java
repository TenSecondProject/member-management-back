package org.colcum.admin.domain.user.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.colcum.admin.global.auth.api.dto.RefreshToken;
import org.colcum.admin.global.auth.jwt.Jwt;
import org.colcum.admin.global.common.application.RedisUserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import static org.colcum.admin.global.auth.api.AuthenticationSuccessHandler.getTokenMap;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final Jwt jwt;
    private final RedisUserService redisUserService;
    private final ObjectMapper objectMapper;

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

        String accessToken = jwt.sign(Jwt.Claims.of(userId, new String[]{role}));
        RefreshToken refreshToken = redisUserService.renewRefreshToken(refreshTokenKey);
        Map<String, Object> tokens = getTokenMap(accessToken, refreshToken);
        response.getWriter().write(objectMapper.writeValueAsString(tokens));
    }

}
