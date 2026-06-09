package ua.nure.kavresiev.optiview.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.nure.kavresiev.optiview.dto.AppointmentRequest;
import ua.nure.kavresiev.optiview.dto.AppointmentSlotResponse;
import ua.nure.kavresiev.optiview.entity.Doctor;
import ua.nure.kavresiev.optiview.entity.Patient;
import ua.nure.kavresiev.optiview.entity.Visit;
import ua.nure.kavresiev.optiview.repository.DoctorRepository;
import ua.nure.kavresiev.optiview.repository.PatientRepository;
import ua.nure.kavresiev.optiview.repository.VisitRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/patient-appointments")
@CrossOrigin(origins = "*")
public class PatientAppointmentController {

    private final VisitRepository visitRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public PatientAppointmentController(
            VisitRepository visitRepository,
            PatientRepository patientRepository,
            DoctorRepository doctorRepository
    ) {
        this.visitRepository = visitRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    @GetMapping("/{patientId}")
    public List<Visit> getPatientAppointments(@PathVariable Long patientId) {
        return visitRepository.findAll()
                .stream()
                .filter(visit -> visit.getPatientId() != null)
                .filter(visit -> visit.getPatientId().equals(patientId))
                .filter(visit -> "PLANNED".equals(visit.getVisitStatus()))
                .toList();
    }

    @GetMapping("/available-slots/{doctorId}/{date}")
    public List<AppointmentSlotResponse> getAvailableSlots(
            @PathVariable Long doctorId,
            @PathVariable String date
    ) {
        LocalDate selectedDate = LocalDate.parse(date);

        List<AppointmentSlotResponse> result = new ArrayList<>();

        LocalTime start = LocalTime.of(8, 0);
        LocalTime end = LocalTime.of(16, 0);

        while (start.isBefore(end)) {
            LocalDateTime slotStart = LocalDateTime.of(selectedDate, start);
            LocalDateTime slotEnd = slotStart.plusMinutes(30);

            boolean busy = isDoctorBusy(doctorId, slotStart, slotEnd);
            String time = start.toString();
            String label = busy ? time + " - зайнято" : time + " - доступно";

            result.add(new AppointmentSlotResponse(time, label, !busy));

            start = start.plusMinutes(30);
        }

        return result;
    }

    @PostMapping("/book")
    public ResponseEntity<?> bookAppointment(@RequestBody AppointmentRequest request) {
        Optional<Patient> optionalPatient = patientRepository.findById(request.getPatientId());
        Optional<Doctor> optionalDoctor = doctorRepository.findById(request.getDoctorId());

        if (optionalPatient.isEmpty()) {
            return ResponseEntity.badRequest().body("Пацієнта не знайдено");
        }

        if (optionalDoctor.isEmpty()) {
            return ResponseEntity.badRequest().body("Лікаря не знайдено");
        }

        LocalDateTime startTime = LocalDateTime.parse(request.getStartTime());
        LocalDateTime endTime = startTime.plusMinutes(30);

        if (!isCorrectWorkTime(startTime)) {
            return ResponseEntity.badRequest().body("Запис доступний тільки з 08:00 до 16:00 з кроком 30 хвилин");
        }

        if (isDoctorBusy(request.getDoctorId(), startTime, endTime)) {
            return ResponseEntity.badRequest().body("Цей час уже зайнятий. Оберіть інший слот");
        }

        Visit visit = new Visit();
        visit.setPatientId(request.getPatientId());
        visit.setDoctorId(request.getDoctorId());
        visit.setStartTime(startTime);
        visit.setEndTime(endTime);
        visit.setVisitStatus("PLANNED");
        visit.setVisitType(request.getVisitType());
        visit.setDiagnosis(null);
        visit.setTreatment(null);
        visit.setVisualAcuity(null);

        Visit savedVisit = visitRepository.save(visit);

        return ResponseEntity.ok(savedVisit);
    }

    private boolean isCorrectWorkTime(LocalDateTime startTime) {
        LocalTime time = startTime.toLocalTime();

        boolean correctMinute = time.getMinute() == 0 || time.getMinute() == 30;
        boolean afterStart = !time.isBefore(LocalTime.of(8, 0));
        boolean beforeEnd = startTime.plusMinutes(30).toLocalTime().compareTo(LocalTime.of(16, 0)) <= 0;

        return correctMinute && afterStart && beforeEnd;
    }

    private boolean isDoctorBusy(Long doctorId, LocalDateTime slotStart, LocalDateTime slotEnd) {
        return visitRepository.findAll()
                .stream()
                .filter(visit -> visit.getDoctorId() != null)
                .filter(visit -> visit.getDoctorId().equals(doctorId))
                .filter(visit -> !"CANCELLED".equals(visit.getVisitStatus()))
                .anyMatch(visit -> slotStart.isBefore(visit.getEndTime()) && slotEnd.isAfter(visit.getStartTime()));
    }
}