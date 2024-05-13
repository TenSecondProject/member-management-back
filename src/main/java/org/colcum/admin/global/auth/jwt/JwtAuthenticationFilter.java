package org.colcum.admin.global.auth.jwt;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.colcum.admin.domain.user.domain.UserEntity;
import org.colcum.admin.global.auth.application.UserAuthenticationService;
import org.colcum.admin.global.common.api.dto.ApiResponse;
import org.colcum.admin.global.common.application.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final Jwt jwt;

    private final UserAuthenticationService userAuthenticationService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtAuthenticationFilter(Jwt jwt, UserAuthenticationService userAuthenticationService) {
        this.jwt = jwt;
        this.userAuthenticationService = userAuthenticationService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            String token = getToken(request);
            if (token != null) {
                try {
                    Jwt.Claims claims = verify(token);
                    log.debug("Jwt parse result: {}", claims);

                    Long userId = claims.userId;
                    List<GrantedAuthority> authorities = getAuthorities(claims);

                    if (userId != null && authorities.size() > 0) {
                        UserEntity userEntity = userAuthenticationService.loadUserById(userId);

                        JwtAuthenticationToken authentication
                            = new JwtAuthenticationToken(new JwtAuthentication(token, userEntity), null, authorities);
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }

                } catch (TokenExpiredException e) {
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write(objectMapper.writeValueAsString(new ApiResponse<Void>(HttpStatus.FORBIDDEN.value(), e.getMessage(), null)));
                } catch (Exception e) {
                    log.error("Jwt processing failed: {}", e.getMessage());
                }
            }
        } else {
            log.debug(
                "SecurityContextHolder not populated with security token, as it already contained: {}",
                SecurityContextHolder.getContext().getAuthentication()
            );
        }

        filterChain.doFilter(request, response);
    }

    private String getToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            log.debug("Jwt token detected: {}", token);
            try {
                String jwtToken = token.substring(7);
                return URLDecoder.decode(jwtToken, StandardCharsets.UTF_8);
            } catch (IllegalArgumentException e) {
                log.error("Unable to get JWT Token");
            } catch (TokenExpiredException e) {
                log.warn("JWT Token has expired");
            }
        }
        return null;
    }

    private Jwt.Claims verify(String token) {
        return jwt.verify(token);
    }

    private List<GrantedAuthority> getAuthorities(Jwt.Claims claims) {
        String[] roles = claims.roles;
        return roles == null || roles.length == 0
            ? emptyList()
            : Arrays.stream(roles).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

}
