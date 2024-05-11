package org.colcum.admin.global.auth.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.colcum.admin.domain.user.domain.UserEntity;
import org.colcum.admin.global.auth.jwt.Jwt;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import static org.colcum.admin.global.auth.jwt.Jwt.ACCESS_TOKEN_EXPIRY_MINUTE;
import static org.colcum.admin.global.auth.jwt.Jwt.REFRESH_TOKEN_EXPIRY_MINUTE;

@Component
@RequiredArgsConstructor
public class AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final Jwt jwt;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (authentication instanceof UsernamePasswordAuthenticationToken token) {
            UserEntity user = (UserEntity) token.getPrincipal();
            String accessToken = generateToken(user, ACCESS_TOKEN_EXPIRY_MINUTE);
            String refreshToken = generateToken(user, REFRESH_TOKEN_EXPIRY_MINUTE);

            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            Map<String, String> tokens = new HashMap<>();
            tokens.put("access_token", accessToken);
            tokens.put("refresh_token", refreshToken);
            String jsonString = objectMapper.writeValueAsString(tokens);
            PrintWriter out = response.getWriter();
            out.print(jsonString);
            out.flush();
        }
    }

    private String generateToken(UserEntity user, Long expireMinute) {
        return jwt.sign(Jwt.Claims.of(user.getId(), new String[]{user.getUserType().getRole()}), expireMinute);
    }

}
