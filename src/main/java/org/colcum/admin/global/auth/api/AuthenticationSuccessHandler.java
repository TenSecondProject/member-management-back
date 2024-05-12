package org.colcum.admin.global.auth.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.colcum.admin.domain.user.domain.UserEntity;
import org.colcum.admin.global.auth.api.dto.RefreshToken;
import org.colcum.admin.global.auth.jwt.Jwt;
import org.colcum.admin.global.common.application.RedisService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final Jwt jwt;
    private final ObjectMapper objectMapper;
    private final RedisService redisService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (authentication instanceof UsernamePasswordAuthenticationToken token) {
            UserEntity user = (UserEntity) token.getPrincipal();
            String accessToken = generateToken(user);
            RefreshToken refreshToken = redisService.createRefreshToken();
            redisService.saveRefreshToken(refreshToken, user);

            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");

            Map<String, Object> tokens = new HashMap<>();
            tokens.put("access_token", accessToken);
            tokens.put("refresh_token", refreshToken);
            String jsonString = objectMapper.writeValueAsString(tokens);
            PrintWriter out = response.getWriter();
            out.print(jsonString);
            out.flush();
        }
    }

    private String generateToken(UserEntity user) {
        return jwt.sign(Jwt.Claims.of(user.getId(), new String[]{user.getUserType().getRole()}));
    }

}
