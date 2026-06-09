package ua.nure.kavresiev.optiview.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.nure.kavresiev.optiview.entity.Doctor;
import ua.nure.kavresiev.optiview.repository.DoctorRepository;

import java.util.Optional;

@RestController
@RequestMapping("/api/doctor-profile")
@CrossOrigin(origins = "*")
public class DoctorProfileController {

    private final DoctorRepository doctorRepository;

    public DoctorProfileController(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    @GetMapping("/{doctorUserId}")
    public ResponseEntity<?> getDoctorProfile(@PathVariable Long doctorUserId) {
        Optional<Doctor> optionalDoctor = doctorRepository.findById(doctorUserId);

        if (optionalDoctor.isEmpty()) {
            return ResponseEntity.badRequest().body("Лікаря не знайдено");
        }

        return ResponseEntity.ok(optionalDoctor.get());
    }

    @PutMapping("/{doctorUserId}")
    public ResponseEntity<?> updateDoctorProfile(@PathVariable Long doctorUserId, @RequestBody Doctor updatedDoctor) {
        Optional<Doctor> optionalDoctor = doctorRepository.findById(doctorUserId);

        if (optionalDoctor.isEmpty()) {
            return ResponseEntity.badRequest().body("Лікаря не знайдено");
        }

        Doctor doctor = optionalDoctor.get();

        doctor.setFirstName(updatedDoctor.getFirstName());
        doctor.setLastName(updatedDoctor.getLastName());
        doctor.setSpecialization(updatedDoctor.getSpecialization());
        doctor.setCabinetNumber(updatedDoctor.getCabinetNumber());

        Doctor savedDoctor = doctorRepository.save(doctor);

        return ResponseEntity.ok(savedDoctor);
    }
}