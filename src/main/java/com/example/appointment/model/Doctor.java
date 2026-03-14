
package com.example.appointment.model;

public class Doctor {

    private Long id;
    private String name;
    private String specialty;
    private String description;

    public Doctor(){}

    public Doctor(Long id,String name,String specialty,String description){
        this.id=id;
        this.name=name;
        this.specialty=specialty;
        this.description=description;
    }

    public Long getId(){return id;}
    public void setId(Long id){this.id=id;}

    public String getName(){return name;}
    public void setName(String name){this.name=name;}

    public String getSpecialty(){return specialty;}
    public void setSpecialty(String specialty){this.specialty=specialty;}

    public String getDescription(){return description;}
    public void setDescription(String description){this.description=description;}
}
