package com.skpijtk.springboot_boilerplate.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private int statusCode;
    private String status;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(HttpStatus.OK.value(), "OK", message, data);
    }

    public static <T> ApiResponse<T> unauthorized(String message) {
        return new ApiResponse<>(HttpStatus.UNAUTHORIZED.value(), "Unauthorized", message, null);
    }

    public static <T> ApiResponse<T> badRequest(String message) {
        return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Bad Request", message, null);
    }

    public static <T> ApiResponse<T> internalError(String message) {
        return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", message, null);
    }

    public static <T> ApiResponse<T> error(T data, String message, int statusCode, String status) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setData(data);
        response.setMessage(message);
        response.setStatusCode(statusCode);
        response.setStatus(status);
        return response;
    }
}
