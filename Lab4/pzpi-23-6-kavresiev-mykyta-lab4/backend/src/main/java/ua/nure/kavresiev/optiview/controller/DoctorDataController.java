package ua.nure.kavresiev.optiview.controller;

import org.springframework.web.bind.annotation.*;
import ua.nure.kavresiev.optiview.entity.*;
import ua.nure.kavresiev.optiview.repository.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/doctor-data")
@CrossOrigin(origins = "*")
public class DoctorDataController {

    private final VisitRepository visitRepository;
    private final PatientRepository patientRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final MeasurementRepository measurementRepository;
    private final PrescriptionRepository prescriptionRepository;

    public DoctorDataController(
            VisitRepository visitRepository,
            PatientRepository patientRepository,
            MedicalRecordRepository medicalRecordRepository,
            MeasurementRepository measurementRepository,
            PrescriptionRepository prescriptionRepository
    ) {
        this.visitRepository = visitRepository;
        this.patientRepository = patientRepository;
        this.medicalRecordRepository = medicalRecordRepository;
        this.measurementRepository = measurementRepository;
        this.prescriptionRepository = prescriptionRepository;
    }

    @GetMapping("/stats/{doctorUserId}")
    public Map<String, Object> getDoctorStats(@PathVariable Long doctorUserId) {
        Map<String, Object> stats = new HashMap<>();

        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(DayOfWeek.MONDAY);
        LocalDate weekEnd = weekStart.plusDays(6);

        int todayAppointments = 0;
        int weekPlanned = 0;
        int weekCompleted = 0;
        int weekIot = 0;
        int totalCompleted = 0;

        Set<Long> uniquePatients = new HashSet<>();

        List<Visit> visits = visitRepository.findByDoctorId(doctorUserId);

        for (Visit visit : visits) {
            if (visit.getPatientId() != null) {
                uniquePatients.add(visit.getPatientId());
            }

            if (visit.getStartTime() == null) {
                continue;
            }

            LocalDate visitDate = visit.getStartTime().toLocalDate();

            if (visitDate.equals(today)) {
                todayAppointments++;
            }

            boolean inCurrentWeek = !visitDate.isBefore(weekStart) && !visitDate.isAfter(weekEnd);

            if (inCurrentWeek && "PLANNED".equals(visit.getVisitStatus())) {
                weekPlanned++;
            }

            if (inCurrentWeek && "COMPLETED".equals(visit.getVisitStatus())) {
                weekCompleted++;
            }

            if ("COMPLETED".equals(visit.getVisitStatus())) {
                totalCompleted++;
            }

            if (inCurrentWeek && hasIotMeasurement(visit.getVisitId())) {
                weekIot++;
            }
        }

        stats.put("todayAppointments", todayAppointments);
        stats.put("weekPlanned", weekPlanned);
        stats.put("weekCompleted", weekCompleted);
        stats.put("weekIot", weekIot);
        stats.put("totalPatients", uniquePatients.size());
        stats.put("totalCompleted", totalCompleted);

        return stats;
    }

    @GetMapping("/medical-records/{doctorUserId}")
    public List<Map<String, Object>> getMedicalRecords(@PathVariable Long doctorUserId) {
        List<Map<String, Object>> result = new ArrayList<>();

        for (Visit visit : visitRepository.findByDoctorId(doctorUserId)) {
            if (!"COMPLETED".equals(visit.getVisitStatus())) {
                continue;
            }

            Optional<Patient> optionalPatient = patientRepository.findById(visit.getPatientId());

            if (optionalPatient.isEmpty()) {
                continue;
            }

            List<MedicalRecord> records = medicalRecordRepository.findByVisitId(visit.getVisitId());
            MedicalRecord record = records.isEmpty() ? null : records.get(0);

            Measurement measurement = null;

            if (record != null) {
                Optional<Measurement> optionalMeasurement =
                        measurementRepository.findTopByMedicalRecordIdOrderByMeasurementsIdDesc(record.getMedicalRecordId());

                if (optionalMeasurement.isPresent()) {
                    measurement = optionalMeasurement.get();
                }
            }

            Patient patient = optionalPatient.get();

            Map<String, Object> item = new HashMap<>();
            item.put("visitId", visit.getVisitId());
            item.put("patientId", patient.getPatientId());
            item.put("patientFirstName", patient.getFirstName());
            item.put("patientLastName", patient.getLastName());
            item.put("patientPhone", patient.getPhone());
            item.put("patientEmail", patient.getEmail());
            item.put("startTime", visit.getStartTime());
            item.put("endTime", visit.getEndTime());
            item.put("diagnosis", visit.getDiagnosis());
            item.put("treatment", visit.getTreatment());
            item.put("visualAcuity", visit.getVisualAcuity());
            item.put("complaints", record == null ? "Не вказано" : record.getComplaints());
            item.put("anamnesis", record == null ? "Не вказано" : record.getAnamnesis());
            item.put("hasIot", measurement != null);
            item.put("deviceSerial", measurement == null ? null : measurement.getDeviceSerial());

            result.add(item);
        }

        result.sort((a, b) -> String.valueOf(b.get("startTime")).compareTo(String.valueOf(a.get("startTime"))));

        return result;
    }

    @GetMapping("/prescriptions/{doctorUserId}")
    public List<Map<String, Object>> getDoctorPrescriptions(@PathVariable Long doctorUserId) {
        List<Map<String, Object>> result = new ArrayList<>();

        for (Visit visit : visitRepository.findByDoctorId(doctorUserId)) {
            if (!"COMPLETED".equals(visit.getVisitStatus())) {
                continue;
            }

            Optional<Patient> optionalPatient = patientRepository.findById(visit.getPatientId());

            if (optionalPatient.isEmpty()) {
                continue;
            }

            List<MedicalRecord> records = medicalRecordRepository.findByVisitId(visit.getVisitId());

            if (records.isEmpty()) {
                continue;
            }

            MedicalRecord record = records.get(0);
            List<Prescription> prescriptions = prescriptionRepository.findByMedicalRecordId(record.getMedicalRecordId());

            if (prescriptions.isEmpty()) {
                continue;
            }

            Patient patient = optionalPatient.get();
            Prescription prescription = prescriptions.get(0);

            Map<String, Object> item = new HashMap<>();
            item.put("visitId", visit.getVisitId());
            item.put("patientId", patient.getPatientId());
            item.put("patientFirstName", patient.getFirstName());
            item.put("patientLastName", patient.getLastName());
            item.put("patientPhone", patient.getPhone());
            item.put("patientEmail", patient.getEmail());
            item.put("startTime", visit.getStartTime());
            item.put("prescriptionId", prescription.getPrescriptionId());
            item.put("prescriptionType", prescription.getPrescriptionType());
            item.put("sphOd", prescription.getSphOd());
            item.put("cylOd", prescription.getCylOd());
            item.put("axisOd", prescription.getAxisOd());
            item.put("sphOs", prescription.getSphOs());
            item.put("cylOs", prescription.getCylOs());
            item.put("axisOs", prescription.getAxisOs());
            item.put("pd", prescription.getPd());
            item.put("createdAt", prescription.getCreatedAt());
            item.put("updatedAt", prescription.getUpdatedAt());

            result.add(item);
        }

        result.sort((a, b) -> String.valueOf(b.get("updatedAt")).compareTo(String.valueOf(a.get("updatedAt"))));

        return result;
    }

    private boolean hasIotMeasurement(Long visitId) {
        List<MedicalRecord> records = medicalRecordRepository.findByVisitId(visitId);

        if (records.isEmpty()) {
            return false;
        }

        List<Measurement> measurements = measurementRepository.findByMedicalRecordId(records.get(0).getMedicalRecordId());

        return !measurements.isEmpty();
    }
}