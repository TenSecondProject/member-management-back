package org.colcum.admin.global.Error.handler;

import org.colcum.admin.global.Error.EmailValidationException;
import org.colcum.admin.global.Error.InvalidAuthenticationException;
import org.colcum.admin.global.Error.PostNotFoundException;
import org.colcum.admin.global.common.api.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = {PostNotFoundException.class, UsernameNotFoundException.class})
    public ApiResponse<Void> handleNotFoundException(Exception ex) {
        return new ApiResponse<>(HttpStatus.NOT_FOUND.value(), ex.getMessage(), null);
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = EmailValidationException.class)
    public ApiResponse<Void> handleEmailValidationException(EmailValidationException ex) {
        return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null);
    }

    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(value = InvalidAuthenticationException.class)
    public ApiResponse<Void> handleInvalidAuthenticationException(InvalidAuthenticationException ex) {
        return new ApiResponse<>(HttpStatus.UNAUTHORIZED.value(), ex.getMessage(), null);
    }

}
