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

import com.example.appointment.model.Specialty;
import com.example.appointment.service.SpecialtyService;

@Controller
public class SpecialtyController {

    @Autowired
    private SpecialtyService specialtyService;

    @GetMapping("/specialties")
    public String listSpecialties() {
        return "specialty/list";
    }

    @GetMapping("/specialties/new")
    public String showNewForm(Model model) {
        model.addAttribute("specialty", new Specialty());
        return "specialty/form";
    }

    @PostMapping("/specialties/save")
    public String saveSpecialty(@ModelAttribute Specialty specialty) {
        specialtyService.saveSpecialty(specialty);
        return "redirect:/specialties";
    }

    @GetMapping("/specialties/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Specialty specialty = specialtyService.getSpecialtyById(id);
        model.addAttribute("specialty", specialty);
        return "specialty/form";
    }

    @GetMapping("/specialties/delete/{id}")
    public String deleteSpecialty(@PathVariable Long id) {
        specialtyService.deleteSpecialty(id);
        return "redirect:/specialties";
    }

    @GetMapping("/api/v1/specialties/list-datatable")
    @ResponseBody
    public Map<String, Object> listDatatable(
            @RequestParam(value = "draw", required = false, defaultValue = "1") int draw,
            @RequestParam(value = "start", required = false, defaultValue = "0") int start,
            @RequestParam(value = "length", required = false, defaultValue = "10") int length,
            @RequestParam(value = "keyword", required = false) String keyword) {

        int page = start / length;
        Pageable pageable = PageRequest.of(page, length, Sort.by("id").descending());

        Page<Specialty> pageData = specialtyService.getSpecialtiesDatatable(keyword, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("draw", draw);
        response.put("recordsTotal", specialtyService.getAllSpecialties().size());
        response.put("recordsFiltered", pageData.getTotalElements());
        response.put("data", pageData.getContent());

        return response;
    }

    @GetMapping("/api/v1/specialties/search")
    @ResponseBody
    public List<Specialty> searchSpecialties(@RequestParam(value = "q", required = false) String query) {
        Pageable pageable = PageRequest.of(0, 20, Sort.by("name").ascending());
        return specialtyService.getSpecialtiesDatatable(query, pageable).getContent();
    }
}
