package ua.nure.kavresiev.optiview.repository;

import ua.nure.kavresiev.optiview.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    List<MedicalRecord> findByVisitId(Long visitId);
}