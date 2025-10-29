package com.smartclinic.services;

import com.smartclinic.models.mongo.Prescription;
import com.smartclinic.repositories.mongo.PrescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PrescriptionService {
    
    @Autowired
    private PrescriptionRepository prescriptionRepository;
    
    public List<Prescription> getAllPrescriptions() {
        return prescriptionRepository.findAll();
    }
    
    public Optional<Prescription> getPrescriptionById(String id) {
        return prescriptionRepository.findById(id);
    }
    
    public List<Prescription> getPrescriptionsByPatientId(Long patientId) {
        return prescriptionRepository.findByPatientId(patientId);
    }
    
    public List<Prescription> getPrescriptionsByDoctorId(Long doctorId) {
        return prescriptionRepository.findByDoctorId(doctorId);
    }
    
    public List<Prescription> getPrescriptionsByAppointmentId(Long appointmentId) {
        return prescriptionRepository.findByAppointmentId(appointmentId);
    }
    
    public List<Prescription> getActivePrescriptions() {
        return prescriptionRepository.findByIsActive(true);
    }
    
    public List<Prescription> getPatientPrescriptionsInDateRange(Long patientId, LocalDateTime startDate, LocalDateTime endDate) {
        return prescriptionRepository.findPatientPrescriptionsInDateRange(patientId, startDate, endDate);
    }
    
    public List<Prescription> getDoctorPrescriptionsByDiagnosis(Long doctorId, String diagnosis) {
        return prescriptionRepository.findByDoctorIdAndDiagnosisContaining(doctorId, diagnosis);
    }
    
    public List<Prescription> getLatestPrescriptionsByPatientId(Long patientId) {
        return prescriptionRepository.findLatestPrescriptionsByPatientId(patientId);
    }
    
    public Prescription createPrescription(Prescription prescription) {
        // Set timestamps
        prescription.setPrescriptionDate(LocalDateTime.now());
        prescription.setCreatedAt(LocalDateTime.now());
        
        return prescriptionRepository.save(prescription);
    }
    
    public Prescription updatePrescription(String id, Prescription prescriptionDetails) {
        Prescription prescription = prescriptionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Prescription not found with id: " + id));
        
        prescription.setDiagnosis(prescriptionDetails.getDiagnosis());
        prescription.setMedications(prescriptionDetails.getMedications());
        prescription.setInstructions(prescriptionDetails.getInstructions());
        prescription.setIsActive(prescriptionDetails.getIsActive());
        
        return prescriptionRepository.save(prescription);
    }
    
    public void deletePrescription(String id) {
        Prescription prescription = prescriptionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Prescription not found with id: " + id));
        prescriptionRepository.delete(prescription);
    }
    
    public void deactivatePrescription(String id) {
        Prescription prescription = prescriptionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Prescription not found with id: " + id));
        prescription.setIsActive(false);
        prescriptionRepository.save(prescription);
    }
    
    public Long getPrescriptionCountByPatient(Long patientId) {
        return prescriptionRepository.countByPatientId(patientId);
    }
    
    public Long getPrescriptionCountByDoctor(Long doctorId) {
        return prescriptionRepository.countByDoctorId(doctorId);
    }
}