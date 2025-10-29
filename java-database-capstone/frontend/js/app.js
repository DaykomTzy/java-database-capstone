// API Base URL - Docker container içinde çalışırken bu URL'yi kullan
const API_BASE_URL = 'http://localhost:8080/api';

// Global state
let currentUser = null;
let currentRole = null;

// DOM Content Loaded
document.addEventListener('DOMContentLoaded', function() {
    initializeApp();
    setupEventListeners();
});

// Uygulama başlatma
function initializeApp() {
    console.log('Smart Clinic Management System initialized');
    
    // Sayfa tipine göre farklı başlatma işlemleri
    const currentPage = window.location.pathname.split('/').pop();
    
    switch(currentPage) {
        case 'login.html':
            initializeLoginPage();
            break;
        case 'admin-dashboard.html':
            initializeAdminDashboard();
            break;
        case 'doctor-dashboard.html':
            initializeDoctorDashboard();
            break;
        case 'patient-dashboard.html':
            initializePatientDashboard();
            break;
        default:
            // Ana sayfa için özel işlemler
            break;
    }
}

// Event listener'ları kur
function setupEventListeners() {
    // Navigation için
    setupNavigation();
    
    // Modal kapatma event'leri
    setupModalEvents();
}

// Navigation yönetimi
function setupNavigation() {
    const navLinks = document.querySelectorAll('.nav-link[href^="#"]');
    
    navLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            const targetId = this.getAttribute('href').substring(1);
            showSection(targetId);
            
            // Active state güncelle
            navLinks.forEach(nl => nl.parentElement.classList.remove('active'));
            this.parentElement.classList.add('active');
        });
    });
}

// Modal event'leri
function setupModalEvents() {
    // Modal kapatma
    const modals = document.querySelectorAll('.modal');
    const closeButtons = document.querySelectorAll('.close');
    
    closeButtons.forEach(btn => {
        btn.addEventListener('click', function() {
            this.closest('.modal').style.display = 'none';
        });
    });
    
    // Modal dışına tıklayarak kapatma
    modals.forEach(modal => {
        modal.addEventListener('click', function(e) {
            if (e.target === this) {
                this.style.display = 'none';
            }
        });
    });
}

// Section gösterme
function showSection(sectionId) {
    const sections = document.querySelectorAll('.content-section');
    sections.forEach(section => {
        section.classList.remove('active');
    });
    
    const targetSection = document.getElementById(sectionId);
    if (targetSection) {
        targetSection.classList.add('active');
    }
}

// ==================== LOGIN PAGE FUNCTIONS ====================

function initializeLoginPage() {
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
    }
}

async function handleLogin(e) {
    e.preventDefault();
    
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    const role = document.getElementById('role').value;
    
    const messageDiv = document.getElementById('loginMessage');
    
    try {
        // Basit login simülasyonu - Gerçek uygulamada JWT token kullanılacak
        const users = await fetchUsers();
        const user = users.find(u => 
            u.username === username && 
            u.password === password && 
            u.role.toLowerCase() === role
        );
        
        if (user) {
            currentUser = user;
            currentRole = role;
            
            messageDiv.textContent = 'Giriş başarılı! Yönlendiriliyorsunuz...';
            messageDiv.className = 'message success';
            messageDiv.style.display = 'block';
            
            // Rol bazlı yönlendirme
            setTimeout(() => {
                switch(role) {
                    case 'admin':
                        window.location.href = 'admin-dashboard.html';
                        break;
                    case 'doctor':
                        window.location.href = 'doctor-dashboard.html';
                        break;
                    case 'patient':
                        window.location.href = 'patient-dashboard.html';
                        break;
                }
            }, 1500);
            
        } else {
            throw new Error('Geçersiz kullanıcı adı, şifre veya rol!');
        }
        
    } catch (error) {
        messageDiv.textContent = error.message;
        messageDiv.className = 'message error';
        messageDiv.style.display = 'block';
    }
}

// ==================== ADMIN DASHBOARD FUNCTIONS ====================

function initializeAdminDashboard() {
    loadAdminStats();
    loadUsersTable();
    loadDoctorsTable();
    loadPatientsTable();
    loadAppointmentsTable();
}

async function loadAdminStats() {
    try {
        // API'den verileri çek
        const [users, doctors, patients, appointments] = await Promise.all([
            fetchUsers(),
            fetchDoctors(),
            fetchPatients(),
            fetchAppointments()
        ]);
        
        // İstatistikleri güncelle
        document.getElementById('totalUsers').textContent = users.length;
        document.getElementById('totalDoctors').textContent = doctors.length;
        document.getElementById('totalPatients').textContent = patients.length;
        document.getElementById('totalAppointments').textContent = appointments.length;
        
    } catch (error) {
        console.error('Stats loading error:', error);
    }
}

// Kullanıcı yönetimi
async function loadUsersTable() {
    try {
        const users = await fetchUsers();
        const tbody = document.getElementById('usersTableBody');
        
        tbody.innerHTML = users.map(user => `
            <tr>
                <td>${user.id}</td>
                <td>${user.username}</td>
                <td>${user.email}</td>
                <td>${user.role}</td>
                <td class="action-buttons">
                    <button class="btn btn-sm btn-info" onclick="editUser(${user.id})">Düzenle</button>
                    <button class="btn btn-sm btn-danger" onclick="deleteUser(${user.id})">Sil</button>
                </td>
            </tr>
        `).join('');
        
    } catch (error) {
        console.error('Users table loading error:', error);
    }
}

// Modal fonksiyonları
function showAddUserForm() {
    document.getElementById('addUserModal').style.display = 'block';
}

function closeAddUserModal() {
    document.getElementById('addUserModal').style.display = 'none';
    document.getElementById('addUserForm').reset();
}

// Yeni kullanıcı ekleme
document.getElementById('addUserForm')?.addEventListener('submit', async function(e) {
    e.preventDefault();
    
    const userData = {
        username: document.getElementById('newUsername').value,
        email: document.getElementById('newEmail').value,
        password: document.getElementById('newPassword').value,
        role: document.getElementById('newRole').value,
        firstName: 'New',
        lastName: 'User'
    };
    
    try {
        await createUser(userData);
        closeAddUserModal();
        loadUsersTable();
        loadAdminStats();
        showMessage('Kullanıcı başarıyla eklendi!', 'success');
    } catch (error) {
        showMessage('Kullanıcı eklenirken hata: ' + error.message, 'error');
    }
});

// ==================== DOCTOR DASHBOARD FUNCTIONS ====================

function initializeDoctorDashboard() {
    loadDoctorStats();
    loadDoctorAppointments();
    loadDoctorPatients();
    loadDoctorPrescriptions();
    loadDoctorMedicalRecords();
    
    // Modal form event'leri
    setupDoctorModals();
}

async function loadDoctorStats() {
    try {
        // Doktor ID'sini al (simülasyon)
        const doctorId = 1; // Gerçek uygulamada currentUser'dan alınacak
        
        const [appointments, patients, prescriptions] = await Promise.all([
            fetchAppointments(),
            fetchPatients(),
            fetchPrescriptions()
        ]);
        
        const today = new Date().toISOString().split('T')[0];
        const todayApps = appointments.filter(apt => 
            apt.doctorId === doctorId && 
            apt.appointmentDate.startsWith(today)
        );
        
        const doctorPatients = patients.filter(p => 
            appointments.some(apt => apt.patientId === p.id && apt.doctorId === doctorId)
        );
        
        const doctorPrescriptions = prescriptions.filter(p => p.doctorId === doctorId);
        const waitingApps = appointments.filter(apt => 
            apt.doctorId === doctorId && 
            apt.status === 'SCHEDULED'
        );
        
        document.getElementById('todayAppointments').textContent = todayApps.length;
        document.getElementById('totalPatients').textContent = doctorPatients.length;
        document.getElementById('totalPrescriptions').textContent = doctorPrescriptions.length;
        document.getElementById('waitingAppointments').textContent = waitingApps.length;
        
    } catch (error) {
        console.error('Doctor stats loading error:', error);
    }
}

async function loadDoctorAppointments() {
    try {
        const appointments = await fetchAppointments();
        const doctorId = 1; // Simülasyon
        
        const doctorAppointments = appointments.filter(apt => apt.doctorId === doctorId);
        const tbody = document.getElementById('appointmentsTableBody');
        
        tbody.innerHTML = doctorAppointments.map(apt => `
            <tr>
                <td>${apt.id}</td>
                <td>${apt.patientName || 'Hasta'}</td>
                <td>${formatDate(apt.appointmentDate)}</td>
                <td>${formatTime(apt.appointmentDate)}</td>
                <td><span class="status-badge status-${apt.status.toLowerCase()}">${apt.status}</span></td>
                <td>${apt.notes || '-'}</td>
                <td class="action-buttons">
                    <button class="btn btn-sm btn-success" onclick="updateAppointmentStatus(${apt.id}, 'CONFIRMED')">Onayla</button>
                    <button class="btn btn-sm btn-warning" onclick="updateAppointmentStatus(${apt.id}, 'CANCELLED')">İptal</button>
                </td>
            </tr>
        `).join('');
        
    } catch (error) {
        console.error('Doctor appointments loading error:', error);
    }
}

// Reçete yönetimi
function showAddPrescriptionForm() {
    loadPatientsForPrescription();
    document.getElementById('addPrescriptionModal').style.display = 'block';
}

function closeAddPrescriptionModal() {
    document.getElementById('addPrescriptionModal').style.display = 'none';
    document.getElementById('addPrescriptionForm').reset();
    document.getElementById('medicationsList').innerHTML = '';
}

function addMedicationField() {
    const medicationsList = document.getElementById('medicationsList');
    const medicationCount = medicationsList.children.length + 1;
    
    const medicationField = document.createElement('div');
    medicationField.className = 'medication-field';
    medicationField.innerHTML = `
        <input type="text" placeholder="İlaç adı" required>
        <input type="text" placeholder="Dozaj" required>
        <input type="text" placeholder="Sıklık" required>
        <input type="text" placeholder="Süre" required>
        <button type="button" class="btn btn-sm btn-danger" onclick="this.parentElement.remove()">Sil</button>
    `;
    
    medicationsList.appendChild(medicationField);
}

// ==================== PATIENT DASHBOARD FUNCTIONS ====================

function initializePatientDashboard() {
    loadPatientStats();
    loadPatientAppointments();
    loadPatientPrescriptions();
    loadPatientMedicalRecords();
    loadPatientProfile();
    
    // Modal event'leri
    setupPatientModals();
}

async function loadPatientStats() {
    try {
        const patientId = 1; // Simülasyon
        
        const [appointments, prescriptions, medicalRecords] = await Promise.all([
            fetchAppointments(),
            fetchPrescriptions(),
            fetchMedicalRecords()
        ]);
        
        const patientApps = appointments.filter(apt => apt.patientId === patientId);
        const patientPrescriptions = prescriptions.filter(p => p.patientId === patientId);
        const patientRecords = medicalRecords.filter(mr => mr.patientId === patientId);
        
        const upcomingApps = patientApps.filter(apt => 
            new Date(apt.appointmentDate) > new Date() && 
            apt.status === 'SCHEDULED'
        );
        
        const activePrescriptions = patientPrescriptions.filter(p => p.isActive);
        const uniqueDoctors = [...new Set(patientApps.map(apt => apt.doctorId))];
        
        document.getElementById('upcomingAppointments').textContent = upcomingApps.length;
        document.getElementById('activePrescriptions').textContent = activePrescriptions.length;
        document.getElementById('totalRecords').textContent = patientRecords.length;
        document.getElementById('doctorsVisited').textContent = uniqueDoctors.length;
        
    } catch (error) {
        console.error('Patient stats loading error:', error);
    }
}

// Randevu alma
function showBookAppointmentForm() {
    loadDoctorsForAppointment();
    document.getElementById('bookAppointmentModal').style.display = 'block';
    
    // Minimum tarihi bugün olarak ayarla
    const today = new Date().toISOString().split('T')[0];
    document.getElementById('appointmentDate').min = today;
}

function closeBookAppointmentModal() {
    document.getElementById('bookAppointmentModal').style.display = 'none';
    document.getElementById('bookAppointmentForm').reset();
}

// ==================== API FUNCTIONS ====================

// Temel API fonksiyonları
async function apiCall(endpoint, options = {}) {
    try {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, {
            headers: {
                'Content-Type': 'application/json',
                ...options.headers
            },
            ...options
        });
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        return await response.json();
    } catch (error) {
        console.error('API call error:', error);
        throw error;
    }
}

// CRUD operasyonları
async function fetchUsers() {
    return await apiCall('/users');
}

async function fetchPatients() {
    return await apiCall('/patients');
}

async function fetchDoctors() {
    return await apiCall('/doctors');
}

async function fetchAppointments() {
    return await apiCall('/appointments');
}

async function fetchPrescriptions() {
    return await apiCall('/prescriptions');
}

async function fetchMedicalRecords() {
    // Bu endpoint henüz yok, simülasyon için
    return [];
}

async function createUser(userData) {
    return await apiCall('/users', {
        method: 'POST',
        body: JSON.stringify(userData)
    });
}

async function updateAppointmentStatus(appointmentId, status) {
    return await apiCall(`/appointments/${appointmentId}/status?status=${status}`, {
        method: 'PATCH'
    });
}

// ==================== UTILITY FUNCTIONS ====================

function formatDate(dateString) {
    return new Date(dateString).toLocaleDateString('tr-TR');
}

function formatTime(dateString) {
    return new Date(dateString).toLocaleTimeString('tr-TR', { 
        hour: '2-digit', 
        minute: '2-digit' 
    });
}

function showMessage(message, type = 'info') {
    // Basit mesaj gösterme - gerçek uygulamada daha gelişmiş bir sistem kullanılabilir
    alert(`${type.toUpperCase()}: ${message}`);
}

// Simülasyon verileri (API hazır olana kadar)
async function fetchUsers() {
    // API hazır olana kadar simülasyon veri
    return [
        { id: 1, username: 'admin', password: 'admin123', email: 'admin@clinic.com', role: 'ADMIN', firstName: 'System', lastName: 'Admin' },
        { id: 2, username: 'dr_ahmet', password: 'doctor123', email: 'ahmet@clinic.com', role: 'DOCTOR', firstName: 'Ahmet', lastName: 'Yılmaz', specialization: 'Kardiyoloji' },
        { id: 3, username: 'hasta_mehmet', password: 'patient123', email: 'mehmet@example.com', role: 'PATIENT', firstName: 'Mehmet', lastName: 'Demir' }
    ];
}

async function fetchDoctors() {
    return [
        { id: 1, firstName: 'Ahmet', lastName: 'Yılmaz', specialization: 'Kardiyoloji', licenseNumber: 'DR12345' },
        { id: 2, firstName: 'Ayşe', lastName: 'Kaya', specialization: 'Dahiliye', licenseNumber: 'DR12346' }
    ];
}

async function fetchPatients() {
    return [
        { id: 1, firstName: 'Mehmet', lastName: 'Demir', email: 'mehmet@example.com', phone: '555-123-4567' },
        { id: 2, firstName: 'Zeynep', lastName: 'Şahin', email: 'zeynep@example.com', phone: '555-123-4568' }
    ];
}

async function fetchAppointments() {
    return [
        { id: 1, patientId: 1, doctorId: 1, appointmentDate: '2024-01-15T10:00:00', status: 'SCHEDULED', patientName: 'Mehmet Demir' },
        { id: 2, patientId: 2, doctorId: 2, appointmentDate: '2024-01-15T14:00:00', status: 'CONFIRMED', patientName: 'Zeynep Şahin' }
    ];
}

async function fetchPrescriptions() {
    return [
        { id: '1', patientId: 1, doctorId: 1, diagnosis: 'Hipertansiyon', prescriptionDate: '2024-01-10T00:00:00', isActive: true }
    ];
}

// Modal setup fonksiyonları
function setupDoctorModals() {
    document.getElementById('addPrescriptionForm')?.addEventListener('submit', async function(e) {
        e.preventDefault();
        // Reçete kaydetme işlemi
        showMessage('Reçete başarıyla oluşturuldu!', 'success');
        closeAddPrescriptionModal();
    });
}

function setupPatientModals() {
    document.getElementById('bookAppointmentForm')?.addEventListener('submit', async function(e) {
        e.preventDefault();
        // Randevu alma işlemi
        showMessage('Randevunuz başarıyla alındı!', 'success');
        closeBookAppointmentModal();
    });
    
    document.getElementById('profileForm')?.addEventListener('submit', async function(e) {
        e.preventDefault();
        // Profil güncelleme işlemi
        showMessage('Profil başarıyla güncellendi!', 'success');
        cancelProfileEdit();
    });
}

// Yardımcı fonksiyonlar
function loadPatientsForPrescription() {
    // Hasta listesini yükle
    const select = document.getElementById('prescriptionPatient');
    select.innerHTML = '<option value="">Hasta seçin</option>';
    // Simülasyon - gerçek uygulamada API'den çekilecek
}

function loadDoctorsForAppointment() {
    // Doktor listesini yükle
    const select = document.getElementById('appointmentDoctor');
    select.innerHTML = '<option value="">Doktor seçin</option>';
    // Simülasyon - gerçek uygulamada API'den çekilecek
}

function enableProfileEdit() {
    const inputs = document.querySelectorAll('#profileForm input, #profileForm select, #profileForm textarea');
    inputs.forEach(input => input.disabled = false);
    document.getElementById('profileActions').style.display = 'block';
}

function cancelProfileEdit() {
    const inputs = document.querySelectorAll('#profileForm input, #profileForm select, #profileForm textarea');
    inputs.forEach(input => input.disabled = true);
    document.getElementById('profileActions').style.display = 'none';
}

// Takip tarihi göster/gizle
document.getElementById('recordFollowUp')?.addEventListener('change', function() {
    document.getElementById('followUpDateGroup').style.display = this.checked ? 'block' : 'none';
});

console.log('app.js loaded successfully');