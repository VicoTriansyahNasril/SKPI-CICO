package com.skpijtk.springboot_boilerplate;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(info = @Info(
    title = "SKPI Attendance API",
    version = "1.0.0",
    description = "Dokumentasi API untuk sistem kehadiran mahasiswa"
))
@SpringBootApplication
public class SpringBootBoilerplateApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootBoilerplateApplication.class, args);
    }
}
