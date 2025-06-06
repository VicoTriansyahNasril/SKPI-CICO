package com.skpijtk.springboot_boilerplate.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponse {
    private Long idUser;
    private String name;
    private String role;
    private String token;
}
