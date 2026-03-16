package com.example.appointment.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


import com.example.appointment.model.Patient;

import com.example.appointment.service.PatientService;
import com.example.appointment.utils.SnowflakeIdGenerator;

@Controller
public class PatientController {

    @Autowired
    private PatientService patientService;



    @GetMapping("/patients")
    public String listPatients() {
        return "patient/list";
    }

    @GetMapping("/patients/new")
    public String showNewForm(Model model) {
        Patient patient = new Patient();
        patient.setGender("Khác");
        patient.setDateOfBirth(java.time.LocalDate.of(2000, 1, 1));
        model.addAttribute("patient", patient);
        return "patient/form";
    }

    @PostMapping("/patients/save")
    public String savePatient(@ModelAttribute Patient patient) {
        try {
            // 0. Generate Snowflake ID for new patients
            if (patient.getId() == null) {
                patient.setId(SnowflakeIdGenerator.nextId());
            }

            // 1. Logic for image has been simplified to direct URL input in form
            // No additional processing needed as the model attribute handles it.

            // 2. Xử lý Bệnh lý nền (Fix lỗi Choices.js gửi mảng gộp chuỗi)
            List<String> rawConditions = patient.getMedicalCondition();
            if (rawConditions != null && !rawConditions.isEmpty()) {
                List<String> cleanedList = new ArrayList<>();
                for (String cond : rawConditions) {
                    if (cond != null && cond.contains(",")) {
                        String[] parts = cond.split(",");
                        for (String p : parts) {
                            String trimmed = p.trim();
                            if (!trimmed.isEmpty())
                                cleanedList.add(trimmed);
                        }
                    } else if (cond != null && !cond.trim().isEmpty()) {
                        cleanedList.add(cond.trim());
                    }
                }
                patient.setMedicalCondition(cleanedList);
            }

            patientService.savePatient(patient);
        } catch (Exception e) {
            System.err.println("Error saving patient: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/patients";
    }

    @GetMapping("/patients/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        try {
            Patient patient = patientService.getPatientById(id);
            model.addAttribute("patient", patient);
            return "patient/form";
        } catch (Exception e) {
            return "redirect:/patients";
        }
    }

    @GetMapping("/patients/delete/{id}")
    public String deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return "redirect:/patients";
    }

    @GetMapping("/patients/view/{id}")
    public String viewPatient(@PathVariable Long id, Model model) {
        Patient patient = patientService.getPatientById(id);
        model.addAttribute("patient", patient);
        return "patient/detail";
    }

    @GetMapping("/api/v1/patients/list-datatable")
    @ResponseBody
    public Map<String, Object> listDatatable(
            @RequestParam(value = "draw", required = false, defaultValue = "1") int draw,
            @RequestParam(value = "start", required = false, defaultValue = "0") int start,
            @RequestParam(value = "length", required = false, defaultValue = "10") int length,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "gender", required = false) String gender,
            @RequestParam(value = "search[value]", required = false) String searchValue) {

        String search = (keyword != null && !keyword.isEmpty()) ? keyword : searchValue;
        int page = start / length;
        Pageable pageable = PageRequest.of(page, length, Sort.by("id").descending());

        Page<Patient> patientPage = patientService.getPatientsDatatable(search, gender, pageable);
        Map<String, Object> response = new HashMap<>();
        response.put("draw", draw);
        response.put("recordsTotal", patientService.getAllPatients().size());
        response.put("recordsFiltered", patientPage.getTotalElements());
        response.put("data", patientPage.getContent());

        return response;
    }
}
