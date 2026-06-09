package ua.nure.kavresiev.optiview.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.nure.kavresiev.optiview.entity.MedicalRecord;
import ua.nure.kavresiev.optiview.entity.Prescription;
import ua.nure.kavresiev.optiview.repository.MedicalRecordRepository;
import ua.nure.kavresiev.optiview.repository.PrescriptionRepository;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/patient-prescriptions")
@CrossOrigin(origins = "*")
public class PatientPrescriptionController {

    private final PrescriptionRepository prescriptionRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    public PatientPrescriptionController(
            PrescriptionRepository prescriptionRepository,
            MedicalRecordRepository medicalRecordRepository
    ) {
        this.prescriptionRepository = prescriptionRepository;
        this.medicalRecordRepository = medicalRecordRepository;
    }

    @GetMapping("/{patientId}")
    public List<Prescription> getPatientPrescriptions(@PathVariable Long patientId) {
        List<Prescription> prescriptions = prescriptionRepository.findByPatientId(patientId);

        if (prescriptions.isEmpty()) {
            return prescriptions;
        }

        List<Prescription> result = new ArrayList<>();
        result.add(prescriptions.get(0));

        return result;
    }

    @GetMapping("/by-visit/{visitId}")
    public ResponseEntity<?> getPrescriptionByVisit(@PathVariable Long visitId) {
        List<MedicalRecord> records = medicalRecordRepository.findByVisitId(visitId);

        if (records.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        MedicalRecord record = records.get(0);
        List<Prescription> prescriptions = prescriptionRepository.findByMedicalRecordId(record.getMedicalRecordId());

        if (prescriptions.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(prescriptions.get(0));
    }
}