package ua.nure.kavresiev.optiview.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import ua.nure.kavresiev.optiview.entity.Prescription;

import java.util.List;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    List<Prescription> findByMedicalRecordId(Long medicalRecordId);

    @Query("""
            SELECT p FROM Prescription p
            JOIN MedicalRecord mr ON p.medicalRecordId = mr.medicalRecordId
            JOIN Visit v ON mr.visitId = v.visitId
            WHERE v.patientId = :patientId
            ORDER BY p.updatedAt DESC, p.prescriptionId DESC
            """)
    List<Prescription> findByPatientId(@Param("patientId") Long patientId);
}