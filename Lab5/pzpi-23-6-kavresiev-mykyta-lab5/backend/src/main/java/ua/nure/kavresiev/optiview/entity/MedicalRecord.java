package ua.nure.kavresiev.optiview.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "medical_records")
public class MedicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "medical_record_id")
    private Long medicalRecordId;

    @Column(name = "visit_id")
    private Long visitId; // Прив'язка до візиту

    @Column(name = "complaints")
    private String complaints; // Скарги

    @Column(name = "anamnesis")
    private String anamnesis; // Анамнез (історія хвороби)

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}