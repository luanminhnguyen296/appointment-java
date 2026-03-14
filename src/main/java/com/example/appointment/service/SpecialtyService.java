package com.example.appointment.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.appointment.model.Specialty;
import com.example.appointment.repository.SpecialtyRepository;

@Service
public class SpecialtyService {

    @Autowired
    private SpecialtyRepository specialtyRepository;

    public List<Specialty> getAllSpecialties() {
        return specialtyRepository.findAll();
    }

    public Specialty getSpecialtyById(Long id) {
        return specialtyRepository.findById(id).orElse(null);
    }

    public void saveSpecialty(Specialty specialty) {
        specialtyRepository.save(specialty);
    }

    public void deleteSpecialty(Long id) {
        specialtyRepository.deleteById(id);
    }

    public Page<Specialty> getSpecialtiesDatatable(String keyword, Pageable pageable) {
        return specialtyRepository.findByKeyword(keyword, pageable);
    }
}
