package com.smartclinic.models.mysql;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "doctors")
@PrimaryKeyJoinColumn(name = "user_id")
public class Doctor extends User {
    
    private String specialization;
    
    @Column(name = "license_number", unique = true)
    private String licenseNumber;
    
    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;
    
    private String qualification;
    
    @Column(name = "consultation_fee")
    private Double consultationFee;
    
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Appointment> appointments = new ArrayList<>();
    
    // Constructors
    public Doctor() {
        super();
    }
    
    public Doctor(String username, String email, String password, String firstName, String lastName, String specialization) {
        super(username, email, password, firstName, lastName, Role.DOCTOR);
        this.specialization = specialization;
    }
    
    // Getters and Setters
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    
    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
    
    public Integer getYearsOfExperience() { return yearsOfExperience; }
    public void setYearsOfExperience(Integer yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; }
    
    public String getQualification() { return qualification; }
    public void setQualification(String qualification) { this.qualification = qualification; }
    
    public Double getConsultationFee() { return consultationFee; }
    public void setConsultationFee(Double consultationFee) { this.consultationFee = consultationFee; }
    
    public List<Appointment> getAppointments() { return appointments; }
    public void setAppointments(List<Appointment> appointments) { this.appointments = appointments; }
}