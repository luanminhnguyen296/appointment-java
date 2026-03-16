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

import com.example.appointment.model.Doctor;
import com.example.appointment.service.DoctorService;
import com.example.appointment.service.HospitalService;
import com.example.appointment.service.SpecialtyService;

@Controller
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private SpecialtyService specialtyService;

    @Autowired
    private HospitalService hospitalService;

    @GetMapping("/doctors")
    public String listDoctors() {
        return "doctor/list";
    }

    @GetMapping("/doctors/new")
    public String showNewForm(Model model) {
        model.addAttribute("doctor", new Doctor());
        model.addAttribute("specialties", specialtyService.getAllSpecialties());
        model.addAttribute("hospitals", hospitalService.getAllHospitals());
        return "doctor/form";
    }

    @PostMapping("/doctors/save")
    public String saveDoctor(@ModelAttribute Doctor doctor) {
        doctorService.saveDoctor(doctor);
        return "redirect:/doctors";
    }

    @GetMapping("/doctors/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Doctor doctor = doctorService.getDoctorById(id);
        model.addAttribute("doctor", doctor);
        model.addAttribute("specialties", specialtyService.getAllSpecialties());
        model.addAttribute("hospitals", hospitalService.getAllHospitals());
        return "doctor/form";
    }

    @GetMapping("/doctors/delete/{id}")
    public String deleteDoctor(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
        return "redirect:/doctors";
    }

    @GetMapping("/api/v1/doctors/list-datatable")
    @ResponseBody
    public Map<String, Object> listDatatable(
            @RequestParam(value = "draw", required = false, defaultValue = "1") int draw,
            @RequestParam(value = "start", required = false, defaultValue = "0") int start,
            @RequestParam(value = "length", required = false, defaultValue = "10") int length,
            @RequestParam(value = "keyword", required = false) String keyword) {

        int page = start / length;
        Pageable pageable = PageRequest.of(page, length, Sort.by("id").descending());

        Page<Doctor> doctorPage = doctorService.getDoctorsDatatable(keyword, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("draw", draw);
        response.put("recordsTotal", doctorService.getAllDoctors().size());
        response.put("recordsFiltered", doctorPage.getTotalElements());
        response.put("data", doctorPage.getContent());

        return response;
    }
}
