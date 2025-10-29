package com.smartclinic.repositories.mongo;

import com.smartclinic.models.mongo.MedicalRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MedicalRecordRepository extends MongoRepository<MedicalRecord, String> {
    
    List<MedicalRecord> findByPatientId(Long patientId);
    
    List<MedicalRecord> findByDoctorId(Long doctorId);
    
    List<MedicalRecord> findByDiagnosisContainingIgnoreCase(String diagnosis);
    
    List<MedicalRecord> findByFollowUpRequired(Boolean followUpRequired);
    
    @Query("{ 'patientId': ?0, 'visitDate': { $gte: ?1, $lte: ?2 } }")
    List<MedicalRecord> findPatientRecordsInDateRange(
        Long patientId, 
        LocalDateTime startDate, 
        LocalDateTime endDate
    );
    
    @Query("{ 'patientId': ?0, 'diagnosis': { $regex: ?1, $options: 'i' } }")
    List<MedicalRecord> findByPatientIdAndDiagnosisContaining(Long patientId, String diagnosis);
    
    @Query(value = "{ 'patientId': ?0 }", sort = "{ 'visitDate': -1 }")
    List<MedicalRecord> findLatestRecordsByPatientId(Long patientId);
    
    @Query("{ 'labResults.testName': { $regex: ?0, $options: 'i' } }")
    List<MedicalRecord> findByLabTestNameContaining(String testName);
    
    Long countByPatientId(Long patientId);
    
    Long countByFollowUpRequired(Boolean followUpRequired);
}