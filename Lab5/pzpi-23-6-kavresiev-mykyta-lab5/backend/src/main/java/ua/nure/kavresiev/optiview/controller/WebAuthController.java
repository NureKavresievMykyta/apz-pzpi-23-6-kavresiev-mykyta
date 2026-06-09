package ua.nure.kavresiev.optiview.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.nure.kavresiev.optiview.dto.DoctorRegisterRequest;
import ua.nure.kavresiev.optiview.dto.LoginRequest;
import ua.nure.kavresiev.optiview.dto.WebAuthResponse;
import ua.nure.kavresiev.optiview.entity.Doctor;
import ua.nure.kavresiev.optiview.entity.User;
import ua.nure.kavresiev.optiview.repository.DoctorRepository;
import ua.nure.kavresiev.optiview.repository.UserRepository;

import java.util.Optional;

@RestController
@RequestMapping("/api/web-auth")
@CrossOrigin(origins = "*")
public class WebAuthController {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;

    public WebAuthController(UserRepository userRepository, DoctorRepository doctorRepository) {
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
    }

    @PostMapping("/register-doctor")
    public ResponseEntity<?> registerDoctor(@RequestBody DoctorRegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body("Користувач з таким логіном вже існує");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPasswordHash(request.getPassword());
        user.setRole("DOCTOR");
        user.setIsActive(false);

        User savedUser = userRepository.save(user);

        Doctor doctor = new Doctor();
        doctor.setDoctorUserId(savedUser.getUserId());
        doctor.setFirstName(request.getFirstName());
        doctor.setLastName(request.getLastName());
        doctor.setSpecialization(request.getSpecialization());
        doctor.setCabinetNumber(request.getCabinetNumber());

        doctorRepository.save(doctor);

        String displayName = request.getFirstName() + " " + request.getLastName();

        return ResponseEntity.ok(new WebAuthResponse(
                savedUser.getUserId(),
                savedUser.getUsername(),
                savedUser.getRole(),
                displayName,
                "Заявку лікаря надіслано адміністратору. Дочекайтеся підтвердження акаунта."
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

        if (!"ADMIN".equals(user.getRole()) && !"DOCTOR".equals(user.getRole())) {
            return ResponseEntity.badRequest().body("Web-кабінет доступний тільки для адміністратора та лікаря");
        }

        if ("DOCTOR".equals(user.getRole()) && Boolean.FALSE.equals(user.getIsActive())) {
            return ResponseEntity.badRequest().body("Акаунт лікаря ще очікує підтвердження адміністратором");
        }

        if (Boolean.FALSE.equals(user.getIsActive())) {
            return ResponseEntity.badRequest().body("Акаунт заблоковано");
        }

        String displayName = user.getUsername();

        if ("ADMIN".equals(user.getRole())) {
            displayName = "Адміністратор";
        }

        if ("DOCTOR".equals(user.getRole())) {
            Optional<Doctor> optionalDoctor = doctorRepository.findById(user.getUserId());

            if (optionalDoctor.isPresent()) {
                Doctor doctor = optionalDoctor.get();
                displayName = doctor.getFirstName() + " " + doctor.getLastName();
            }
        }

        return ResponseEntity.ok(new WebAuthResponse(
                user.getUserId(),
                user.getUsername(),
                user.getRole(),
                displayName,
                "Вхід виконано успішно"
        ));
    }
}