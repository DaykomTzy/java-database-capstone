// MongoDB Initialization Script for Smart Clinic

db = db.getSiblingDB('smart_clinic');

// Create collections
db.createCollection('prescriptions');
db.createCollection('medical_records');

// Create indexes for better performance
db.prescriptions.createIndex({ "patientId": 1 });
db.prescriptions.createIndex({ "doctorId": 1 });
db.prescriptions.createIndex({ "appointmentId": 1 });
db.prescriptions.createIndex({ "prescriptionDate": -1 });
db.prescriptions.createIndex({ "isActive": 1 });

db.medical_records.createIndex({ "patientId": 1 });
db.medical_records.createIndex({ "doctorId": 1 });
db.medical_records.createIndex({ "visitDate": -1 });
db.medical_records.createIndex({ "diagnosis": "text" });
db.medical_records.createIndex({ "followUpRequired": 1 });

// Insert sample prescriptions
db.prescriptions.insertMany([
    {
        "appointmentId": 1,
        "patientId": 1,
        "doctorId": 1,
        "patientName": "Mehmet Demir",
        "doctorName": "Dr. Ahmet Yılmaz",
        "diagnosis": "Hipertansiyon",
        "medications": [
            {
                "name": "Lisinopril",
                "dosage": "10 mg",
                "frequency": "Günde 1 kez",
                "duration": "30 gün",
                "instructions": "Sabah aç karnına alınacak"
            },
            {
                "name": "Aspirin",
                "dosage": "100 mg",
                "frequency": "Günde 1 kez",
                "duration": "Sürekli",
                "instructions": "Yemeklerden sonra"
            }
        ],
        "instructions": "Tuz tüketimini azaltın. Düzenli egzersiz yapın. 1 ay sonra kontrole gelin.",
        "prescriptionDate": new Date("2024-01-15"),
        "isActive": true,
        "createdAt": new Date()
    },
    {
        "appointmentId": 2,
        "patientId": 2,
        "doctorId": 2,
        "patientName": "Zeynep Şahin",
        "doctorName": "Dr. Ayşe Kaya",
        "diagnosis": "Tip 2 Diyabet",
        "medications": [
            {
                "name": "Metformin",
                "dosage": "500 mg",
                "frequency": "Günde 2 kez",
                "duration": "90 gün",
                "instructions": "Yemeklerle birlikte"
            }
        ],
        "instructions": "Diyet programına uyun. Haftada 3 kez kan şekeri ölçümü yapın. 3 ay sonra kontrole gelin.",
        "prescriptionDate": new Date("2024-01-10"),
        "isActive": true,
        "createdAt": new Date()
    }
]);

// Insert sample medical records
db.medical_records.insertMany([
    {
        "patientId": 1,
        "visitDate": new Date("2024-01-15"),
        "doctorId": 1,
        "doctorName": "Dr. Ahmet Yılmaz",
        "diagnosis": "Hipertansiyon",
        "symptoms": ["Baş ağrısı", "Baş dönmesi", "Yorgunluk"],
        "treatment": "Antihipertansif ilaç tedavisi başlandı",
        "notes": "Hasta 150/95 mmHg kan basıncı ile başvurdu. Tuz kısıtlaması önerildi.",
        "vitalSigns": {
            "bloodPressureSystolic": 150,
            "bloodPressureDiastolic": 95,
            "heartRate": 78,
            "temperature": 36.8,
            "respiratoryRate": 16,
            "weight": 85,
            "height": 178
        },
        "labResults": [
            {
                "testName": "Kan Şekeri",
                "result": "98",
                "unit": "mg/dL",
                "normalRange": "70-100",
                "notes": "Normal"
            },
            {
                "testName": "Kolesterol",
                "result": "210",
                "unit": "mg/dL",
                "normalRange": "<200",
                "notes": "Hafif yüksek"
            }
        ],
        "followUpRequired": true,
        "followUpDate": new Date("2024-02-15"),
        "createdAt": new Date()
    },
    {
        "patientId": 2,
        "visitDate": new Date("2024-01-10"),
        "doctorId": 2,
        "doctorName": "Dr. Ayşe Kaya",
        "diagnosis": "Tip 2 Diyabet",
        "symptoms": ["Çok su içme", "Sık idrara çıkma", "Yorgunluk"],
        "treatment": "Oral antidiyabetik tedavi başlandı",
        "notes": "Hasta açlık kan şekeri 145 mg/dL ile başvurdu. Diyet ve egzersiz önerildi.",
        "vitalSigns": {
            "bloodPressureSystolic": 130,
            "bloodPressureDiastolic": 85,
            "heartRate": 72,
            "temperature": 36.6,
            "respiratoryRate": 14,
            "weight": 68,
            "height": 165
        },
        "labResults": [
            {
                "testName": "Açlık Kan Şekeri",
                "result": "145",
                "unit": "mg/dL",
                "normalRange": "70-100",
                "notes": "Yüksek"
            },
            {
                "testName": "HbA1c",
                "result": "7.2",
                "unit": "%",
                "normalRange": "<6.5",
                "notes": "Yüksek"
            }
        ],
        "followUpRequired": true,
        "followUpDate": new Date("2024-04-10"),
        "createdAt": new Date()
    }
]);

print("MongoDB initialization completed successfully!");