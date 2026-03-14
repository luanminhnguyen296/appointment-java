
package com.example.appointment.model;

public class Appointment {

    private Long id;
    private Long doctorId;
    private String doctorName;
    private String patientName;
    private int age;
    private String phone;
    private String date;
    private String time;
    private String note;

    public Long getId(){return id;}
    public void setId(Long id){this.id=id;}

    public Long getDoctorId(){return doctorId;}
    public void setDoctorId(Long doctorId){this.doctorId=doctorId;}

    public String getDoctorName(){return doctorName;}
    public void setDoctorName(String doctorName){this.doctorName=doctorName;}

    public String getPatientName(){return patientName;}
    public void setPatientName(String patientName){this.patientName=patientName;}

    public int getAge(){return age;}
    public void setAge(int age){this.age=age;}

    public String getPhone(){return phone;}
    public void setPhone(String phone){this.phone=phone;}

    public String getDate(){return date;}
    public void setDate(String date){this.date=date;}

    public String getTime(){return time;}
    public void setTime(String time){this.time=time;}

    public String getNote(){return note;}
    public void setNote(String note){this.note=note;}
}
