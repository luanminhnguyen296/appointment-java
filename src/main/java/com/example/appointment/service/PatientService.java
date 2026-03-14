package com.example.appointment.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.appointment.model.Patient;
import com.example.appointment.repository.PatientRepository;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public Patient getPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bệnh nhân với ID: " + id));
    }

    public Patient savePatient(Patient patient) {
        // Kiểm tra logic nghiệp vụ (ví dụ: số điện thoại không được trùng)
        if (patient.getId() == null && patientRepository.existsByPhone(patient.getPhone())) {
            throw new RuntimeException("Số điện thoại này đã được đăng ký!");
        }
        return patientRepository.save(patient);
    }

    public Patient updatePatient(Long id, Patient patientDetails) {
        Patient patient = getPatientById(id);
        patient.setFullName(patientDetails.getFullName());
        patient.setPhone(patientDetails.getPhone());
        patient.setEmail(patientDetails.getEmail());
        patient.setDateOfBirth(patientDetails.getDateOfBirth());
        patient.setAddress(patientDetails.getAddress());
        patient.setGender(patientDetails.getGender());
        return patientRepository.save(patient);
    }

    public void deletePatient(Long id) {
        if (!patientRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy bệnh nhân để xóa!");
        }
        patientRepository.deleteById(id);
    }

    public List<Patient> searchPatients(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return getAllPatients();
        }
        return patientRepository.findByFullNameContainingIgnoreCaseOrPhoneContainingOrEmailContainingIgnoreCase(keyword,
                keyword, keyword);
    }

    public Page<Patient> getPatientsDatatable(String keyword, Pageable pageable) {
        if (keyword == null || keyword.isEmpty()) {
            return patientRepository.findAll(pageable);
        }
        return patientRepository.findByFullNameContainingIgnoreCaseOrPhoneContainingOrEmailContainingIgnoreCase(keyword,
                keyword, keyword, pageable);
    }

    public boolean isPhoneExists(String phone) {
        return patientRepository.existsByPhone(phone);
    }

    public Patient getPatientByEmail(String email) {
        return patientRepository.findByEmail(email);
    }
}
