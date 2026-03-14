package com.example.appointment.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import com.example.appointment.model.Appointment;

@Service
public class AppointmentService {

    private List<Appointment> appointments = new ArrayList<>();
    private AtomicLong counter = new AtomicLong();

    public List<Appointment> getAll(){
        return appointments;
    }

    public void add(Appointment a){
        a.setId(counter.incrementAndGet());
        appointments.add(a);
    }

    public Appointment getById(Long id){
        return appointments.stream().filter(a->a.getId().equals(id)).findFirst().orElse(null);
    }

    public void update(Appointment updated){

        Appointment a = getById(updated.getId());

        if(a!=null){
            a.setDoctorId(updated.getDoctorId());
            a.setDoctorName(updated.getDoctorName());
            a.setPatientName(updated.getPatientName());
            a.setAge(updated.getAge());
            a.setPhone(updated.getPhone());
            a.setDate(updated.getDate());
            a.setTime(updated.getTime());
            a.setNote(updated.getNote());
        }
    }

    public void delete(Long id){
        appointments.removeIf(a->a.getId().equals(id));
    }
}
