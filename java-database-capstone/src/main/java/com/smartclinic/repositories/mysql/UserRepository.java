package com.smartclinic.repositories.mysql;

import com.smartclinic.models.mysql.User;
import com.smartclinic.models.mysql.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    List<User> findByRole(Role role);
    
    Boolean existsByUsername(String username);
    
    Boolean existsByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE u.firstName LIKE %:name% OR u.lastName LIKE %:name%")
    List<User> findByNameContaining(@Param("name") String name);
    
    @Query("SELECT u FROM User u WHERE u.role = 'DOCTOR' AND u.id IN " +
           "(SELECT d.user.id FROM Doctor d WHERE d.specialization = :specialization)")
    List<User> findDoctorsBySpecialization(@Param("specialization") String specialization);
}