-- Smart Clinic Management System Database Initialization

-- Create database if not exists (already created by environment variable)
USE smart_clinic;

-- Create tables
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    role ENUM('ADMIN', 'DOCTOR', 'PATIENT') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS patients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    date_of_birth DATE,
    blood_type ENUM('A_POSITIVE', 'A_NEGATIVE', 'B_POSITIVE', 'B_NEGATIVE', 
                   'AB_POSITIVE', 'AB_NEGATIVE', 'O_POSITIVE', 'O_NEGATIVE'),
    emergency_contact VARCHAR(100),
    insurance_info TEXT,
    address TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS doctors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    license_number VARCHAR(50) UNIQUE NOT NULL,
    years_of_experience INT,
    qualification VARCHAR(100),
    consultation_fee DECIMAL(10,2),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS admins (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    department VARCHAR(100),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS appointments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    appointment_date TIMESTAMP NOT NULL,
    status ENUM('SCHEDULED', 'CONFIRMED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED', 'NO_SHOW') DEFAULT 'SCHEDULED',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id),
    FOREIGN KEY (doctor_id) REFERENCES doctors(id)
);

-- Insert sample data
INSERT IGNORE INTO users (username, email, password, first_name, last_name, phone, role) VALUES
('admin', 'admin@smartclinic.com', 'admin123', 'System', 'Administrator', '+90-555-123-4567', 'ADMIN'),
('dr_ahmet', 'ahmet.yilmaz@smartclinic.com', 'doctor123', 'Ahmet', 'Yılmaz', '+90-555-123-4568', 'DOCTOR'),
('dr_ayse', 'ayse.kaya@smartclinic.com', 'doctor123', 'Ayşe', 'Kaya', '+90-555-123-4569', 'DOCTOR'),
('mehmet_demir', 'mehmet.demir@example.com', 'patient123', 'Mehmet', 'Demir', '+90-555-123-4570', 'PATIENT'),
('zeynep_sahin', 'zeynep.sahin@example.com', 'patient123', 'Zeynep', 'Şahin', '+90-555-123-4571', 'PATIENT');

INSERT IGNORE INTO patients (user_id, date_of_birth, blood_type, emergency_contact, insurance_info, address) VALUES
(4, '1985-05-15', 'A_POSITIVE', '+90-555-123-9999', 'SGK - Prim Borcu Yok', 'İstanbul, Beşiktaş'),
(5, '1990-08-22', 'O_POSITIVE', '+90-555-123-8888', 'Özel Sigorta - Anadolu Sigorta', 'Ankara, Çankaya');

INSERT IGNORE INTO doctors (user_id, specialization, license_number, years_of_experience, qualification, consultation_fee) VALUES
(2, 'Kardiyoloji', 'DR-TRK-2023001', 12, 'Profesör Doktor', 500.00),
(3, 'Dahiliye', 'DR-TRK-2023002', 8, 'Uzman Doktor', 300.00);

INSERT IGNORE INTO admins (user_id, department) VALUES
(1, 'IT');

INSERT IGNORE INTO appointments (patient_id, doctor_id, appointment_date, status, notes) VALUES
(1, 1, '2024-01-20 10:00:00', 'SCHEDULED', 'Kontrol muayenesi'),
(2, 2, '2024-01-20 14:30:00', 'CONFIRMED', 'Şeker takibi'),
(1, 2, '2024-01-22 11:00:00', 'SCHEDULED', 'Genel kontrol');

-- Create stored procedures
DELIMITER //

CREATE PROCEDURE IF NOT EXISTS GetDoctorAppointmentStats(IN doctor_id_param BIGINT)
BEGIN
    SELECT 
        status,
        COUNT(*) as count
    FROM appointments 
    WHERE doctor_id = doctor_id_param 
    GROUP BY status;
END //

CREATE PROCEDURE IF NOT EXISTS GetPatientAppointments(IN patient_id_param BIGINT)
BEGIN
    SELECT 
        a.*,
        d.first_name as doctor_first_name,
        d.last_name as doctor_last_name,
        d.specialization
    FROM appointments a
    JOIN doctors d ON a.doctor_id = d.id
    WHERE a.patient_id = patient_id_param
    ORDER BY a.appointment_date DESC;
END //

CREATE PROCEDURE IF NOT EXISTS GetUpcomingAppointments(IN days INT)
BEGIN
    SELECT 
        a.*,
        p.first_name as patient_first_name,
        p.last_name as patient_last_name,
        d.first_name as doctor_first_name,
        d.last_name as doctor_last_name,
        d.specialization
    FROM appointments a
    JOIN patients p ON a.patient_id = p.id
    JOIN doctors d ON a.doctor_id = d.id
    WHERE a.appointment_date BETWEEN NOW() AND DATE_ADD(NOW(), INTERVAL days DAY)
    AND a.status IN ('SCHEDULED', 'CONFIRMED')
    ORDER BY a.appointment_date ASC;
END //

DELIMITER ;

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_appointments_doctor_date ON appointments(doctor_id, appointment_date);
CREATE INDEX IF NOT EXISTS idx_appointments_patient_date ON appointments(patient_id, appointment_date);
CREATE INDEX IF NOT EXISTS idx_appointments_status ON appointments(status);
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);
CREATE INDEX IF NOT EXISTS idx_doctors_specialization ON doctors(specialization);

-- Create views for reporting
CREATE OR REPLACE VIEW doctor_appointment_summary AS
SELECT 
    d.id as doctor_id,
    d.first_name,
    d.last_name,
    d.specialization,
    COUNT(a.id) as total_appointments,
    SUM(CASE WHEN a.status = 'COMPLETED' THEN 1 ELSE 0 END) as completed_appointments,
    SUM(CASE WHEN a.status = 'SCHEDULED' THEN 1 ELSE 0 END) as scheduled_appointments
FROM doctors d
LEFT JOIN appointments a ON d.id = a.doctor_id
GROUP BY d.id, d.first_name, d.last_name, d.specialization;

CREATE OR REPLACE VIEW patient_medical_history AS
SELECT 
    p.id as patient_id,
    p.first_name,
    p.last_name,
    COUNT(a.id) as total_appointments,
    MAX(a.appointment_date) as last_visit,
    MIN(a.appointment_date) as first_visit
FROM patients p
LEFT JOIN appointments a ON p.id = a.patient_id
GROUP BY p.id, p.first_name, p.last_name;