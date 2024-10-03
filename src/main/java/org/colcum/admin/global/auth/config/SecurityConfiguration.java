package org.colcum.admin.global.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.colcum.admin.domain.user.domain.type.UserType;
import org.colcum.admin.global.auth.api.AuthenticationFailureHandler;
import org.colcum.admin.global.auth.api.AuthenticationSuccessHandler;
import org.colcum.admin.global.auth.api.LoggingFilter;
import org.colcum.admin.global.auth.application.UserAuthenticationProvider;
import org.colcum.admin.global.auth.application.UserAuthenticationService;
import org.colcum.admin.global.auth.jwt.Jwt;
import org.colcum.admin.global.auth.jwt.JwtAuthenticationFilter;
import org.colcum.admin.global.auth.jwt.JwtConfigure;
import org.colcum.admin.global.common.application.RedisUserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtConfigure jwtConfigure;
    private final UserAuthenticationService userAuthenticationService;
    private final RedisUserService redisUserService;

    @Value("${cors.origin}")
    private String corsOrigin;

    @Bean
    protected SecurityFilterChain config(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(this.corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(
                request -> request
                    .requestMatchers(HttpMethod.OPTIONS).permitAll()
                    .requestMatchers("/api/v1/users/token/refresh").permitAll()
                    .requestMatchers("/api/v1/notifications/**").permitAll()
                    .requestMatchers("/api/login").permitAll()
                    .requestMatchers("/ping/**").permitAll()
                    .requestMatchers("/api/**").hasAnyRole(UserType.STAFF.name(), UserType.MANAGER.name())
                    .anyRequest().authenticated()
            )
            .addFilterBefore(new JwtAuthenticationFilter(jwt(), userAuthenticationService), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(new LoggingFilter(), SecurityContextHolderFilter.class)
            .formLogin(
                form -> form
                    .loginProcessingUrl("/api/login")
                    .permitAll()
                    .successHandler(new AuthenticationSuccessHandler(jwt(), new ObjectMapper(), redisUserService))
                    .failureHandler(new AuthenticationFailureHandler())
            )
            .sessionManagement(
                config -> config.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );
        return http.build();
    }

    @Bean
    @ConditionalOnProperty(name = "spring.h2.console.enabled", havingValue = "true")
    public WebSecurityCustomizer configureH2ConsoleEnable() {
        return web -> web.ignoring()
            .requestMatchers(PathRequest.toH2Console());
    }

    @Bean
    public WebSecurityCustomizer configureEnableDocs() {
        return web -> web.ignoring()
            .requestMatchers("/docs/**");
    }

    @Bean
    public WebSecurityCustomizer configureActuator() {
        return web -> web.ignoring()
            .requestMatchers("/actuator/**");
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwt(), userAuthenticationService);
    }

    @Bean
    public Jwt jwt() {
        return new Jwt(
            jwtConfigure.getIssuer(),
            jwtConfigure.getClientSecret(),
            jwtConfigure.getExpirySeconds()
        );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(getAuthenticationProvider());
        return authenticationManagerBuilder.build();
    }

    @Bean
    public UserAuthenticationProvider getAuthenticationProvider() {
        return new UserAuthenticationProvider(userAuthenticationService, passwordEncoder());
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(corsOrigin));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);  // 자격 증명 허용 여부
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}
