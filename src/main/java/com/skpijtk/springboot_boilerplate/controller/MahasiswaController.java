package com.skpijtk.springboot_boilerplate.controller;

import com.skpijtk.springboot_boilerplate.dto.mahasiswa.CreateMahasiswaRequest;
import com.skpijtk.springboot_boilerplate.dto.mahasiswa.EditMahasiswaRequest;
import com.skpijtk.springboot_boilerplate.service.MahasiswaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class MahasiswaController {

    private final MahasiswaService mahasiswaService;

    @PostMapping("/add-mahasiswa")
    public ResponseEntity<Object> createMahasiswa(@Valid @RequestBody CreateMahasiswaRequest request) {
        return mahasiswaService.createMahasiswa(request);
    }

    @PutMapping("/edit-mahasiswa/{id}")
    public ResponseEntity<Object> updateMahasiswa(
            @PathVariable("id") Long id,
            @Valid @RequestBody EditMahasiswaRequest request) {
        return mahasiswaService.updateMahasiswa(id, request);
    }
}
