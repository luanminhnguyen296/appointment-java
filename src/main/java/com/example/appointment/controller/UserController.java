package com.example.appointment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.appointment.model.User;
import com.example.appointment.repository.UserRepository;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "users/list"; // thymeleaf template
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("user", new User());
        return "users/add";
    }

    @PostMapping("/add")
    public String addUser(@ModelAttribute User user, @org.springframework.web.bind.annotation.RequestParam(value="avatarFile", required=false) org.springframework.web.multipart.MultipartFile multipartFile) throws java.io.IOException {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (multipartFile != null && !multipartFile.isEmpty()) {
            String fileName = com.example.appointment.utils.FileUploadUtil.saveFile("uploads/users", multipartFile);
            user.setImageUrl("/uploads/users/" + fileName);
        }
        userRepository.save(user);
        return "redirect:/users";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        model.addAttribute("user", user);
        return "users/edit";
    }

    @PostMapping("/update/{id}")
    public String updateUser(@PathVariable("id") long id, @ModelAttribute User user, @org.springframework.web.bind.annotation.RequestParam(value="avatarFile", required=false) org.springframework.web.multipart.MultipartFile multipartFile) throws java.io.IOException {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));

        existingUser.setUsername(user.getUsername());
        existingUser.setRole(user.getRole());

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        if (multipartFile != null && !multipartFile.isEmpty()) {
            String fileName = com.example.appointment.utils.FileUploadUtil.saveFile("uploads/users", multipartFile);
            existingUser.setImageUrl("/uploads/users/" + fileName);
        }

        userRepository.save(existingUser);
        return "redirect:/users";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") long id, Model model) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        userRepository.delete(user);
        return "redirect:/users";
    }
}
