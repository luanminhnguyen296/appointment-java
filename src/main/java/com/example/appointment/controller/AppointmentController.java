
package com.example.appointment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.appointment.model.Appointment;
import com.example.appointment.service.AppointmentService;
import com.example.appointment.service.DoctorService;

@Controller
@RequestMapping("/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private DoctorService doctorService;

    @GetMapping
    public String list(Model model){

        model.addAttribute("appointments",appointmentService.getAll());

        return "appointment-list";
    }

    @GetMapping("/new")
    public String form(Model model){

        model.addAttribute("appointment",new Appointment());
        model.addAttribute("doctors",doctorService.getAllDoctors());

        return "appointment-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Appointment a){

        if(a.getId()==null){
            appointmentService.add(a);
        }else{
            appointmentService.update(a);
        }

        return "redirect:/appointments";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id,Model model){

        model.addAttribute("appointment",appointmentService.getById(id));
        model.addAttribute("doctors",doctorService.getAllDoctors());

        return "appointment-form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id){

        appointmentService.delete(id);

        return "redirect:/appointments";
    }

    @GetMapping("/view/{id}")
    public String view(@PathVariable Long id,Model model){

        model.addAttribute("appointment",appointmentService.getById(id));

        return "appointment-detail";
    }
}
