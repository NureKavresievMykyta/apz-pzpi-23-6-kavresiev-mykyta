package ua.nure.kavresiev.optiview.controller;

import ua.nure.kavresiev.optiview.entity.Diagnosis;
import ua.nure.kavresiev.optiview.repository.DiagnosisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diagnoses")
public class DiagnosisController {

    @Autowired
    private DiagnosisRepository repository;

    @GetMapping
    public List<Diagnosis> getAll() {
        return repository.findAll();
    }

    @PostMapping
    public Diagnosis create(@RequestBody Diagnosis diagnosis) {
        return repository.save(diagnosis);
    }

    @PutMapping("/{id}")
    public Diagnosis updateDiagnosis(@PathVariable Long id, @RequestBody Diagnosis details) {
        Diagnosis d = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Diagnosis not found with id: " + id));

        d.setDiagnosisCode(details.getDiagnosisCode());
        d.setDiagnosisName(details.getDiagnosisName());
        d.setDiagnosisDescription(details.getDiagnosisDescription());

        return repository.save(d);
    }

    @DeleteMapping("/{id}")
    public void deleteDiagnosis(@PathVariable Long id) {
        repository.deleteById(id);
    }
}