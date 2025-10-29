package com.smartclinic.repositories.mysql;

import com.smartclinic.models.mysql.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    
    Optional<Doctor> findByLicenseNumber(String licenseNumber);
    
    List<Doctor> findBySpecialization(String specialization);
    
    List<Doctor> findByYearsOfExperienceGreaterThanEqual(Integer years);
    
    @Query("SELECT d FROM Doctor d WHERE d.specialization = :specialization AND d.yearsOfExperience >= :minExperience")
    List<Doctor> findBySpecializationAndMinExperience(
        @Param("specialization") String specialization, 
        @Param("minExperience") Integer minExperience
    );
    
    @Query("SELECT DISTINCT d.specialization FROM Doctor d")
    List<String> findAllSpecializations();
    
    @Query("SELECT COUNT(d) FROM Doctor d WHERE d.specialization = :specialization")
    Long countBySpecialization(@Param("specialization") String specialization);
}