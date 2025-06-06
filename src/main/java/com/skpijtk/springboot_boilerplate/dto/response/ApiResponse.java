package com.skpijtk.springboot_boilerplate.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ApiResponse<T> {
    private int statusCode;
    private String message;
    private String status;
    private T data;
}
