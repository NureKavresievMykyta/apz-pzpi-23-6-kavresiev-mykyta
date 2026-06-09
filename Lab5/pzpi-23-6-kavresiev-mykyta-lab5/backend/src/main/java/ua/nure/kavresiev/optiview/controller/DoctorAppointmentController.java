package ua.nure.kavresiev.optiview.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.nure.kavresiev.optiview.dto.DoctorVisitFinishRequest;
import ua.nure.kavresiev.optiview.dto.DoctorVisitResponse;
import ua.nure.kavresiev.optiview.entity.*;
import ua.nure.kavresiev.optiview.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/doctor-appointments")
@CrossOrigin(origins = "*")
public class DoctorAppointmentController {

    private final VisitRepository visitRepository;
    private final PatientRepository patientRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final MeasurementRepository measurementRepository;
    private final PrescriptionRepository prescriptionRepository;

    public DoctorAppointmentController(
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

    @GetMapping("/{doctorUserId}")
    public List<DoctorVisitResponse> getDoctorAppointments(@PathVariable Long doctorUserId) {
        return buildDoctorVisits(doctorUserId, null, null, "PLANNED");
    }

    @GetMapping("/calendar/{doctorUserId}")
    public List<DoctorVisitResponse> getDoctorCalendar(
            @PathVariable Long doctorUserId,
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam(required = false, defaultValue = "ALL") String status
    ) {
        LocalDate fromDate = LocalDate.parse(from);
        LocalDate toDate = LocalDate.parse(to);

        LocalDateTime fromDateTime = fromDate.atStartOfDay();
        LocalDateTime toDateTime = toDate.plusDays(1).atStartOfDay();

        return buildDoctorVisits(doctorUserId, fromDateTime, toDateTime, status);
    }

    @GetMapping("/visit/{visitId}")
    public ResponseEntity<?> getVisitDetails(@PathVariable Long visitId) {
        Optional<Visit> optionalVisit = visitRepository.findById(visitId);

        if (optionalVisit.isEmpty()) {
            return ResponseEntity.badRequest().body("Прийом не знайдено");
        }

        Visit visit = optionalVisit.get();

        Optional<Patient> optionalPatient = patientRepository.findById(visit.getPatientId());

        if (optionalPatient.isEmpty()) {
            return ResponseEntity.badRequest().body("Пацієнта не знайдено");
        }

        return ResponseEntity.ok(toResponse(visit, optionalPatient.get()));
    }

    @GetMapping("/visit/{visitId}/has-iot")
    public Map<String, Object> hasIotResult(@PathVariable Long visitId) {
        Map<String, Object> result = new HashMap<>();
        List<MedicalRecord> records = medicalRecordRepository.findByVisitId(visitId);

        if (records.isEmpty()) {
            result.put("hasIot", false);
            return result;
        }

        MedicalRecord record = records.get(0);
        List<Measurement> measurements = measurementRepository.findByMedicalRecordId(record.getMedicalRecordId());

        result.put("hasIot", !measurements.isEmpty());
        return result;
    }

    @GetMapping("/completed-visit/{visitId}")
    public ResponseEntity<?> getCompletedVisitDetails(@PathVariable Long visitId) {
        Optional<Visit> optionalVisit = visitRepository.findById(visitId);

        if (optionalVisit.isEmpty()) {
            return ResponseEntity.badRequest().body("Прийом не знайдено");
        }

        Visit visit = optionalVisit.get();

        Optional<Patient> optionalPatient = patientRepository.findById(visit.getPatientId());

        if (optionalPatient.isEmpty()) {
            return ResponseEntity.badRequest().body("Пацієнта не знайдено");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("visit", visit);
        result.put("patient", optionalPatient.get());

        List<MedicalRecord> records = medicalRecordRepository.findByVisitId(visitId);

        if (records.isEmpty()) {
            result.put("medicalRecord", null);
            result.put("measurement", null);
            result.put("prescription", null);
            return ResponseEntity.ok(result);
        }

        MedicalRecord record = records.get(0);
        result.put("medicalRecord", record);

        Optional<Measurement> measurement =
                measurementRepository.findTopByMedicalRecordIdOrderByMeasurementsIdDesc(record.getMedicalRecordId());

        result.put("measurement", measurement.orElse(null));

        List<Prescription> prescriptions = prescriptionRepository.findByMedicalRecordId(record.getMedicalRecordId());

        if (prescriptions.isEmpty()) {
            result.put("prescription", null);
        } else {
            result.put("prescription", prescriptions.get(0));
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/patients/search")
    public List<Map<String, Object>> searchPatients(@RequestParam String query) {
        String normalizedQuery = query == null ? "" : query.trim().toLowerCase();

        if (normalizedQuery.isEmpty()) {
            return new ArrayList<>();
        }

        List<Map<String, Object>> result = new ArrayList<>();

        for (Patient patient : patientRepository.findAll()) {
            String fullName = safe(patient.getFirstName()) + " " + safe(patient.getLastName());
            String data = (
                    fullName + " " +
                            safe(patient.getPhone()) + " " +
                            safe(patient.getEmail()) + " " +
                            safe(patient.getAddress())
            ).toLowerCase();

            if (!data.contains(normalizedQuery)) {
                continue;
            }

            result.add(patientToMap(patient));
        }

        return result;
    }

    @GetMapping("/patients/{patientId}/card")
    public ResponseEntity<?> getPatientCard(@PathVariable Long patientId) {
        Optional<Patient> optionalPatient = patientRepository.findById(patientId);

        if (optionalPatient.isEmpty()) {
            return ResponseEntity.badRequest().body("Пацієнта не знайдено");
        }

        Patient patient = optionalPatient.get();

        Map<String, Object> result = new HashMap<>();
        result.put("patient", patientToMap(patient));

        List<Map<String, Object>> completedVisits = new ArrayList<>();

        for (Visit visit : visitRepository.findByPatientId(patientId)) {
            if (!"COMPLETED".equals(visit.getVisitStatus())) {
                continue;
            }

            completedVisits.add(visitToMap(visit));
        }

        result.put("completedVisits", completedVisits);

        List<Prescription> prescriptions = prescriptionRepository.findByPatientId(patientId);

        if (prescriptions.isEmpty()) {
            result.put("actualPrescription", null);
        } else {
            result.put("actualPrescription", prescriptions.get(0));
        }

        Measurement lastMeasurement = null;

        for (Map<String, Object> visitMap : completedVisits) {
            Long visitId = (Long) visitMap.get("visitId");
            List<MedicalRecord> records = medicalRecordRepository.findByVisitId(visitId);

            if (records.isEmpty()) {
                continue;
            }

            Optional<Measurement> measurement =
                    measurementRepository.findTopByMedicalRecordIdOrderByMeasurementsIdDesc(records.get(0).getMedicalRecordId());

            if (measurement.isPresent()) {
                lastMeasurement = measurement.get();
                break;
            }
        }

        result.put("lastMeasurement", lastMeasurement);

        return ResponseEntity.ok(result);
    }

    @PutMapping("/{visitId}/finish")
    public ResponseEntity<?> finishAppointment(
            @PathVariable Long visitId,
            @RequestBody DoctorVisitFinishRequest request
    ) {
        Optional<Visit> optionalVisit = visitRepository.findById(visitId);

        if (optionalVisit.isEmpty()) {
            return ResponseEntity.badRequest().body("Запис не знайдено");
        }

        Visit visit = optionalVisit.get();

        visit.setDiagnosis(valueOrDefault(request.getDiagnosis()));
        visit.setTreatment(valueOrDefault(request.getTreatment()));
        visit.setVisualAcuity(valueOrDefault(request.getVisualAcuity()));
        visit.setVisitStatus("COMPLETED");

        Visit savedVisit = visitRepository.save(visit);

        MedicalRecord record = getOrCreateMedicalRecord(savedVisit.getVisitId());

        record.setComplaints(valueOrDefault(request.getComplaints()));
        record.setAnamnesis(valueOrDefault(request.getAnamnesis()));

        MedicalRecord savedRecord = medicalRecordRepository.save(record);

        createOrUpdatePrescription(savedVisit, savedRecord);

        return ResponseEntity.ok(savedVisit);
    }

    private List<DoctorVisitResponse> buildDoctorVisits(
            Long doctorUserId,
            LocalDateTime from,
            LocalDateTime to,
            String status
    ) {
        List<Visit> visits = visitRepository.findAll();
        List<DoctorVisitResponse> result = new ArrayList<>();

        for (Visit visit : visits) {
            if (visit.getDoctorId() == null || !visit.getDoctorId().equals(doctorUserId)) {
                continue;
            }

            if (!"ALL".equals(status) && !status.equals(visit.getVisitStatus())) {
                continue;
            }

            if (from != null && visit.getStartTime().isBefore(from)) {
                continue;
            }

            if (to != null && !visit.getStartTime().isBefore(to)) {
                continue;
            }

            Optional<Patient> optionalPatient = patientRepository.findById(visit.getPatientId());

            if (optionalPatient.isEmpty()) {
                continue;
            }

            result.add(toResponse(visit, optionalPatient.get()));
        }

        return result;
    }

    private DoctorVisitResponse toResponse(Visit visit, Patient patient) {
        return new DoctorVisitResponse(
                visit.getVisitId(),
                patient.getPatientId(),
                patient.getFirstName(),
                patient.getLastName(),
                patient.getBirthDate() == null ? null : patient.getBirthDate().toString(),
                patient.getPhone(),
                patient.getEmail(),
                patient.getAddress(),
                visit.getStartTime() == null ? null : visit.getStartTime().toString(),
                visit.getEndTime() == null ? null : visit.getEndTime().toString(),
                visit.getVisitStatus(),
                visit.getVisitType()
        );
    }

    private Map<String, Object> patientToMap(Patient patient) {
        Map<String, Object> map = new HashMap<>();
        map.put("patientId", patient.getPatientId());
        map.put("firstName", patient.getFirstName());
        map.put("lastName", patient.getLastName());
        map.put("birthDate", patient.getBirthDate());
        map.put("phone", patient.getPhone());
        map.put("email", patient.getEmail());
        map.put("address", patient.getAddress());
        return map;
    }

    private Map<String, Object> visitToMap(Visit visit) {
        Map<String, Object> map = new HashMap<>();
        map.put("visitId", visit.getVisitId());
        map.put("patientId", visit.getPatientId());
        map.put("doctorId", visit.getDoctorId());
        map.put("startTime", visit.getStartTime());
        map.put("endTime", visit.getEndTime());
        map.put("visitStatus", visit.getVisitStatus());
        map.put("visitType", visit.getVisitType());
        map.put("diagnosis", visit.getDiagnosis());
        map.put("treatment", visit.getTreatment());
        map.put("visualAcuity", visit.getVisualAcuity());
        return map;
    }

    private void createOrUpdatePrescription(Visit visit, MedicalRecord medicalRecord) {
        Optional<Measurement> optionalMeasurement =
                measurementRepository.findTopByMedicalRecordIdOrderByMeasurementsIdDesc(medicalRecord.getMedicalRecordId());

        if (optionalMeasurement.isEmpty()) {
            return;
        }

        Measurement measurement = optionalMeasurement.get();

        List<Prescription> prescriptions = prescriptionRepository.findByMedicalRecordId(medicalRecord.getMedicalRecordId());

        Prescription prescription;

        if (prescriptions.isEmpty()) {
            prescription = new Prescription();
            prescription.setMedicalRecordId(medicalRecord.getMedicalRecordId());
        } else {
            prescription = prescriptions.get(0);
        }

        prescription.setPrescriptionType("Актуальний рецепт після IoT-обстеження");
        prescription.setSphOd(measurement.getSphOd());
        prescription.setCylOd(measurement.getCylOd());
        prescription.setAxisOd(measurement.getAxisOd());
        prescription.setSphOs(measurement.getSphOs());
        prescription.setCylOs(measurement.getCylOs());
        prescription.setAxisOs(measurement.getAxisOs());
        prescription.setPd(measurement.getPd());

        prescriptionRepository.save(prescription);
    }

    private MedicalRecord getOrCreateMedicalRecord(Long visitId) {
        List<MedicalRecord> records = medicalRecordRepository.findByVisitId(visitId);

        if (!records.isEmpty()) {
            return records.get(0);
        }

        MedicalRecord record = new MedicalRecord();
        record.setVisitId(visitId);
        record.setComplaints("Не вказано");
        record.setAnamnesis("Не вказано");

        return medicalRecordRepository.save(record);
    }

    private String valueOrDefault(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "Не вказано";
        }

        return value.trim();
    }

    private String safe(String value) {
        if (value == null) {
            return "";
        }

        return value;
    }
}