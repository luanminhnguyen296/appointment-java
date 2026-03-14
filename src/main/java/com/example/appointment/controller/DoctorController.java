
package com.example.appointment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.appointment.service.DoctorService;

@Controller
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @GetMapping("/")
    public String home(Model model){

        model.addAttribute("doctors",doctorService.getAllDoctors());

        return "index";
    }
}
