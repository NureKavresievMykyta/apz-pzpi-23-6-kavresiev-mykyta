package ua.nure.kavresiev.optiview.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.nure.kavresiev.optiview.entity.*;
import ua.nure.kavresiev.optiview.repository.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final VisitRepository visitRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final MeasurementRepository measurementRepository;
    private final PrescriptionRepository prescriptionRepository;

    public AdminController(
            UserRepository userRepository,
            DoctorRepository doctorRepository,
            PatientRepository patientRepository,
            VisitRepository visitRepository,
            MedicalRecordRepository medicalRecordRepository,
            MeasurementRepository measurementRepository,
            PrescriptionRepository prescriptionRepository
    ) {
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.visitRepository = visitRepository;
        this.medicalRecordRepository = medicalRecordRepository;
        this.measurementRepository = measurementRepository;
        this.prescriptionRepository = prescriptionRepository;
    }

    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();

        long patientsCount = patientRepository.count();
        long doctorsCount = doctorRepository.count();
        long pendingDoctorsCount = doctorRepository.findAll()
                .stream()
                .filter(doctor -> {
                    Optional<User> user = userRepository.findById(doctor.getDoctorUserId());
                    return user.isPresent()
                            && "DOCTOR".equals(user.get().getRole())
                            && Boolean.FALSE.equals(user.get().getIsActive());
                })
                .count();

        long plannedVisits = visitRepository.findAll()
                .stream()
                .filter(visit -> "PLANNED".equals(visit.getVisitStatus()))
                .count();

        long completedVisits = visitRepository.findAll()
                .stream()
                .filter(visit -> "COMPLETED".equals(visit.getVisitStatus()))
                .count();

        long measurementsCount = measurementRepository.count();
        long prescriptionsCount = prescriptionRepository.count();

        stats.put("patientsCount", patientsCount);
        stats.put("doctorsCount", doctorsCount);
        stats.put("pendingDoctorsCount", pendingDoctorsCount);
        stats.put("plannedVisits", plannedVisits);
        stats.put("completedVisits", completedVisits);
        stats.put("measurementsCount", measurementsCount);
        stats.put("prescriptionsCount", prescriptionsCount);

        return stats;
    }

    @GetMapping("/doctors/pending")
    public List<Map<String, Object>> getPendingDoctors() {
        List<Map<String, Object>> result = new ArrayList<>();

        for (Doctor doctor : doctorRepository.findAll()) {
            Optional<User> optionalUser = userRepository.findById(doctor.getDoctorUserId());

            if (optionalUser.isEmpty()) {
                continue;
            }

            User user = optionalUser.get();

            if (!"DOCTOR".equals(user.getRole()) || Boolean.TRUE.equals(user.getIsActive())) {
                continue;
            }

            result.add(doctorToMap(doctor, user));
        }

        return result;
    }

    @PutMapping("/doctors/{doctorUserId}/approve")
    public ResponseEntity<?> approveDoctor(@PathVariable Long doctorUserId) {
        Optional<User> optionalUser = userRepository.findById(doctorUserId);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body("Користувача не знайдено");
        }

        User user = optionalUser.get();
        user.setIsActive(true);
        userRepository.save(user);

        return ResponseEntity.ok("Лікаря підтверджено");
    }

    @PutMapping("/doctors/{doctorUserId}/reject")
    public ResponseEntity<?> rejectDoctor(@PathVariable Long doctorUserId) {
        Optional<User> optionalUser = userRepository.findById(doctorUserId);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body("Користувача не знайдено");
        }

        Optional<Doctor> optionalDoctor = doctorRepository.findById(doctorUserId);

        optionalDoctor.ifPresent(doctorRepository::delete);
        userRepository.delete(optionalUser.get());

        return ResponseEntity.ok("Заявку лікаря відхилено");
    }

    @GetMapping("/doctors")
    public List<Map<String, Object>> getAllDoctors() {
        List<Map<String, Object>> result = new ArrayList<>();

        for (Doctor doctor : doctorRepository.findAll()) {
            Optional<User> optionalUser = userRepository.findById(doctor.getDoctorUserId());

            if (optionalUser.isEmpty()) {
                continue;
            }

            result.add(doctorToMap(doctor, optionalUser.get()));
        }

        return result;
    }

    @PutMapping("/doctors/{doctorUserId}/toggle-active")
    public ResponseEntity<?> toggleDoctorActive(@PathVariable Long doctorUserId) {
        Optional<User> optionalUser = userRepository.findById(doctorUserId);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body("Користувача не знайдено");
        }

        User user = optionalUser.get();

        if (!"DOCTOR".equals(user.getRole())) {
            return ResponseEntity.badRequest().body("Це не акаунт лікаря");
        }

        user.setIsActive(!Boolean.TRUE.equals(user.getIsActive()));
        userRepository.save(user);

        return ResponseEntity.ok("Статус лікаря змінено");
    }

    @GetMapping("/patients")
    public List<Map<String, Object>> getAllPatients() {
        List<Map<String, Object>> result = new ArrayList<>();

        for (Patient patient : patientRepository.findAll()) {
            Map<String, Object> item = new HashMap<>();

            long visitsCount = visitRepository.findByPatientId(patient.getPatientId()).size();
            long completedVisitsCount = visitRepository.findByPatientId(patient.getPatientId())
                    .stream()
                    .filter(visit -> "COMPLETED".equals(visit.getVisitStatus()))
                    .count();

            item.put("patientId", patient.getPatientId());
            item.put("firstName", patient.getFirstName());
            item.put("lastName", patient.getLastName());
            item.put("birthDate", patient.getBirthDate());
            item.put("phone", patient.getPhone());
            item.put("email", patient.getEmail());
            item.put("address", patient.getAddress());
            item.put("visitsCount", visitsCount);
            item.put("completedVisitsCount", completedVisitsCount);

            result.add(item);
        }

        return result;
    }

    @GetMapping("/visits")
    public List<Map<String, Object>> getAllVisits() {
        List<Map<String, Object>> result = new ArrayList<>();

        for (Visit visit : visitRepository.findAll()) {
            Map<String, Object> item = new HashMap<>();

            Optional<Patient> optionalPatient = patientRepository.findById(visit.getPatientId());
            Optional<Doctor> optionalDoctor = doctorRepository.findById(visit.getDoctorId());

            item.put("visitId", visit.getVisitId());
            item.put("patientId", visit.getPatientId());
            item.put("doctorId", visit.getDoctorId());
            item.put("patientName", optionalPatient
                    .map(patient -> patient.getFirstName() + " " + patient.getLastName())
                    .orElse("Не вказано"));
            item.put("doctorName", optionalDoctor
                    .map(doctor -> doctor.getFirstName() + " " + doctor.getLastName())
                    .orElse("Не вказано"));
            item.put("startTime", visit.getStartTime());
            item.put("endTime", visit.getEndTime());
            item.put("visitStatus", visit.getVisitStatus());
            item.put("visitType", visit.getVisitType());
            item.put("diagnosis", visit.getDiagnosis());

            result.add(item);
        }

        result.sort((a, b) -> String.valueOf(b.get("startTime")).compareTo(String.valueOf(a.get("startTime"))));

        return result;
    }

    @GetMapping("/measurements")
    public List<Map<String, Object>> getAllMeasurements() {
        List<Map<String, Object>> result = new ArrayList<>();

        for (Measurement measurement : measurementRepository.findAll()) {
            Map<String, Object> item = new HashMap<>();

            item.put("measurementsId", measurement.getMeasurementsId());
            item.put("medicalRecordId", measurement.getMedicalRecordId());
            item.put("sphOd", measurement.getSphOd());
            item.put("cylOd", measurement.getCylOd());
            item.put("axisOd", measurement.getAxisOd());
            item.put("sphOs", measurement.getSphOs());
            item.put("cylOs", measurement.getCylOs());
            item.put("axisOs", measurement.getAxisOs());
            item.put("iopOd", measurement.getIopOd());
            item.put("iopOs", measurement.getIopOs());
            item.put("pd", measurement.getPd());
            item.put("deviceSerial", measurement.getDeviceSerial());

            Optional<MedicalRecord> optionalRecord = medicalRecordRepository.findById(measurement.getMedicalRecordId());

            if (optionalRecord.isPresent()) {
                MedicalRecord record = optionalRecord.get();
                Optional<Visit> optionalVisit = visitRepository.findById(record.getVisitId());

                if (optionalVisit.isPresent()) {
                    Visit visit = optionalVisit.get();

                    item.put("visitId", visit.getVisitId());
                    item.put("startTime", visit.getStartTime());

                    Optional<Patient> optionalPatient = patientRepository.findById(visit.getPatientId());
                    Optional<Doctor> optionalDoctor = doctorRepository.findById(visit.getDoctorId());

                    item.put("patientName", optionalPatient
                            .map(patient -> patient.getFirstName() + " " + patient.getLastName())
                            .orElse("Не вказано"));
                    item.put("doctorName", optionalDoctor
                            .map(doctor -> doctor.getFirstName() + " " + doctor.getLastName())
                            .orElse("Не вказано"));
                }
            }

            result.add(item);
        }

        return result;
    }

    private Map<String, Object> doctorToMap(Doctor doctor, User user) {
        Map<String, Object> map = new HashMap<>();

        map.put("userId", user.getUserId());
        map.put("username", user.getUsername());
        map.put("role", user.getRole());
        map.put("isActive", user.getIsActive());
        map.put("createdAt", user.getCreatedAt());
        map.put("doctorUserId", doctor.getDoctorUserId());
        map.put("firstName", doctor.getFirstName());
        map.put("lastName", doctor.getLastName());
        map.put("specialization", doctor.getSpecialization());
        map.put("cabinetNumber", doctor.getCabinetNumber());

        return map;
    }
}