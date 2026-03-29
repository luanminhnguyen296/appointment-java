package com.example.appointment.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.appointment.model.Hospital;
import com.example.appointment.model.Specialty;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        // Converter for Hospital from ID String
        registry.addConverter(String.class, Hospital.class, source -> {
            if (source == null || source.isEmpty() || source.equals("none"))
                return null;
            return new Hospital(Long.valueOf(source));
        });

        // Converter for Specialty from ID String
        registry.addConverter(String.class, Specialty.class, source -> {
            if (source == null || source.isEmpty() || source.equals("none"))
                return null;
            return new Specialty(Long.valueOf(source));
        });
    }

    @Override
    public void addResourceHandlers(org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry registry) {
        Path userUploadDir = Paths.get("./uploads/users");
        String userUploadPath = userUploadDir.toFile().getAbsolutePath();
        registry.addResourceHandler("/uploads/users/**").addResourceLocations("file:" + userUploadPath + "/");
    }
}
