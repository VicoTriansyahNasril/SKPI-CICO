package com.skpijtk.springboot_boilerplate.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.LinkedHashMap;
import java.util.Map;

public class ResponseHandler {
    public static ResponseEntity<Object> generateResponse(String message, HttpStatus status, Object responseObj) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("data", responseObj);
        map.put("message", message);
        map.put("statusCode", status.value());
        map.put("status", status.getReasonPhrase());
        return new ResponseEntity<>(map, status);
    }
}
