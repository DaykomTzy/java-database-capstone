package com.smartclinic.repositories.mysql;

import com.smartclinic.models.mysql.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    
    Optional<Patient> findByEmail(String email);
    
    List<Patient> findByLastNameContainingIgnoreCase(String lastName);
    
    @Query("SELECT p FROM Patient p WHERE p.firstName LIKE %:name% OR p.lastName LIKE %:name%")
    List<Patient> findByNameContaining(@Param("name") String name);
    
    @Query("SELECT COUNT(p) FROM Patient p")
    Long getTotalPatientCount();
    
    @Query("SELECT p FROM Patient p WHERE p.emergencyContact IS NOT NULL")
    List<Patient> findPatientsWithEmergencyContact();
}