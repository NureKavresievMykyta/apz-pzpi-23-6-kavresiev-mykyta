package ua.nure.kavresiev.optiview.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "prescriptions")
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prescription_id")
    private Long prescriptionId;

    @Column(name = "medical_record_id")
    private Long medicalRecordId;

    @Column(name = "prescription_type")
    private String prescriptionType;

    @Column(name = "sph_od")
    private BigDecimal sphOd;

    @Column(name = "cyl_od")
    private BigDecimal cylOd;

    @Column(name = "axis_od")
    private Integer axisOd;

    @Column(name = "sph_os")
    private BigDecimal sphOs;

    @Column(name = "cyl_os")
    private BigDecimal cylOs;

    @Column(name = "axis_os")
    private Integer axisOs;

    @Column(name = "pd")
    private Integer pd;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}