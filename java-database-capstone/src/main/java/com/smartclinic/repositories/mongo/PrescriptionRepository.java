package com.smartclinic.repositories.mongo;

import com.smartclinic.models.mongo.Prescription;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PrescriptionRepository extends MongoRepository<Prescription, String> {
    
    List<Prescription> findByPatientId(Long patientId);
    
    List<Prescription> findByDoctorId(Long doctorId);
    
    List<Prescription> findByAppointmentId(Long appointmentId);
    
    List<Prescription> findByIsActive(Boolean isActive);
    
    @Query("{ 'patientId': ?0, 'prescriptionDate': { $gte: ?1, $lte: ?2 } }")
    List<Prescription> findPatientPrescriptionsInDateRange(
        Long patientId, 
        LocalDateTime startDate, 
        LocalDateTime endDate
    );
    
    @Query("{ 'doctorId': ?0, 'diagnosis': { $regex: ?1, $options: 'i' } }")
    List<Prescription> findByDoctorIdAndDiagnosisContaining(Long doctorId, String diagnosis);
    
    @Query(value = "{ 'patientId': ?0 }", sort = "{ 'prescriptionDate': -1 }")
    List<Prescription> findLatestPrescriptionsByPatientId(Long patientId);
    
    Long countByPatientId(Long patientId);
    
    Long countByDoctorId(Long doctorId);
}