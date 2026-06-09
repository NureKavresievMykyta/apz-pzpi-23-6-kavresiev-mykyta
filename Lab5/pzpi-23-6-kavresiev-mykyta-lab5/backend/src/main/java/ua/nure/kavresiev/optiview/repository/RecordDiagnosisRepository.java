package ua.nure.kavresiev.optiview.repository;

import ua.nure.kavresiev.optiview.entity.RecordDiagnosis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RecordDiagnosisRepository extends JpaRepository<RecordDiagnosis, Long> {
    List<RecordDiagnosis> findByMedicalRecordId(Long medicalRecordId);
}