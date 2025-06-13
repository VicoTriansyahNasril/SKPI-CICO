package com.skpijtk.springboot_boilerplate.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private Long idUser;
    private String name;
    private String role;
    private String token;
}
