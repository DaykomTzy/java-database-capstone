package com.smartclinic.models.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "medical_records")
public class MedicalRecord {
    
    @Id
    private String id;
    
    @Field("patient_id")
    private Long patientId;
    
    @Field("visit_date")
    private LocalDateTime visitDate;
    
    @Field("doctor_id")
    private Long doctorId;
    
    @Field("doctor_name")
    private String doctorName;
    
    private String diagnosis;
    
    private List<String> symptoms = new ArrayList<>();
    
    private String treatment;
    
    private String notes;
    
    @Field("vital_signs")
    private VitalSigns vitalSigns;
    
    @Field("lab_results")
    private List<LabResult> labResults = new ArrayList<>();
    
    @Field("follow_up_required")
    private Boolean followUpRequired = false;
    
    @Field("follow_up_date")
    private LocalDateTime followUpDate;
    
    @Field("created_at")
    private LocalDateTime createdAt;
    
    // Constructors
    public MedicalRecord() {
        this.createdAt = LocalDateTime.now();
        this.visitDate = LocalDateTime.now();
    }
    
    public MedicalRecord(Long patientId, Long doctorId, String diagnosis) {
        this();
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.diagnosis = diagnosis;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }
    
    public LocalDateTime getVisitDate() { return visitDate; }
    public void setVisitDate(LocalDateTime visitDate) { this.visitDate = visitDate; }
    
    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }
    
    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    
    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
    
    public List<String> getSymptoms() { return symptoms; }
    public void setSymptoms(List<String> symptoms) { this.symptoms = symptoms; }
    
    public String getTreatment() { return treatment; }
    public void setTreatment(String treatment) { this.treatment = treatment; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public VitalSigns getVitalSigns() { return vitalSigns; }
    public void setVitalSigns(VitalSigns vitalSigns) { this.vitalSigns = vitalSigns; }
    
    public List<LabResult> getLabResults() { return labResults; }
    public void setLabResults(List<LabResult> labResults) { this.labResults = labResults; }
    
    public Boolean getFollowUpRequired() { return followUpRequired; }
    public void setFollowUpRequired(Boolean followUpRequired) { this.followUpRequired = followUpRequired; }
    
    public LocalDateTime getFollowUpDate() { return followUpDate; }
    public void setFollowUpDate(LocalDateTime followUpDate) { this.followUpDate = followUpDate; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

class VitalSigns {
    private Double bloodPressureSystolic;
    private Double bloodPressureDiastolic;
    private Double heartRate;
    private Double temperature;
    private Double respiratoryRate;
    private Double weight;
    private Double height;
    
    // Getters and Setters
    public Double getBloodPressureSystolic() { return bloodPressureSystolic; }
    public void setBloodPressureSystolic(Double bloodPressureSystolic) { this.bloodPressureSystolic = bloodPressureSystolic; }
    
    public Double getBloodPressureDiastolic() { return bloodPressureDiastolic; }
    public void setBloodPressureDiastolic(Double bloodPressureDiastolic) { this.bloodPressureDiastolic = bloodPressureDiastolic; }
    
    public Double getHeartRate() { return heartRate; }
    public void setHeartRate(Double heartRate) { this.heartRate = heartRate; }
    
    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }
    
    public Double getRespiratoryRate() { return respiratoryRate; }
    public void setRespiratoryRate(Double respiratoryRate) { this.respiratoryRate = respiratoryRate; }
    
    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }
    
    public Double getHeight() { return height; }
    public void setHeight(Double height) { this.height = height; }
}

class LabResult {
    private String testName;
    private String result;
    private String unit;
    private String normalRange;
    private String notes;
    
    // Getters and Setters
    public String getTestName() { return testName; }
    public void setTestName(String testName) { this.testName = testName; }
    
    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
    
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    
    public String getNormalRange() { return normalRange; }
    public void setNormalRange(String normalRange) { this.normalRange = normalRange; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}