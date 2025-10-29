package com.smartclinic.services;

import java.util.Optional;
import com.smartclinic.models.mysql.Appointment;
import com.smartclinic.models.mysql.AppointmentStatus;
import com.smartclinic.repositories.mysql.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentService {
    
    @Autowired
    private AppointmentRepository appointmentRepository;
    
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }
    
    public Optional<Appointment> getAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }
    
    public List<Appointment> getAppointmentsByPatientId(Long patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }
    
    public List<Appointment> getAppointmentsByDoctorId(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId);
    }
    
    public List<Appointment> getAppointmentsByStatus(AppointmentStatus status) {
        return appointmentRepository.findByStatus(status);
    }
    
    public List<Appointment> getAppointmentsInDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return appointmentRepository.findByAppointmentDateBetween(startDate, endDate);
    }
    
    public List<Appointment> getDoctorAppointmentsInDateRange(Long doctorId, LocalDateTime startDate, LocalDateTime endDate) {
        return appointmentRepository.findDoctorAppointmentsInDateRange(doctorId, startDate, endDate);
    }
    
    public List<Appointment> getPatientAppointmentsByStatus(Long patientId, AppointmentStatus status) {
        return appointmentRepository.findPatientAppointmentsByStatus(patientId, status);
    }
    
    public Appointment createAppointment(Appointment appointment) {
        // Basic validation - check if appointment time is in the future
        if (appointment.getAppointmentDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Appointment date cannot be in the past");
        }
        
        // Check for scheduling conflicts (simplified)
        List<Appointment> existingAppointments = appointmentRepository
            .findDoctorAppointmentsInDateRange(
                appointment.getDoctor().getId(),
                appointment.getAppointmentDate().minusHours(1),
                appointment.getAppointmentDate().plusHours(1)
            );
        
        if (!existingAppointments.isEmpty()) {
            throw new RuntimeException("Doctor has conflicting appointment at this time");
        }
        
        return appointmentRepository.save(appointment);
    }
    
    public Appointment updateAppointment(Long id, Appointment appointmentDetails) {
        Appointment appointment = appointmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));
        
        appointment.setAppointmentDate(appointmentDetails.getAppointmentDate());
        appointment.setNotes(appointmentDetails.getNotes());
        appointment.setStatus(appointmentDetails.getStatus());
        
        return appointmentRepository.save(appointment);
    }
    
    public void updateAppointmentStatus(Long appointmentId, AppointmentStatus status) {
        appointmentRepository.updateAppointmentStatus(appointmentId, status);
    }
    
    public void deleteAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));
        appointmentRepository.delete(appointment);
    }
    
    public Long getScheduledAppointmentsCountByDoctor(Long doctorId) {
        return appointmentRepository.countScheduledAppointmentsByDoctor(doctorId);
    }
    
    public List<Object[]> getDoctorAppointmentStats(Long doctorId) {
        return appointmentRepository.getDoctorAppointmentStats(doctorId);
    }
}