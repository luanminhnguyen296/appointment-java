package com.example.appointment.controller;

import java.util.HashMap;
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
        patientService.savePatient(patient);
        return "redirect:/patients";
    }

    @GetMapping("/patients/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Patient patient = patientService.getPatientById(id);
        model.addAttribute("patient", patient);
        return "patient/form";
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

        // Ưu tiên lấy keyword từ d.keyword của client, nếu không có thì lấy từ
        // search[value] mặc định của DataTable
        String search = (keyword != null && !keyword.isEmpty()) ? keyword : searchValue;

        // Tính toán trang
        int page = start / length;
        Pageable pageable = PageRequest.of(page, length, Sort.by("id").descending());

        Page<Patient> patientPage = patientService.getPatientsDatatable(search, gender, pageable);
        long totalRecords = patientService.getAllPatients().size(); // Hoặc thêm method count() vào service

        Map<String, Object> response = new HashMap<>();
        response.put("draw", draw);
        response.put("recordsTotal", totalRecords);
        response.put("recordsFiltered", patientPage.getTotalElements());
        response.put("data", patientPage.getContent());

        return response;
    }
}
