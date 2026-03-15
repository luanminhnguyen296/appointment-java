package com.example.appointment.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
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

import com.example.appointment.model.Appointment;
import com.example.appointment.service.AppointmentService;
import com.example.appointment.service.DoctorService;
import com.example.appointment.service.PatientService;
import com.example.appointment.service.SpecialtyService;

@Controller
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private SpecialtyService specialtyService;

    @GetMapping("/appointments")
    public String listAppointments(Model model) {
        model.addAttribute("specialties", specialtyService.getAllSpecialties());
        model.addAttribute("doctors", doctorService.getAllDoctors());
        model.addAttribute("patients", patientService.getAllPatients());
        return "appointment/list";
    }

    @GetMapping("/appointments/new")
    public String showNewForm(Model model) {
        model.addAttribute("appointment", new Appointment());
        model.addAttribute("doctors", doctorService.getAllDoctors());
        model.addAttribute("patients", patientService.getAllPatients());
        model.addAttribute("specialties", specialtyService.getAllSpecialties());
        return "appointment/form";
    }

    @PostMapping("/appointments/save")
    public String saveAppointment(@ModelAttribute Appointment appointment) {
        appointmentService.saveAppointment(appointment);
        return "redirect:/appointments";
    }

    @GetMapping("/appointments/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Appointment appointment = appointmentService.getAppointmentById(id);
        model.addAttribute("appointment", appointment);
        model.addAttribute("doctors", doctorService.getAllDoctors());
        model.addAttribute("patients", patientService.getAllPatients());
        model.addAttribute("specialties", specialtyService.getAllSpecialties());
        return "appointment/form";
    }

    @GetMapping("/appointments/delete/{id}")
    public String deleteAppointment(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
        return "redirect:/appointments";
    }

    @GetMapping("/api/v1/appointments/list-datatable")
    @ResponseBody
    public Map<String, Object> listDatatable(
            @RequestParam(value = "draw", required = false, defaultValue = "1") int draw,
            @RequestParam(value = "start", required = false, defaultValue = "0") int start,
            @RequestParam(value = "length", required = false, defaultValue = "10") int length,
            @RequestParam(value = "keyword", required = false) String keyword) {

        int page = start / length;
        Pageable pageable = PageRequest.of(page, length, Sort.by("id").descending());

        Page<Appointment> appointmentPage = appointmentService.getAppointmentsDatatable(keyword, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("draw", draw);
        response.put("recordsTotal", appointmentService.getAllAppointments().size());
        response.put("recordsFiltered", appointmentPage.getTotalElements());
        response.put("data", appointmentPage.getContent());

        return response;
    }

    @GetMapping("/api/v1/appointments/calendar")
    @ResponseBody
    public java.util.List<Map<String, Object>> calendarEvents(
            @RequestParam(value = "specialtyId", required = false) Long specialtyId,
            @RequestParam(value = "doctorId", required = false) Long doctorId,
            @RequestParam(value = "patientId", required = false) Long patientId,
            @RequestParam(value = "start") String startStr,
            @RequestParam(value = "end") String endStr) {

        // fullCalendar sends ISO dates like 2023-01-01T00:00:00Z
        LocalDate startDate = ZonedDateTime.parse(startStr).toLocalDate();
        LocalDate endDate = ZonedDateTime.parse(endStr).toLocalDate();

        List<Appointment> appointments = appointmentService.getCalendarEvents(specialtyId, doctorId,
                patientId, startDate, endDate);
        List<Map<String, Object>> events = new ArrayList<>();

        for (Appointment a : appointments) {
            Map<String, Object> event = new HashMap<>();
            event.put("id", a.getId());

            // Title: Patient Name
            event.put("title", a.getPatient().getFullName());

            // Start: Combine date and time
            LocalDateTime startDateTime = LocalDateTime.of(a.getAppointmentDate(),
                    a.getAppointmentTime());
            event.put("start", startDateTime.toString());

            // End: assume 30 mins duration for UI display
            event.put("end", startDateTime.plusMinutes(30).toString());

            // Colors based on status
            String color = "#0284c7"; // Blue (Pending)
            if ("CONFIRMED".equals(a.getStatus()))
                color = "#10b981"; // Green
            if ("CANCELLED".equals(a.getStatus()))
                color = "#f43f5e"; // Red

            event.put("backgroundColor", color);
            event.put("borderColor", color);

            // Extended Data
            Map<String, Object> props = new HashMap<>();
            props.put("patientName", a.getPatient().getFullName());
            props.put("patientAvatar", a.getPatient().getImageUrl());
            props.put("doctorName", a.getDoctor().getName());
            props.put("doctorAvatar", a.getDoctor().getImageUrl());
            props.put("reason", a.getReason());
            props.put("status", a.getStatus());
            event.put("extendedProps", props);

            events.add(event);
        }

        return events;
    }
}
