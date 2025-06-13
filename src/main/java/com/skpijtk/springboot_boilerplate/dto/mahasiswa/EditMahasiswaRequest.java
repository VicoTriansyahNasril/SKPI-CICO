package com.skpijtk.springboot_boilerplate.dto.mahasiswa;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditMahasiswaRequest {

    @NotBlank(message = "Student name is required")
    @Size(max = 50, message = "Student name must be less than 50 characters")
    @Pattern(
        regexp = "^(?!.*\\s$)([A-Za-z]{1,50})\\s([A-Za-z]{1,50})$",
        message = "Student name must be two words (first and last name), only letters, no trailing space"
    )
    private String studentName;

    @NotBlank(message = "NIM is required")
    @Pattern(regexp = "^\\d{9}$", message = "NIM must be exactly 9 digits")
    private String nim;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 50, message = "Email must be less than 50 characters")
    @Pattern(
        regexp = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$",
        message = "Email format is invalid"
    )
    private String email;
}
