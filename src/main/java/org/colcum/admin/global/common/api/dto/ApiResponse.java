package org.colcum.admin.global.common.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {

    private int statusCode;
    private String message;
    private T data;

}

