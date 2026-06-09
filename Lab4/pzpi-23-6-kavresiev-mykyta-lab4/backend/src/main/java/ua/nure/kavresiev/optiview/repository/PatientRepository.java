package ua.nure.kavresiev.optiview.repository;

import ua.nure.kavresiev.optiview.entity.Patient;
import ua.nure.kavresiev.optiview.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByUser(User user);
}