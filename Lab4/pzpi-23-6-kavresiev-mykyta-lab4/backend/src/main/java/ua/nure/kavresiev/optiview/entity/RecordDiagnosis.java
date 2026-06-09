package ua.nure.kavresiev.optiview.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "record_diagnoses")
public class RecordDiagnosis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_diagnosis_id")
    private Long recordDiagnosisId;

    @Column(name = "medical_record_id")
    private Long medicalRecordId;

    @Column(name = "diagnosis_id")
    private Long diagnosisId;

    @Column(name = "diagnosis_type")
    private String diagnosisType; // 'MAIN' (Основний) або 'CONCOMITANT' (Супутній)
}