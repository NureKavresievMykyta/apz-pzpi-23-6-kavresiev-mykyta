package ua.nure.kavresiev.optiview.repository;

import ua.nure.kavresiev.optiview.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {
    List<Visit> findByPatientId(Long patientId);

    List<Visit> findByDoctorId(Long doctorId);
}