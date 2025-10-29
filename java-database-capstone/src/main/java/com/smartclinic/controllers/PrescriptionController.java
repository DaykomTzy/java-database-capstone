package com.smartclinic.controllers;

import com.smartclinic.models.mongo.Prescription;
import com.smartclinic.services.PrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/prescriptions")
@CrossOrigin(origins = "http://localhost:3000")
public class PrescriptionController {
    
    @Autowired
    private PrescriptionService prescriptionService;
    
    @GetMapping
    public ResponseEntity<List<Prescription>> getAllPrescriptions() {
        List<Prescription> prescriptions = prescriptionService.getAllPrescriptions();
        return ResponseEntity.ok(prescriptions);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Prescription> getPrescriptionById(@PathVariable String id) {
        Optional<Prescription> prescription = prescriptionService.getPrescriptionById(id);
        return prescription.map(ResponseEntity::ok)
                          .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Prescription>> getPrescriptionsByPatientId(@PathVariable Long patientId) {
        List<Prescription> prescriptions = prescriptionService.getPrescriptionsByPatientId(patientId);
        return ResponseEntity.ok(prescriptions);
    }
    
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<Prescription>> getPrescriptionsByDoctorId(@PathVariable Long doctorId) {
        List<Prescription> prescriptions = prescriptionService.getPrescriptionsByDoctorId(doctorId);
        return ResponseEntity.ok(prescriptions);
    }
    
    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<List<Prescription>> getPrescriptionsByAppointmentId(@PathVariable Long appointmentId) {
        List<Prescription> prescriptions = prescriptionService.getPrescriptionsByAppointmentId(appointmentId);
        return ResponseEntity.ok(prescriptions);
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<Prescription>> getActivePrescriptions() {
        List<Prescription> prescriptions = prescriptionService.getActivePrescriptions();
        return ResponseEntity.ok(prescriptions);
    }
    
    @GetMapping("/patient/{patientId}/date-range")
    public ResponseEntity<List<Prescription>> getPatientPrescriptionsInDateRange(
            @PathVariable Long patientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Prescription> prescriptions = prescriptionService.getPatientPrescriptionsInDateRange(patientId, startDate, endDate);
        return ResponseEntity.ok(prescriptions);
    }
    
    @GetMapping("/doctor/{doctorId}/diagnosis")
    public ResponseEntity<List<Prescription>> getDoctorPrescriptionsByDiagnosis(
            @PathVariable Long doctorId,
            @RequestParam String diagnosis) {
        List<Prescription> prescriptions = prescriptionService.getDoctorPrescriptionsByDiagnosis(doctorId, diagnosis);
        return ResponseEntity.ok(prescriptions);
    }
    
    @GetMapping("/patient/{patientId}/latest")
    public ResponseEntity<List<Prescription>> getLatestPrescriptionsByPatientId(@PathVariable Long patientId) {
        List<Prescription> prescriptions = prescriptionService.getLatestPrescriptionsByPatientId(patientId);
        return ResponseEntity.ok(prescriptions);
    }
    
    @PostMapping
    public ResponseEntity<Prescription> createPrescription(@RequestBody Prescription prescription) {
        try {
            Prescription createdPrescription = prescriptionService.createPrescription(prescription);
            return ResponseEntity.ok(createdPrescription);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Prescription> updatePrescription(@PathVariable String id, @RequestBody Prescription prescriptionDetails) {
        try {
            Prescription updatedPrescription = prescriptionService.updatePrescription(id, prescriptionDetails);
            return ResponseEntity.ok(updatedPrescription);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrescription(@PathVariable String id) {
        try {
            prescriptionService.deletePrescription(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivatePrescription(@PathVariable String id) {
        try {
            prescriptionService.deactivatePrescription(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/patient/{patientId}/count")
    public ResponseEntity<Long> getPrescriptionCountByPatient(@PathVariable Long patientId) {
        Long count = prescriptionService.getPrescriptionCountByPatient(patientId);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/doctor/{doctorId}/count")
    public ResponseEntity<Long> getPrescriptionCountByDoctor(@PathVariable Long doctorId) {
        Long count = prescriptionService.getPrescriptionCountByDoctor(doctorId);
        return ResponseEntity.ok(count);
    }
}