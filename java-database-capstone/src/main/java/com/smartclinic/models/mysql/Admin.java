package com.smartclinic.models.mysql;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "admins")
@PrimaryKeyJoinColumn(name = "user_id")
public class Admin extends User {
    
    // Constructors
    public Admin() {
        super();
    }
    
    public Admin(String username, String email, String password, String firstName, String lastName) {
        super(username, email, password, firstName, lastName, Role.ADMIN);
    }
    
    // Admin-specific fields can be added here
    private String department;
    
    // Getters and Setters
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
}