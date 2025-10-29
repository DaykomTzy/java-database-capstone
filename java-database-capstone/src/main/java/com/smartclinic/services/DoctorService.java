package com.smartclinic.services;

import com.smartclinic.models.mysql.Doctor;
import com.smartclinic.repositories.mysql.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DoctorService {
    
    @Autowired
    private DoctorRepository doctorRepository;
    
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }
    
    public Optional<Doctor> getDoctorById(Long id) {
        return doctorRepository.findById(id);
    }
    
    public Optional<Doctor> getDoctorByLicenseNumber(String licenseNumber) {
        return doctorRepository.findByLicenseNumber(licenseNumber);
    }
    
    public List<Doctor> getDoctorsBySpecialization(String specialization) {
        return doctorRepository.findBySpecialization(specialization);
    }
    
    public List<Doctor> getDoctorsByExperience(Integer minYears) {
        return doctorRepository.findByYearsOfExperienceGreaterThanEqual(minYears);
    }
    
    public List<Doctor> getDoctorsBySpecializationAndExperience(String specialization, Integer minExperience) {
        return doctorRepository.findBySpecializationAndMinExperience(specialization, minExperience);
    }
    
    public Doctor createDoctor(Doctor doctor) {
        // Check if license number already exists
        if (doctorRepository.findByLicenseNumber(doctor.getLicenseNumber()).isPresent()) {
            throw new RuntimeException("Doctor with license number already exists: " + doctor.getLicenseNumber());
        }
        return doctorRepository.save(doctor);
    }
    
    public Doctor updateDoctor(Long id, Doctor doctorDetails) {
        Doctor doctor = doctorRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + id));
        
        doctor.setSpecialization(doctorDetails.getSpecialization());
        doctor.setYearsOfExperience(doctorDetails.getYearsOfExperience());
        doctor.setQualification(doctorDetails.getQualification());
        doctor.setConsultationFee(doctorDetails.getConsultationFee());
        doctor.setPhone(doctorDetails.getPhone());
        
        return doctorRepository.save(doctor);
    }
    
    public void deleteDoctor(Long id) {
        Doctor doctor = doctorRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + id));
        doctorRepository.delete(doctor);
    }
    
    public List<String> getAllSpecializations() {
        return doctorRepository.findAllSpecializations();
    }
    
    public Long getDoctorCountBySpecialization(String specialization) {
        return doctorRepository.countBySpecialization(specialization);
    }
}