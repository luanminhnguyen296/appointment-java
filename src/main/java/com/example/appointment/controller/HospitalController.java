package com.example.appointment.controller;

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

import com.example.appointment.model.Hospital;
import com.example.appointment.service.HospitalService;

@Controller
public class HospitalController {

    @Autowired
    private HospitalService hospitalService;

    @GetMapping("/hospitals")
    public String listHospitals() {
        return "hospital/list";
    }

    @GetMapping("/hospitals/new")
    public String showNewForm(Model model) {
        model.addAttribute("hospital", new Hospital());
        return "hospital/form";
    }

    @PostMapping("/hospitals/save")
    public String saveHospital(@ModelAttribute Hospital hospital) {
        hospitalService.saveHospital(hospital);
        return "redirect:/hospitals";
    }

    @GetMapping("/hospitals/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Hospital hospital = hospitalService.getHospitalById(id);
        model.addAttribute("hospital", hospital);
        return "hospital/form";
    }

    @GetMapping("/hospitals/delete/{id}")
    public String deleteHospital(@PathVariable Long id) {
        hospitalService.deleteHospital(id);
        return "redirect:/hospitals";
    }

    @GetMapping("/api/v1/hospitals/list-datatable")
    @ResponseBody
    public Map<String, Object> listDatatable(
            @RequestParam(value = "draw", required = false, defaultValue = "1") int draw,
            @RequestParam(value = "start", required = false, defaultValue = "0") int start,
            @RequestParam(value = "length", required = false, defaultValue = "10") int length,
            @RequestParam(value = "keyword", required = false) String keyword) {

        int page = start / length;
        Pageable pageable = PageRequest.of(page, length, Sort.by("id").descending());

        Page<Hospital> hospitalPage = hospitalService.getHospitalsDatatable(keyword, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("draw", draw);
        response.put("recordsTotal", hospitalService.getAllHospitals().size());
        response.put("recordsFiltered", hospitalPage.getTotalElements());
        response.put("data", hospitalPage.getContent());

        return response;
    }

    @GetMapping("/api/v1/hospitals/search")
    @ResponseBody
    public List<Hospital> searchHospitals(@RequestParam(value = "q", required = false) String query) {
        Pageable pageable = PageRequest.of(0, 20, Sort.by("name").ascending());
        return hospitalService.getHospitalsDatatable(query, pageable).getContent();
    }
}
