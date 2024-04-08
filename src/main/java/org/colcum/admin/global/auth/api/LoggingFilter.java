package org.colcum.admin.global.auth.api;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
@Component
public class LoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String threadName = Thread.currentThread().getName();
        String ipAddress = request.getRemoteAddr();
        String requestURI = request.getRequestURI();
        logger.info("Thread: " + threadName + ", IP: " + ipAddress + ", Path: " + requestURI);
        filterChain.doFilter(request, response);
    }

}
