package ua.nure.kavresiev.optiview.repository;

import ua.nure.kavresiev.optiview.entity.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {
    Optional<Diagnosis> findByDiagnosisCode(String code);
}