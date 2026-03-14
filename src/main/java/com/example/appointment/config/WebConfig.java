package com.example.appointment.config;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.appointment.model.Hospital;
import com.example.appointment.model.Specialty;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadDir = Paths.get("uploads");
        String uploadPath = uploadDir.toFile().getAbsolutePath();

        registry.addResourceHandler("/uploads/**").addResourceLocations("file:" + uploadPath + "/");
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        // Converter for Hospital from ID String
        registry.addConverter(String.class, Hospital.class, source -> {
            if (source == null || source.isEmpty())
                return null;
            return new Hospital(Long.valueOf(source));
        });

        // Converter for Specialty from ID String
        registry.addConverter(String.class, Specialty.class, source -> {
            if (source == null || source.isEmpty())
                return null;
            return new Specialty(Long.valueOf(source));
        });
    }
}
