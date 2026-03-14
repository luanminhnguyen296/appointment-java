package com.example.appointment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.appointment.service.AppointmentService;
import com.example.appointment.service.DoctorService;
import com.example.appointment.service.PatientService;

@Controller
public class HomeController {

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private AppointmentService appointmentService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("doctors", doctorService.getAllDoctors());
        model.addAttribute("totalPatients", patientService.getAllPatients().size());
        model.addAttribute("totalDoctors", doctorService.getAllDoctors().size());
        model.addAttribute("totalAppointments", appointmentService.getAllAppointments().size());
        return "index";
    }
}
