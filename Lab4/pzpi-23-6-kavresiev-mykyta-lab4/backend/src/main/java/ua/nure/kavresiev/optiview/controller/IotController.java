package ua.nure.kavresiev.optiview.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ua.nure.kavresiev.optiview.dto.IotScanRequest;
import ua.nure.kavresiev.optiview.dto.IotScanResponse;
import ua.nure.kavresiev.optiview.entity.Measurement;
import ua.nure.kavresiev.optiview.entity.MedicalRecord;
import ua.nure.kavresiev.optiview.entity.Patient;
import ua.nure.kavresiev.optiview.entity.Visit;
import ua.nure.kavresiev.optiview.repository.MeasurementRepository;
import ua.nure.kavresiev.optiview.repository.MedicalRecordRepository;
import ua.nure.kavresiev.optiview.repository.PatientRepository;
import ua.nure.kavresiev.optiview.repository.VisitRepository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/iot")
@CrossOrigin(origins = "*")
public class IotController {

    private final VisitRepository visitRepository;
    private final PatientRepository patientRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final MeasurementRepository measurementRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${IOT_DEVICE_URL:http://localhost:8090}")
    private String iotDeviceUrl;

    public IotController(
            VisitRepository visitRepository,
            PatientRepository patientRepository,
            MedicalRecordRepository medicalRecordRepository,
            MeasurementRepository measurementRepository
    ) {
        this.visitRepository = visitRepository;
        this.patientRepository = patientRepository;
        this.medicalRecordRepository = medicalRecordRepository;
        this.measurementRepository = measurementRepository;
    }

    @GetMapping("/status")
    public ResponseEntity<?> getIotStatus() {
        try {
            Object response = restTemplate.getForObject(
                    iotDeviceUrl + "/api/device/status",
                    Object.class
            );

            return ResponseEntity.ok(response);
        } catch (Exception exception) {
            Map<String, Object> result = new HashMap<>();
            result.put("status", "OFFLINE");
            result.put("deviceName", "Oftalmika Smart Vision Scanner");
            result.put("message", "IoT-пристрій недоступний");

            return ResponseEntity.ok(result);
        }
    }

    @PostMapping("/scan/{visitId}")
    public ResponseEntity<?> scanPatient(@PathVariable Long visitId) {
        Optional<Visit> optionalVisit = visitRepository.findById(visitId);

        if (optionalVisit.isEmpty()) {
            return ResponseEntity.badRequest().body("Прийом не знайдено");
        }

        Visit visit = optionalVisit.get();

        Optional<Patient> optionalPatient = patientRepository.findById(visit.getPatientId());

        if (optionalPatient.isEmpty()) {
            return ResponseEntity.badRequest().body("Пацієнта не знайдено");
        }

        Patient patient = optionalPatient.get();
        String patientFullName = patient.getFirstName() + " " + patient.getLastName();

        IotScanRequest request = new IotScanRequest(
                visit.getVisitId(),
                patient.getPatientId(),
                patientFullName
        );

        IotScanResponse scanResponse;

        try {
            scanResponse = restTemplate.postForObject(
                    iotDeviceUrl + "/api/device/scan",
                    request,
                    IotScanResponse.class
            );
        } catch (Exception exception) {
            return ResponseEntity.status(503).body("IoT-пристрій офлайн. Запустіть контейнер ophthalmica_iot_device.");
        }

        if (scanResponse == null) {
            return ResponseEntity.status(503).body("IoT-пристрій не повернув результати обстеження");
        }

        MedicalRecord medicalRecord = getOrCreateMedicalRecord(visit.getVisitId());

        Measurement measurement = new Measurement();
        measurement.setMedicalRecordId(medicalRecord.getMedicalRecordId());
        measurement.setSphOd(toBigDecimal(scanResponse.getSphOd()));
        measurement.setCylOd(toBigDecimal(scanResponse.getCylOd()));
        measurement.setAxisOd(scanResponse.getAxisOd());
        measurement.setSphOs(toBigDecimal(scanResponse.getSphOs()));
        measurement.setCylOs(toBigDecimal(scanResponse.getCylOs()));
        measurement.setAxisOs(scanResponse.getAxisOs());
        measurement.setIopOd(scanResponse.getIopOd());
        measurement.setIopOs(scanResponse.getIopOs());
        measurement.setPd(scanResponse.getPd());
        measurement.setDeviceSerial(scanResponse.getDeviceSerial());

        measurementRepository.save(measurement);

        return ResponseEntity.ok(scanResponse);
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

    private BigDecimal toBigDecimal(Double value) {
        if (value == null) {
            return null;
        }

        return BigDecimal.valueOf(value);
    }
}