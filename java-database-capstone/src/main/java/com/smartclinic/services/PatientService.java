package com.smartclinic.services;

import com.smartclinic.models.mysql.Patient;
import com.smartclinic.repositories.mysql.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PatientService {
    
    @Autowired
    private PatientRepository patientRepository;
    
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }
    
    public Optional<Patient> getPatientById(Long id) {
        return patientRepository.findById(id);
    }
    
    public Optional<Patient> getPatientByEmail(String email) {
        return patientRepository.findByEmail(email);
    }
    
    public List<Patient> searchPatientsByName(String name) {
        return patientRepository.findByNameContaining(name);
    }
    
    public List<Patient> searchPatientsByLastName(String lastName) {
        return patientRepository.findByLastNameContainingIgnoreCase(lastName);
    }
    
    public Patient createPatient(Patient patient) {
        // Check if email already exists
        if (patientRepository.findByEmail(patient.getEmail()).isPresent()) {
            throw new RuntimeException("Patient with email already exists: " + patient.getEmail());
        }
        return patientRepository.save(patient);
    }
    
    public Patient updatePatient(Long id, Patient patientDetails) {
        Patient patient = patientRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Patient not found with id: " + id));
        
        patient.setDateOfBirth(patientDetails.getDateOfBirth());
        patient.setBloodType(patientDetails.getBloodType());
        patient.setEmergencyContact(patientDetails.getEmergencyContact());
        patient.setInsuranceInfo(patientDetails.getInsuranceInfo());
        patient.setAddress(patientDetails.getAddress());
        patient.setPhone(patientDetails.getPhone());
        
        return patientRepository.save(patient);
    }
    
    public void deletePatient(Long id) {
        Patient patient = patientRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Patient not found with id: " + id));
        patientRepository.delete(patient);
    }
    
    public Long getTotalPatientCount() {
        return patientRepository.getTotalPatientCount();
    }
    
    public List<Patient> getPatientsWithEmergencyContact() {
        return patientRepository.findPatientsWithEmergencyContact();
    }
}