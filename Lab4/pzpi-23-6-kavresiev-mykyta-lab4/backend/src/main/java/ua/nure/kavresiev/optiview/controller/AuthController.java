package ua.nure.kavresiev.optiview.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.nure.kavresiev.optiview.dto.AuthResponse;
import ua.nure.kavresiev.optiview.dto.LoginRequest;
import ua.nure.kavresiev.optiview.dto.RegisterPatientRequest;
import ua.nure.kavresiev.optiview.entity.Patient;
import ua.nure.kavresiev.optiview.entity.User;
import ua.nure.kavresiev.optiview.repository.PatientRepository;
import ua.nure.kavresiev.optiview.repository.UserRepository;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;

    public AuthController(UserRepository userRepository, PatientRepository patientRepository) {
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
    }

    @PostMapping("/register-patient")
    public ResponseEntity<?> registerPatient(@RequestBody RegisterPatientRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body("Користувач з таким логіном вже існує");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPasswordHash(request.getPassword());
        user.setRole("PATIENT");
        user.setIsActive(true);

        User savedUser = userRepository.save(user);

        Patient patient = new Patient();
        patient.setUser(savedUser);
        patient.setFirstName(request.getFirstName());
        patient.setLastName(request.getLastName());
        patient.setBirthDate(LocalDate.parse(request.getBirthDate()));
        patient.setPhone(request.getPhone());
        patient.setEmail(request.getEmail());
        patient.setAddress(request.getAddress());

        Patient savedPatient = patientRepository.save(patient);

        return ResponseEntity.ok(new AuthResponse(
                savedUser.getUserId(),
                savedPatient.getPatientId(),
                savedUser.getUsername(),
                savedUser.getRole(),
                "Пацієнта успішно зареєстровано"
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<User> optionalUser = userRepository.findByUsername(request.getUsername());

        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body("Користувача не знайдено");
        }

        User user = optionalUser.get();

        if (!user.getPasswordHash().equals(request.getPassword())) {
            return ResponseEntity.badRequest().body("Невірний пароль");
        }

        if (!"PATIENT".equals(user.getRole())) {
            return ResponseEntity.badRequest().body("Мобільний застосунок доступний тільки для пацієнтів");
        }

        Optional<Patient> optionalPatient = patientRepository.findByUser(user);

        if (optionalPatient.isEmpty()) {
            return ResponseEntity.badRequest().body("Профіль пацієнта не знайдено");
        }

        Patient patient = optionalPatient.get();

        return ResponseEntity.ok(new AuthResponse(
                user.getUserId(),
                patient.getPatientId(),
                user.getUsername(),
                user.getRole(),
                "Вхід виконано успішно"
        ));
    }
}