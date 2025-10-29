# Smart Clinic Management System - Mimari Tasarım Dokümanı

## Bölüm 1: Sistem Mimarisi Genel Bakış

### 1.1 Mimari Stil
- **Microservice Mimari** - Her bir business capability için bağımsız servisler
- **API Gateway Pattern** - Tüm isteklerin tek noktadan yönetimi
- **Repository Pattern** - Data access katmanı soyutlaması

### 1.2 Teknoloji Stack'i
- **Backend**: Java Spring Boot
- **Frontend**: HTML5, CSS3, JavaScript
- **Database**: MySQL (Relational), MongoDB (NoSQL)
- **Authentication**: JWT (JSON Web Token)
- **Containerization**: Docker
- **CI/CD**: GitHub Actions

### 1.3 Servis Mimarisi Diagramı
┌─────────────────┐ ┌──────────────────┐ ┌─────────────────┐
│ Frontend │ │ API Gateway │ │ MySQL │
│ (HTML/CSS/JS) │───▶│ (Spring Cloud) │───▶│ (Users, │
│ │ │ │ │ Appointments) │
└─────────────────┘ └──────────────────┘ └─────────────────┘
│
│ ┌──────────────┐ ┌───────────────┐
├─▶│ Auth Service│ │ MongoDB │
│ │ (JWT) │ │ (Prescriptions│
│ └──────────────┘ │ Medical History)
│ └───────────────┘
│ ┌─────────────────┐
├─│ User Service │
│ └─────────────────┘
│ ┌─────────────────┐
├─│ Appointment │
│ │ Service │
│ └─────────────────┘
│ ┌─────────────────┐
└─│ Medical Record │
│ Service │
└─────────────────┘


## Bölüm 2: Database Şema Tasarımı

### 2.1 MySQL - Relational Database Şeması

#### Users Tablosu
```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'DOCTOR', 'PATIENT') NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    date_of_birth DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);