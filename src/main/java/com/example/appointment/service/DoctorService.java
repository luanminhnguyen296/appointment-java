
package com.example.appointment.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.appointment.model.Doctor;

@Service
public class DoctorService {

    private List<Doctor> doctors = new ArrayList<>();

    public DoctorService(){
        doctors.add(new Doctor(1L,"Dr. Smith","Cardiology","Heart specialist"));
        doctors.add(new Doctor(2L,"Dr. Anna","Dermatology","Skin specialist"));
        doctors.add(new Doctor(3L,"Dr. John","Neurology","Brain specialist"));
    }

    public List<Doctor> getAllDoctors(){
        return doctors;
    }

    public Doctor getDoctorById(Long id){
        return doctors.stream().filter(d->d.getId().equals(id)).findFirst().orElse(null);
    }
}
