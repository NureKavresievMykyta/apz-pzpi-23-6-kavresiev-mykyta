package ua.nure.kavresiev.optiview.controller;

import ua.nure.kavresiev.optiview.dto.VisitDTO;
import ua.nure.kavresiev.optiview.entity.Visit;
import ua.nure.kavresiev.optiview.service.MedicalRecordService;
import ua.nure.kavresiev.optiview.repository.VisitRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/visits")
public class VisitController {

    private final MedicalRecordService medicalRecordService;
    private final VisitRepository visitRepository;

    public VisitController(MedicalRecordService medicalRecordService, VisitRepository visitRepository) {
        this.medicalRecordService = medicalRecordService;
        this.visitRepository = visitRepository;
    }

    @GetMapping
    public List<Visit> getAllVisits() {
        return visitRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Visit> getVisitById(@PathVariable Long id) {
        return visitRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/save")
    public ResponseEntity<?> createVisit(@RequestBody VisitDTO visitDTO) {
        try {
            Visit createdVisit = medicalRecordService.createVisit(visitDTO);
            return ResponseEntity.ok(createdVisit);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateVisit(@PathVariable Long id, @RequestBody VisitDTO visitDTO) {
        try {
            Visit updatedVisit = medicalRecordService.updateVisit(id, visitDTO);
            return ResponseEntity.ok(updatedVisit);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVisit(@PathVariable Long id) {
        try {
            medicalRecordService.deleteVisit(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}