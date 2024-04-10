package org.colcum.admin.global.auth.config;

import lombok.RequiredArgsConstructor;
import org.colcum.admin.domain.user.domain.UserType;
import org.colcum.admin.global.auth.api.AuthenticationSuccessHandler;
import org.colcum.admin.global.auth.api.LoggingFilter;
import org.colcum.admin.global.auth.application.UserAuthenticationProvider;
import org.colcum.admin.global.auth.application.UserAuthenticationService;
import org.colcum.admin.global.auth.jwt.Jwt;
import org.colcum.admin.global.auth.jwt.JwtAuthenticationFilter;
import org.colcum.admin.global.auth.jwt.JwtConfigure;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextHolderFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtConfigure jwtConfigure;
    private final UserAuthenticationService userAuthenticationService;

    @Bean
    protected SecurityFilterChain config(HttpSecurity http) throws Exception {
        http
            .cors(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(
                request -> request
                    .requestMatchers("/login").permitAll()
                    .requestMatchers("/docs/**").permitAll()
                    .requestMatchers("/api/**").hasRole(UserType.STAFF.name())
                    .anyRequest().authenticated()
            )
            .addFilterBefore(new JwtAuthenticationFilter(jwt(), userAuthenticationService), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(new LoggingFilter(), SecurityContextHolderFilter.class)
            .formLogin(
                form -> form
                    .permitAll()
                    .successHandler(new AuthenticationSuccessHandler(jwt()))
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

}
