package ua.nure.kavresiev.optiview.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "diagnoses")
public class Diagnosis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diagnosis_id")
    private Long diagnosisId;

    @Column(name = "diagnosis_code", unique = true, nullable = false)
    private String diagnosisCode; // Наприклад "H52.1"

    @Column(name = "diagnosis_name", nullable = false)
    private String diagnosisName;

    @Column(name = "diagnosis_description")
    private String diagnosisDescription;
}