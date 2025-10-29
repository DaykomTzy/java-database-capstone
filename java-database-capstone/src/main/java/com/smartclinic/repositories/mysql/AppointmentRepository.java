package com.smartclinic.repositories.mysql;

import com.smartclinic.models.mysql.Appointment;
import com.smartclinic.models.mysql.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    
    List<Appointment> findByPatientId(Long patientId);
    
    List<Appointment> findByDoctorId(Long doctorId);
    
    List<Appointment> findByStatus(AppointmentStatus status);
    
    List<Appointment> findByAppointmentDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId AND a.appointmentDate BETWEEN :startDate AND :endDate")
    List<Appointment> findDoctorAppointmentsInDateRange(
        @Param("doctorId") Long doctorId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    @Query("SELECT a FROM Appointment a WHERE a.patient.id = :patientId AND a.status = :status")
    List<Appointment> findPatientAppointmentsByStatus(
        @Param("patientId") Long patientId,
        @Param("status") AppointmentStatus status
    );
    
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.doctor.id = :doctorId AND a.status = 'SCHEDULED'")
    Long countScheduledAppointmentsByDoctor(@Param("doctorId") Long doctorId);
    
    @Modifying
    @Transactional
    @Query("UPDATE Appointment a SET a.status = :status WHERE a.id = :appointmentId")
    void updateAppointmentStatus(@Param("appointmentId") Long appointmentId, @Param("status") AppointmentStatus status);
    
    @Query(value = "CALL GetDoctorAppointmentStats(:doctorId)", nativeQuery = true)
    List<Object[]> getDoctorAppointmentStats(@Param("doctorId") Long doctorId);
}