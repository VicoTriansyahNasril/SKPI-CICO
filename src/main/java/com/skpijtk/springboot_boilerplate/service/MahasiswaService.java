package com.skpijtk.springboot_boilerplate.service;

import com.skpijtk.springboot_boilerplate.dto.mahasiswa.CreateMahasiswaRequest;
import com.skpijtk.springboot_boilerplate.dto.mahasiswa.EditMahasiswaRequest;
import org.springframework.http.ResponseEntity;

public interface MahasiswaService {
    ResponseEntity<Object> createMahasiswa(CreateMahasiswaRequest request);
    ResponseEntity<Object> updateMahasiswa(Long id, EditMahasiswaRequest request);
}
