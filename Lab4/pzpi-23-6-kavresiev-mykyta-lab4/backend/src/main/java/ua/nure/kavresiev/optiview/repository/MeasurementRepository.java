package ua.nure.kavresiev.optiview.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.nure.kavresiev.optiview.entity.Measurement;

import java.util.List;
import java.util.Optional;

@Repository
public interface MeasurementRepository extends JpaRepository<Measurement, Long> {

    List<Measurement> findByMedicalRecordId(Long medicalRecordId);

    Optional<Measurement> findTopByMedicalRecordIdOrderByMeasurementsIdDesc(Long medicalRecordId);
}